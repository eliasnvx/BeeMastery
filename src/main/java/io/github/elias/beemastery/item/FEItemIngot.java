package io.github.elias.beemastery.item;

import forestry.core.items.ItemForestry;
import net.minecraft.world.item.ItemStack;

/**
 * Unlike most Bee Mastery items, the ingot uses a hand-drawn, pre-colored texture per
 * type instead of a tinted base texture, so it does not implement {@code IColoredItem}.
 */
public class FEItemIngot extends ItemForestry {
    private final FEEnumIngot type;

    public FEItemIngot(FEEnumIngot type) {
        this.type = type;
    }

    public FEEnumIngot getType() {
        return type;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return type == FEEnumIngot.LEGENDARY || type == FEEnumIngot.DRACONIC || super.isFoil(stack);
    }
}
