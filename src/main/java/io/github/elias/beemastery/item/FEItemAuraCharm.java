package io.github.elias.beemastery.item;

import forestry.core.items.ItemForestry;
import io.github.elias.beemastery.effect.AuraActions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * "Aura in a bottle": a charm bound to a single {@link FEEnumAura}.
 *
 * <p>While carried, it periodically applies its aura's pulse to a small region around the
 * holder (reusing {@link AuraActions}, the same logic apiaries use) and spends one point of
 * charge each pulse. Charge is the item's durability: when it runs out the charm breaks. It
 * is not infinite by design — you re-craft it from the bee's elite drops.
 *
 * <p>Unlike most Bee Mastery items, this one uses a hand-drawn, pre-colored texture per
 * aura instead of a tinted base texture, so it does not implement {@code IColoredItem}.
 */
public class FEItemAuraCharm extends ItemForestry {

    private final FEEnumAura type;

    public FEItemAuraCharm(FEEnumAura type) {
        super(new Properties().stacksTo(1).durability(type.charmCharge).setNoRepair());
        this.type = type;
    }

    public FEEnumAura getType() {
        return type;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return type == FEEnumAura.DRACONIC || type == FEEnumAura.LEGENDARY || super.isFoil(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide || !(entity instanceof Player player)) {
            return;
        }
        // Pulse on the charm's own cadence; each pulse costs one charge.
        if (level.getGameTime() % type.charmInterval != 0) {
            return;
        }

        double r = type.personalRadius;
        AABB area = player.getBoundingBox().inflate(r, r / 2.0, r);
        AuraActions.pulse(type, level, area);

        stack.hurtAndBreak(1, player, p -> {
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        int remaining = stack.getMaxDamage() - stack.getDamageValue();
        tooltip.add(Component.translatable("beemastery.charm.aura",
                Component.translatable("beemastery.aura." + type.getSerializedName()))
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("beemastery.charm.charge", remaining, stack.getMaxDamage())
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
