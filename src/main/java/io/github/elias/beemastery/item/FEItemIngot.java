package io.github.elias.beemastery.item;

import forestry.core.items.ItemForestry;
import forestry.core.items.definitions.IColoredItem;
import net.minecraft.world.item.ItemStack;

public class FEItemIngot extends ItemForestry implements IColoredItem {
    private final FEEnumIngot type;

    public FEItemIngot(FEEnumIngot type) {
        this.type = type;
    }

    public FEEnumIngot getType() {
        return type;
    }

    @Override
    public int getColorFromItemStack(ItemStack itemstack, int tintIndex) {
        return type.color;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return type == FEEnumIngot.LEGENDARY || type == FEEnumIngot.DRACONIC || super.isFoil(stack);
    }
}
