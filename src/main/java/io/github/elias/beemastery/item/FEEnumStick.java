package io.github.elias.beemastery.item;

import forestry.api.core.IItemSubtype;
import net.minecraft.util.StringRepresentable;

import java.awt.*;
import java.util.Locale;

public enum FEEnumStick implements StringRepresentable, IItemSubtype {

    COAL(new Color(0x323232)),
    IRON(new Color(0xFFFFCC)),
    GOLD(new Color(0xFFFF66)),
    DIAMOND(new Color(0x99FFFF)),
    EMERALD(new Color(0x99FF33)),
    OBSIDIAN(new Color(0x999966)),
    DRACONIC(new Color(0xFF0000)),
    LEGENDARY(new Color(0x0066FF)),
    REINFORCED(new Color(0xCCCC99)),
    WITHERIA(new Color(0x333333)),
    MUTATED_IRON(new Color(0x99CC00));

    public static final FEEnumStick[] VALUES = values();

    public final String name;
    public final int color;

    FEEnumStick(Color color) {
        this.name = toString().toLowerCase(Locale.ENGLISH);
        this.color = color.getRGB();
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static FEEnumStick get(int meta) {
        if (meta >= VALUES.length) {
            meta = 0;
        }
        return VALUES[meta];
    }
}
