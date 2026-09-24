package io.github.elias.beemastery.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FEItemStick extends Item {
    private final FEEnumStick type;

    public FEItemStick(FEEnumStick type, Item.Properties properties) {
        super(properties);
        this.type = type;
    }

    public FEEnumStick getType() {
        return type;
    }

    public int getColor(int tintIndex) {
        return type.color;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return type == FEEnumStick.LEGENDARY || super.isFoil(stack);
    }
}
