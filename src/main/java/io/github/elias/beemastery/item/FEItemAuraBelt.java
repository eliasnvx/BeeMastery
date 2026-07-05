package io.github.elias.beemastery.item;

import io.github.elias.beemastery.effect.AuraActions;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds up to {@link #SLOTS} Aura Charms and is the only thing that makes a charm actually
 * pulse — a bare charm sitting in the inventory does nothing on its own. This is the balance
 * knob for auras: without the belt a player could carry all five charms and get every aura
 * for free, so charms only work once socketed here, and the belt caps how many run at once.
 *
 * <p>Socketing has no GUI: hold the belt in one hand and a charm in the other and right-click
 * to store it (main/off-hand order doesn't matter). Sneak + right-click with an empty other
 * hand pops the most recently socketed charm back out.
 */
public class FEItemAuraBelt extends Item {

    public static final int SLOTS = 2;
    private static final String TAG_CHARMS = "Charms";

    public FEItemAuraBelt() {
        super(new Properties().stacksTo(1));
    }

    private static ListTag getCharms(ItemStack belt) {
        return belt.getOrCreateTag().getList(TAG_CHARMS, Tag.TAG_COMPOUND);
    }

    private static void setCharms(ItemStack belt, ListTag list) {
        belt.getOrCreateTag().put(TAG_CHARMS, list);
    }

    public static List<ItemStack> getSocketed(ItemStack belt) {
        ListTag list = getCharms(belt);
        List<ItemStack> out = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            out.add(ItemStack.of(list.getCompound(i)));
        }
        return out;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack belt = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(belt);
        }

        InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack other = player.getItemInHand(otherHand);
        ListTag list = getCharms(belt);

        if (other.isEmpty()) {
            if (player.isShiftKeyDown()) {
                if (list.isEmpty()) {
                    player.displayClientMessage(Component.translatable("beemastery.belt.empty"), true);
                    return InteractionResultHolder.pass(belt);
                }
                CompoundTag removedTag = list.getCompound(list.size() - 1);
                ItemStack removed = ItemStack.of(removedTag);
                list.remove(list.size() - 1);
                setCharms(belt, list);
                if (!player.getInventory().add(removed)) {
                    player.drop(removed, false);
                }
                player.displayClientMessage(Component.translatable("beemastery.belt.removed", removed.getHoverName()), true);
                return InteractionResultHolder.success(belt);
            }
            player.displayClientMessage(statusMessage(list), true);
            return InteractionResultHolder.pass(belt);
        }

        if (!(other.getItem() instanceof FEItemAuraCharm)) {
            return InteractionResultHolder.pass(belt);
        }

        if (list.size() >= SLOTS) {
            player.displayClientMessage(Component.translatable("beemastery.belt.full"), true);
            return InteractionResultHolder.fail(belt);
        }

        ItemStack toStore = other.copyWithCount(1);
        CompoundTag stored = new CompoundTag();
        toStore.save(stored);
        list.add(stored);
        setCharms(belt, list);
        other.shrink(1);
        player.displayClientMessage(Component.translatable("beemastery.belt.socketed", toStore.getHoverName(), list.size(), SLOTS), true);
        return InteractionResultHolder.success(belt);
    }

    private static Component statusMessage(ListTag list) {
        if (list.isEmpty()) {
            return Component.translatable("beemastery.belt.empty");
        }
        StringBuilder names = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                names.append(", ");
            }
            ItemStack charm = ItemStack.of(list.getCompound(i));
            int remaining = charm.getMaxDamage() - charm.getDamageValue();
            names.append(charm.getHoverName().getString())
                    .append(" (").append(remaining).append('/').append(charm.getMaxDamage()).append(')');
        }
        return Component.translatable("beemastery.belt.status", names.toString());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide || !(entity instanceof Player player)) {
            return;
        }
        ListTag list = getCharms(stack);
        if (list.isEmpty()) {
            return;
        }

        boolean changed = false;
        for (int i = list.size() - 1; i >= 0; i--) {
            ItemStack charmStack = ItemStack.of(list.getCompound(i));
            if (!(charmStack.getItem() instanceof FEItemAuraCharm charmItem)) {
                continue;
            }
            FEEnumAura type = charmItem.getType();
            if (level.getGameTime() % type.charmInterval != 0) {
                continue;
            }

            double r = type.personalRadius;
            AABB area = player.getBoundingBox().inflate(r, r / 2.0, r);
            AuraActions.pulse(type, level, area);

            charmStack.hurtAndBreak(1, player, p -> {
            });
            changed = true;
            if (charmStack.isEmpty()) {
                list.remove(i);
                player.displayClientMessage(Component.translatable("beemastery.belt.charm_broke"), true);
            } else {
                CompoundTag updated = new CompoundTag();
                charmStack.save(updated);
                list.set(i, updated);
            }
        }

        if (changed) {
            setCharms(stack, list);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        List<ItemStack> socketed = getSocketed(stack);
        if (socketed.isEmpty()) {
            tooltip.add(Component.translatable("beemastery.belt.empty").withStyle(ChatFormatting.DARK_GRAY));
        } else {
            for (ItemStack charm : socketed) {
                int remaining = charm.getMaxDamage() - charm.getDamageValue();
                tooltip.add(Component.literal("- ").append(charm.getHoverName()).withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.literal("  ")
                        .append(Component.translatable("beemastery.charm.charge", remaining, charm.getMaxDamage()))
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }
}
