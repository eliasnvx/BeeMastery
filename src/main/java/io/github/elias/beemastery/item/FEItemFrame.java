package io.github.elias.beemastery.item;

import forestry.core.items.ItemForestry;
import forestry.core.items.definitions.IColoredItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class FEItemFrame extends ItemForestry implements IColoredItem {
    private final FEEnumFrame type;

    public FEItemFrame(FEEnumFrame type) {
        super(new Properties().stacksTo(1).durability(type.maxDamage));
        this.type = type;
    }

    public FEEnumFrame getType() {
        return type;
    }

    @Override
    public int getColorFromItemStack(ItemStack itemstack, int tintIndex) {
        return type.color;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        
        // Показываем информацию всегда
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("forestryextras.frame.production", 
            formatModifier(type.productionModifier)).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("forestryextras.frame.lifespan", 
            formatModifier(type.lifespanModifier)).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("forestryextras.frame.mutation", 
            formatModifier(type.mutationModifier)).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("forestryextras.frame.territory", 
            formatModifier(type.territoryModifier)).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("forestryextras.frame.durability", 
            String.valueOf(type.maxDamage)).withStyle(ChatFormatting.GRAY));
    }

    private String formatModifier(float modifier) {
        if (modifier == 1.0F) {
            return "1.0x";
        } else if (modifier > 1.0F) {
            return String.format("+%.1fx", modifier);
        } else {
            return String.format("%.1fx", modifier);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return type == FEEnumFrame.LEGENDARY || type == FEEnumFrame.DRACONIC || super.isFoil(stack);
    }
}
