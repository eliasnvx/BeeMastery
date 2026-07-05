package io.github.elias.beemastery.item;

import forestry.core.items.ItemForestry;
import net.minecraft.world.item.ItemStack;

/**
 * Unlike most Bee Mastery items, the nugget uses a hand-drawn, pre-colored texture per
 * type instead of a tinted base texture, so it does not implement {@code IColoredItem}.
 */
public class FEItemNugget extends ItemForestry {
    private final FEEnumNugget type;

    public FEItemNugget(FEEnumNugget type) {
        this.type = type;
    }

    public FEEnumNugget getType() {
        return type;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return type == FEEnumNugget.LEGENDARY || type == FEEnumNugget.DRACONIC || super.isFoil(stack);
    }
}
