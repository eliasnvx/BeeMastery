package io.github.elias.beemastery.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Unlike most Bee Mastery items, the nugget uses a hand-drawn, pre-colored texture per
 * type instead of a tinted base texture, so it does not implement {@code IColoredItem}.
 */
public class FEItemNugget extends Item {
    private final FEEnumNugget type;

    public FEItemNugget(FEEnumNugget type, Item.Properties properties) {
        super(properties);
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
