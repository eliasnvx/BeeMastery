package io.github.elias.beemastery.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import io.github.elias.beemastery.hive.HiveStackContainer;
import io.github.elias.beemastery.hive.HiveTier;
import io.github.elias.beemastery.hive.PortableHiveMenu;
import io.github.elias.beemastery.registry.ModArmorMaterials;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * A bee house worn on the back (chest slot). The bees work wherever the player carries them —
 * see {@link io.github.elias.beemastery.hive.PortableHiveTicker}.
 *
 * <p>On its own it gives no protection. Crafting it together with a chestplate builds that
 * chestplate in (NBT {@value #TAG_ARMOR}): the hive then takes the chestplate's armour,
 * toughness, enchantments and durability, and renders as that chestplate. When the built-in
 * armour breaks, only the armour is lost — the hive and its bees are kept.
 */
public class FEItemPortableHive extends ArmorItem {

    /** The chestplate built into a portable hive (absent on a plain hive), as a saved item stack. */
    public static final String TAG_ARMOR = "hive_armor";
    private static final String TAG_ENCHANTMENTS = "Enchantments";
    private static final String TAG_DAMAGE = "Damage";

    public FEItemPortableHive() {
        super(ModArmorMaterials.PORTABLE_HIVE, ArmorItem.Type.CHESTPLATE, new Properties().stacksTo(1));
    }

    // --- built-in armour ------------------------------------------------------------------------

    public static ItemStack getArmor(ItemStack hive) {
        CompoundTag tag = hive.getTag();
        return tag != null && tag.contains(TAG_ARMOR, CompoundTag.TAG_COMPOUND) ? ItemStack.of(tag.getCompound(TAG_ARMOR)) : ItemStack.EMPTY;
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
        CompoundTag tag = out.getOrCreateTag();
        tag.put(TAG_ARMOR, armor.save(new CompoundTag()));
        ListTag enchantments = armor.getEnchantmentTags();
        if (enchantments.isEmpty()) {
            tag.remove(TAG_ENCHANTMENTS);
        } else {
            tag.put(TAG_ENCHANTMENTS, enchantments.copy());
        }
        if (armor.isDamageableItem()) {
            out.setDamageValue(armor.getDamageValue());
        }
        return out;
    }

    /** The chestplate as it is now (current wear and enchantments), for taking it back out. */
    public static ItemStack extractArmor(ItemStack hive) {
        ItemStack armor = getArmor(hive).copy();
        if (armor.isDamageableItem()) {
            armor.setDamageValue(hive.getDamageValue());
        }
        ListTag enchantments = hive.getEnchantmentTags();
        if (!enchantments.isEmpty()) {
            armor.getOrCreateTag().put(TAG_ENCHANTMENTS, enchantments.copy());
        } else if (armor.getTag() != null) {
            armor.getTag().remove(TAG_ENCHANTMENTS);
        }
        return armor;
    }

    /** Remove the built-in armour from {@code hive} in place. */
    public static void stripArmor(ItemStack hive) {
        CompoundTag tag = hive.getTag();
        if (tag == null) {
            return;
        }
        tag.remove(TAG_ARMOR);
        tag.remove(TAG_ENCHANTMENTS);
        tag.remove(TAG_DAMAGE);
        if (tag.isEmpty()) {
            hive.setTag(null);
        }
    }

    /** Armour, toughness etc. are the built-in chestplate's; a plain hive has none. */
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        ItemStack armor = getArmor(stack);
        if (armor.isEmpty()) {
            return super.getAttributeModifiers(slot, stack);
        }
        return slot == EquipmentSlot.CHEST ? armor.getAttributeModifiers(EquipmentSlot.CHEST) : ImmutableMultimap.of();
    }

    /** Durability is the built-in chestplate's; a plain hive has no durability of its own. */
    @Override
    public boolean isDamageable(ItemStack stack) {
        return getArmor(stack).isDamageableItem();
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        ItemStack armor = getArmor(stack);
        return armor.isEmpty() ? 0 : armor.getMaxDamage();
    }

    /**
     * Armour that would break takes the hit alone: the chestplate is removed and the hive survives.
     *
     * <p>On 1.20.1 this needs no further trick: right after this hook vanilla's
     * {@code ItemStack.hurt} re-checks {@code isDamageableItem()}, which is false for the hive
     * once its chestplate is gone, so the stack is neither damaged further nor destroyed.
     */
    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        if (!isArmored(stack)) {
            return 0;  // a plain hive has no durability of its own
        }
        if (stack.getDamageValue() + amount >= stack.getMaxDamage()) {
            ItemStack brokenArmor = getArmor(stack);
            stripArmor(stack);
            playBreakEffects(entity, brokenArmor);
            return 0;
        }
        return amount;
    }

    /** Break sound and particles of the chestplate itself (vanilla's break event would show the hive's). */
    private static void playBreakEffects(LivingEntity entity, ItemStack broken) {
        if (entity.level() instanceof ServerLevel level) {
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_BREAK,
                    entity.getSoundSource(), 0.8f, 0.8f + level.random.nextFloat() * 0.4f);
            level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, broken),
                    entity.getX(), entity.getY(0.6), entity.getZ(), 5, 0.2, 0.1, 0.2, 0.05);
        }
    }

    /**
     * Render the built-in chestplate; a plain hive shows just its leather straps
     * ({@code beemastery:textures/models/armor/portable_hive_layer_1.png}, from the material).
     */
    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        ItemStack armor = getArmor(stack);
        if (!(armor.getItem() instanceof ArmorItem chest)) {
            return null;
        }
        String custom = chest.getArmorTexture(armor, entity, slot, type);
        if (custom != null) {
            return custom;
        }
        // what vanilla's HumanoidArmorLayer would use for that chestplate (chest slot = outer layer 1)
        String texture = chest.getMaterial().getName();
        String domain = "minecraft";
        int colon = texture.indexOf(':');
        if (colon != -1) {
            domain = texture.substring(0, colon);
            texture = texture.substring(colon + 1);
        }
        return String.format(Locale.ROOT, "%s:textures/models/armor/%s_layer_1%s.png", domain, texture, type == null ? "" : "_" + type);
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
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
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
