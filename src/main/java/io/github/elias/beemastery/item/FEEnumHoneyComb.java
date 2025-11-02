package io.github.elias.beemastery.item;

import forestry.api.core.IBlockSubtype;
import forestry.api.core.IItemSubtype;
import net.minecraft.util.StringRepresentable;

import java.awt.*;
import java.util.Locale;

public enum FEEnumHoneyComb implements StringRepresentable, IItemSubtype, IBlockSubtype {

    DRACONIC(new Color(0x990000), new Color(0xFFFFCC)),
    LEGENDARY(new Color(0x0000CD), new Color(0xFFFFCC)),
    REINFORCED(new Color(0xCCCC99), new Color(0xFFFFCC)),
    WITHERIA(new Color(0x000000), new Color(0xFFFFCC)),
    MUTATED(new Color(0x99CC00), new Color(0xFFFFCC)),
    CLAYIOUS(new Color(0xB0C4DE), new Color(0xF5F5F5)),
    POTATO(new Color(0xEEE8AA), new Color(0xF0E68C)),
    CARROT(new Color(0xFFA500), new Color(0xFFA500)),
    PIG(new Color(0xFF69B4), new Color(0xFFB6C1)),
    COW(new Color(0x8B4513), new Color(0xE9967A)),
    SHEEP(new Color(0xFFFFFF), new Color(0xE9967A));

    public static final FEEnumHoneyComb[] VALUES = values();

    public final String name;
    public final int primaryColor;
    public final int secondaryColor;

    FEEnumHoneyComb(Color primary, Color secondary) {
        this.name = toString().toLowerCase(Locale.ENGLISH);
        this.primaryColor = primary.getRGB();
        this.secondaryColor = secondary.getRGB();
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static FEEnumHoneyComb get(int meta) {
        if (meta >= VALUES.length) {
            meta = 0;
        }
        return VALUES[meta];
    }
}
