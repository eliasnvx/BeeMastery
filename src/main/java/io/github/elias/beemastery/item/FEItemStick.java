package io.github.elias.beemastery.item;

import forestry.core.items.ItemForestry;
import forestry.core.items.definitions.IColoredItem;
import net.minecraft.world.item.ItemStack;

public class FEItemStick extends ItemForestry implements IColoredItem {
    private final FEEnumStick type;

    public FEItemStick(FEEnumStick type) {
        this.type = type;
    }

    public FEEnumStick getType() {
        return type;
    }

    @Override
    public int getColorFromItemStack(ItemStack itemstack, int tintIndex) {
        return type.color;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return type == FEEnumStick.LEGENDARY || super.isFoil(stack);
    }
}
