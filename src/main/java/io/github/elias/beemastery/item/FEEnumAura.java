package io.github.elias.beemastery.item;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

/**
 * The set of aura types shared by aura bee effects and the Aura Charm item.
 *
 * <p>Each constant carries the tunable data for its aura: display tint, how often it
 * pulses (in ticks), the charm charge (used as item durability), and the radius the
 * charm's personal version reaches around the holder.
 */
public enum FEEnumAura implements StringRepresentable {

    // name              tint       apiaryInterval  charmCharge  charmInterval  personalRadius
    DRACONIC (0xFF3030, 40,             220,         40,            5.0),
    LEGENDARY(0x3070FF, 100,            320,         40,            5.0),
    WITHERIA (0x303030, 40,             200,         40,            5.0),
    MUTAGENIC(0x99CC00, 100,            180,         40,            5.0),
    HARVEST  (0xFFA500, 20,             160,         20,            6.0);

    public static final FEEnumAura[] VALUES = values();

    public final String name;
    public final int color;
    /** Ticks between pulses when this aura runs inside an apiary/bee house. */
    public final int apiaryInterval;
    /** Total charge of an Aura Charm bound to this aura (also used as item durability). */
    public final int charmCharge;
    /** Ticks between pulses when this aura runs from a charm carried by a player. */
    public final int charmInterval;
    /** Radius (in blocks) the charm's personal pulse reaches around the holder. */
    public final double personalRadius;

    FEEnumAura(int color, int apiaryInterval, int charmCharge, int charmInterval, double personalRadius) {
        this.name = toString().toLowerCase(Locale.ENGLISH);
        this.color = color;
        this.apiaryInterval = apiaryInterval;
        this.charmCharge = charmCharge;
        this.charmInterval = charmInterval;
        this.personalRadius = personalRadius;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
