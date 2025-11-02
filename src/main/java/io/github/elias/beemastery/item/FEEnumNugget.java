package io.github.elias.beemastery.item;

import forestry.api.core.IItemSubtype;
import net.minecraft.util.StringRepresentable;

import java.awt.*;
import java.util.Locale;

public enum FEEnumNugget implements StringRepresentable, IItemSubtype {

    DRACONIC(new Color(0xFF0000)),
    LEGENDARY(new Color(0x0066FF)),
    REINFORCED(new Color(0xCCCC99)),
    WITHERIA(new Color(0x333333)),
    MUTATED_IRON(new Color(0x99CC00));

    public static final FEEnumNugget[] VALUES = values();

    public final String name;
    public final int color;

    FEEnumNugget(Color color) {
        this.name = toString().toLowerCase(Locale.ENGLISH);
        this.color = color.getRGB();
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static FEEnumNugget get(int meta) {
        if (meta >= VALUES.length) {
            meta = 0;
        }
        return VALUES[meta];
    }
}
