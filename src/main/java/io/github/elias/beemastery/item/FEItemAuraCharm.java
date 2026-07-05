package io.github.elias.beemastery.item;

import forestry.core.items.ItemForestry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * "Aura in a bottle": a charm bound to a single {@link FEEnumAura}.
 *
 * <p>A bare charm does nothing by itself — it must be socketed into an {@link FEItemAuraBelt}
 * to pulse. Charge is the item's durability: when it runs out the charm breaks inside the belt.
 * It is not infinite by design — you re-craft it from the bee's elite drops.
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
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        int remaining = stack.getMaxDamage() - stack.getDamageValue();
        tooltip.add(Component.translatable("beemastery.charm.aura",
                Component.translatable("beemastery.aura." + type.getSerializedName()))
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("beemastery.charm.charge", remaining, stack.getMaxDamage())
                .withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("beemastery.charm.needs_belt")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }
}
