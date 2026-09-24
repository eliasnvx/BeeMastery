package io.github.elias.beemastery.item;

import io.github.elias.beemastery.hive.HiveStackContainer;
import io.github.elias.beemastery.hive.HiveTier;
import io.github.elias.beemastery.hive.PortableHiveMenu;
import io.github.elias.beemastery.registry.ModArmorMaterials;
import io.github.elias.beemastery.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;

/**
 * A bee house worn on the back (chest slot). The bees work wherever the player carries them —
 * see {@link io.github.elias.beemastery.hive.PortableHiveTicker}.
 *
 * <p>On its own it gives no protection. Crafting it together with a chestplate builds that
 * chestplate in: the hive then takes the chestplate's armour, toughness, enchantments and
 * durability, and renders as that chestplate. When the built-in armour breaks, only the armour
 * is lost — the hive and its bees are kept.
 */
public class FEItemPortableHive extends ArmorItem {

    public FEItemPortableHive(Item.Properties properties) {
        super(ModArmorMaterials.PORTABLE_HIVE, ArmorItem.Type.CHESTPLATE, properties.stacksTo(1));
    }

    // --- built-in armour ------------------------------------------------------------------------

    public static ItemStack getArmor(ItemStack hive) {
        return hive.getOrDefault(ModDataComponents.HIVE_ARMOR.get(), ItemContainerContents.EMPTY).copyOne();
    }

    public static boolean isArmored(ItemStack hive) {
        return !getArmor(hive).isEmpty();
    }

    public static boolean isChestplate(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem armor
                && armor.getType() == ArmorItem.Type.CHESTPLATE
                && !(stack.getItem() instanceof FEItemPortableHive);
    }

    /** Copy of {@code hive} with {@code chestplate} built in. */
    public static ItemStack withArmor(ItemStack hive, ItemStack chestplate) {
        ItemStack out = hive.copyWithCount(1);
        ItemStack armor = chestplate.copyWithCount(1);
        out.set(ModDataComponents.HIVE_ARMOR.get(), ItemContainerContents.fromItems(List.of(armor)));
        out.set(DataComponents.ATTRIBUTE_MODIFIERS,
                armor.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, armor.getItem().getDefaultAttributeModifiers()));
        out.set(DataComponents.ENCHANTMENTS, armor.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY));
        if (armor.isDamageableItem()) {
            out.set(DataComponents.MAX_DAMAGE, armor.getMaxDamage());
            out.set(DataComponents.DAMAGE, armor.getDamageValue());
        }
        return out;
    }

    /** The chestplate as it is now (current wear and enchantments), for taking it back out. */
    public static ItemStack extractArmor(ItemStack hive) {
        ItemStack armor = getArmor(hive).copy();
        if (armor.isDamageableItem()) {
            armor.setDamageValue(hive.getDamageValue());
        }
        armor.set(DataComponents.ENCHANTMENTS, hive.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY));
        return armor;
    }

    /** Remove the built-in armour from {@code hive} in place. */
    public static void stripArmor(ItemStack hive) {
        hive.remove(ModDataComponents.HIVE_ARMOR.get());
        hive.remove(DataComponents.ATTRIBUTE_MODIFIERS);
        hive.remove(DataComponents.ENCHANTMENTS);
        hive.remove(DataComponents.MAX_DAMAGE);
        hive.remove(DataComponents.DAMAGE);
    }

    /**
     * Armour that would break takes the hit alone: the chestplate is removed and the hive survives.
     *
     * <p>Vanilla's hurtAndBreak destroys the stack right after this hook if damage >= max damage,
     * and a hive without its chestplate has max damage 0 — so on breaking, the max damage is left
     * in place for a moment with the damage reset to 0, and {@link #inventoryTick} tidies it away.
     */
    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
        if (!isArmored(stack)) {
            return 0;  // a plain hive has no durability of its own
        }
        if (stack.getDamageValue() + amount >= stack.getMaxDamage()) {
            Item brokenArmor = getArmor(stack).getItem();
            int maxDamage = stack.getMaxDamage();
            stripArmor(stack);
            stack.set(DataComponents.MAX_DAMAGE, maxDamage);
            stack.set(DataComponents.DAMAGE, 0);
            entity.onEquippedItemBroken(brokenArmor, EquipmentSlot.CHEST);  // break sound + particles
            return 0;
        }
        return amount;
    }

    /** Drops the durability left behind by a chestplate that just broke (see {@link #damageItem}). */
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && !isArmored(stack) && stack.has(DataComponents.MAX_DAMAGE)) {
            stack.remove(DataComponents.MAX_DAMAGE);
            stack.remove(DataComponents.DAMAGE);
        }
    }

    /** Render the built-in chestplate; a plain hive shows just its leather straps. */
    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        ItemStack armor = getArmor(stack);
        if (armor.getItem() instanceof ArmorItem chest) {
            List<ArmorMaterial.Layer> layers = chest.getMaterial().value().layers();
            if (!layers.isEmpty()) {
                return layers.getFirst().texture(innerModel);
            }
        }
        return super.getArmorTexture(stack, entity, slot, layer, innerModel);
    }

    // --- using it ----------------------------------------------------------------------------------

    /** Right-click opens the hive; sneak + right-click puts it on (vanilla armour swap). */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            return super.use(level, player, hand);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            PortableHiveMenu.open(serverPlayer, hand == InteractionHand.MAIN_HAND
                    ? PortableHiveMenu.Source.MAIN_HAND : PortableHiveMenu.Source.OFF_HAND);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }

    /** The legendary hive shimmers like the legendary propolis it's upgraded with. */
    @Override
    public boolean isFoil(ItemStack stack) {
        return HiveTier.of(stack) == HiveTier.LEGENDARY || super.isFoil(stack);
    }

    /** "Reinforced Portable Hive" etc. for upgraded hives. */
    @Override
    public Component getName(ItemStack stack) {
        HiveTier tier = HiveTier.of(stack);
        return tier == HiveTier.BASIC ? super.getName(stack)
                : Component.translatable(getDescriptionId() + "." + tier.name).withStyle(tier.color);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        HiveStackContainer inv = new HiveStackContainer(stack);
        HiveTier tier = HiveTier.of(stack);
        tooltip.add(Component.translatable("beemastery.hive.tier_info",
                Math.round(tier.productionMultiplier() * 100), tier.moduleSlots()).withStyle(ChatFormatting.GOLD));
        tooltip.add(line("beemastery.hive.queen", inv.getQueen()));
        tooltip.add(line("beemastery.hive.drone", inv.getDrone()));
        for (FEEnumHiveModule module : inv.getModules()) {
            tooltip.add(Component.literal(" + ").append(Component.translatable("item.beemastery.hive_module_" + module.name))
                    .withStyle(ChatFormatting.DARK_AQUA));
        }
        ItemStack armor = getArmor(stack);
        if (!armor.isEmpty()) {
            tooltip.add(Component.translatable("beemastery.hive.armor", armor.getHoverName()).withStyle(ChatFormatting.BLUE));
        } else {
            tooltip.add(Component.translatable("beemastery.hive.hint_armor").withStyle(ChatFormatting.DARK_GRAY));
        }
        tooltip.add(Component.translatable("beemastery.hive.hint_use").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("beemastery.hive.hint_worn", Component.keybind("key.beemastery.open_hive"))
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    private static Component line(String key, ItemStack bee) {
        Component name = bee.isEmpty()
                ? Component.translatable("beemastery.hive.empty").withStyle(ChatFormatting.DARK_GRAY)
                : bee.getHoverName();
        return Component.translatable(key, name).withStyle(ChatFormatting.GRAY);
    }
}
