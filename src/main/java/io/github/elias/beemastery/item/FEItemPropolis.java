package io.github.elias.beemastery.item;

import forestry.core.items.ItemForestry;
import forestry.core.items.definitions.IColoredItem;
import net.minecraft.world.item.ItemStack;

public class FEItemPropolis extends ItemForestry implements IColoredItem {
    private final FEEnumPropolis type;

    public FEItemPropolis(FEEnumPropolis type) {
        this.type = type;
    }

    public FEEnumPropolis getType() {
        return type;
    }

    @Override
    public int getColorFromItemStack(ItemStack itemstack, int tintIndex) {
        return type.color;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return type == FEEnumPropolis.LEGENDARY || type == FEEnumPropolis.DRACONIC || super.isFoil(stack);
    }
}
