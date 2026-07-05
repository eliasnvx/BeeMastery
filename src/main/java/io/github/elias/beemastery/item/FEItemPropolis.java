package io.github.elias.beemastery.item;

import forestry.core.items.ItemForestry;
import net.minecraft.world.item.ItemStack;

/**
 * Unlike most Bee Mastery items, propolis uses a hand-drawn, pre-colored texture per
 * type instead of a tinted base texture, so it does not implement {@code IColoredItem}.
 */
public class FEItemPropolis extends ItemForestry {
    private final FEEnumPropolis type;

    public FEItemPropolis(FEEnumPropolis type) {
        this.type = type;
    }

    public FEEnumPropolis getType() {
        return type;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return type == FEEnumPropolis.LEGENDARY || type == FEEnumPropolis.DRACONIC || super.isFoil(stack);
    }
}
