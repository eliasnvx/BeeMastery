package io.github.elias.beemastery.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Unlike most Bee Mastery items, the ingot uses a hand-drawn, pre-colored texture per
 * type instead of a tinted base texture, so it does not implement {@code IColoredItem}.
 */
public class FEItemIngot extends Item {
    private final FEEnumIngot type;

    public FEItemIngot(FEEnumIngot type, Item.Properties properties) {
        super(properties);
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
