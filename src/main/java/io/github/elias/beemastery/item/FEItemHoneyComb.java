package io.github.elias.beemastery.item;

import forestry.core.items.ItemForestry;
import forestry.core.items.definitions.IColoredItem;
import net.minecraft.world.item.ItemStack;

public class FEItemHoneyComb extends ItemForestry implements IColoredItem {
    private final FEEnumHoneyComb type;

    public FEItemHoneyComb(FEEnumHoneyComb type) {
        this.type = type;
    }

    public FEEnumHoneyComb getType() {
        return type;
    }

    @Override
    public int getColorFromItemStack(ItemStack itemstack, int tintIndex) {
        FEEnumHoneyComb honeyComb = this.type;

        if (tintIndex == 1) {
            return honeyComb.primaryColor;
        } else {
            return honeyComb.secondaryColor;
        }
    }
}
