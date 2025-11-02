package io.github.elias.beemastery.item;

import forestry.api.core.IItemSubtype;
import net.minecraft.util.StringRepresentable;

import java.awt.*;
import java.util.Locale;

public enum FEEnumScoop implements StringRepresentable, IItemSubtype {

    REINFORCED(new Color(0xCCCC99), 300, 1.5f),
    DRACONIC(new Color(0xFF0000), 700, 2.0f),
    LEGENDARY(new Color(0x0066FF), 10000, 3.0f);

    public static final FEEnumScoop[] VALUES = values();

    public final String name;
    public final int color;
    public final int maxDamage;
    public final float miningSpeed;

    FEEnumScoop(Color color, int maxDamage, float miningSpeed) {
        this.name = toString().toLowerCase(Locale.ENGLISH);
        this.color = color.getRGB();
        this.maxDamage = maxDamage;
        this.miningSpeed = miningSpeed;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static FEEnumScoop get(int meta) {
        if (meta >= VALUES.length) {
            meta = 0;
        }
        return VALUES[meta];
    }
}
