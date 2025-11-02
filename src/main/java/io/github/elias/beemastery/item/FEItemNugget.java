package io.github.elias.beemastery.item;

import forestry.core.items.ItemForestry;
import forestry.core.items.definitions.IColoredItem;
import net.minecraft.world.item.ItemStack;

public class FEItemNugget extends ItemForestry implements IColoredItem {
    private final FEEnumNugget type;

    public FEItemNugget(FEEnumNugget type) {
        this.type = type;
    }

    public FEEnumNugget getType() {
        return type;
    }

    @Override
    public int getColorFromItemStack(ItemStack itemstack, int tintIndex) {
        return type.color;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return type == FEEnumNugget.LEGENDARY || type == FEEnumNugget.DRACONIC || super.isFoil(stack);
    }
}
