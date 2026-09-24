package io.github.elias.beemastery.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Unlike most Bee Mastery items, the propolis uses a hand-drawn, pre-colored texture per
 * type instead of a tinted base texture, so it does not implement {@code IColoredItem}.
 */
public class FEItemPropolis extends Item {
    private final FEEnumPropolis type;

    public FEItemPropolis(FEEnumPropolis type, Item.Properties properties) {
        super(properties);
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
