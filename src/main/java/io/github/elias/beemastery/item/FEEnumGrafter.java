package io.github.elias.beemastery.item;

import forestry.api.core.IItemSubtype;
import net.minecraft.util.StringRepresentable;

import java.awt.*;
import java.util.Locale;

public enum FEEnumGrafter implements StringRepresentable, IItemSubtype {

    REINFORCED(new Color(0xCCCC99), 300, 3.0f),
    DRACONIC(new Color(0xFF0000), 700, 5.0f),
    LEGENDARY(new Color(0x0066FF), 10000, 10.0f);

    public static final FEEnumGrafter[] VALUES = values();

    public final String name;
    public final int color;
    public final int maxDamage;
    public final float saplingModifier;

    FEEnumGrafter(Color color, int maxDamage, float saplingModifier) {
        this.name = toString().toLowerCase(Locale.ENGLISH);
        this.color = color.getRGB();
        this.maxDamage = maxDamage;
        this.saplingModifier = saplingModifier;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static FEEnumGrafter get(int meta) {
        if (meta >= VALUES.length) {
            meta = 0;
        }
        return VALUES[meta];
    }
}
