package io.github.elias.beemastery.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FEItemHoneyComb extends Item {
    private final FEEnumHoneyComb type;

    public FEItemHoneyComb(FEEnumHoneyComb type, Item.Properties properties) {
        super(properties);
        this.type = type;
    }

    public FEEnumHoneyComb getType() {
        return type;
    }

    public int getColor(int tintIndex) {
        FEEnumHoneyComb honeyComb = this.type;

        if (tintIndex == 1) {
            return honeyComb.primaryColor;
        } else {
            return honeyComb.secondaryColor;
        }
    }
}
