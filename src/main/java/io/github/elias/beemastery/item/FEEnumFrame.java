package io.github.elias.beemastery.item;

import forestry.api.apiculture.IBeeModifier;
import forestry.api.core.IItemSubtype;
import net.minecraft.util.StringRepresentable;

import java.awt.*;
import java.util.Locale;

public enum FEEnumFrame implements StringRepresentable, IItemSubtype {

    COAL(new Color(0x000000), 100, 1.1f, 1.0f, 1.0f, 1.0f),
    IRON(new Color(0xFFFFCC), 200, 1.2f, 1.0f, 1.0f, 1.0f),
    GOLD(new Color(0xFFFF66), 150, 1.3f, 1.0f, 1.0f, 1.0f),
    DIAMOND(new Color(0xCCFFFF), 250, 1.4f, 1.0f, 1.0f, 1.0f),
    EMERALD(new Color(0x99FF66), 300, 1.5f, 1.0f, 1.0f, 1.0f),
    REINFORCED(new Color(0xCCCC99), 500, 1.4f, 1.0f, 1.0f, 1.0f),
    WITHERIA(new Color(0x333333), 750, 2.0f, 1.0f, 1.0f, 1.0f),
    DRACONIC(new Color(0xFF0000), 1000, 2.5f, 1.0f, 1.0f, 1.0f),
    MUTATION(new Color(0x99CC00), 40, 1.0f, 0.5f, 10.0f, 1.0f),
    LEGENDARY(new Color(0x0066FF), 10000, 5.0f, 1.0f, 1.0f, 1.0f);

    public static final FEEnumFrame[] VALUES = values();

    public final String name;
    public final int color;
    public final int maxDamage;
    public final float productionModifier;
    public final float lifespanModifier;
    public final float mutationModifier;
    public final float territoryModifier;

    FEEnumFrame(Color color, int maxDamage, float productionModifier, float lifespanModifier, float mutationModifier, float territoryModifier) {
        this.name = toString().toLowerCase(Locale.ENGLISH);
        this.color = color.getRGB();
        this.maxDamage = maxDamage;
        this.productionModifier = productionModifier;
        this.lifespanModifier = lifespanModifier;
        this.mutationModifier = mutationModifier;
        this.territoryModifier = territoryModifier;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static FEEnumFrame get(int meta) {
        if (meta >= VALUES.length) {
            meta = 0;
        }
        return VALUES[meta];
    }
}
