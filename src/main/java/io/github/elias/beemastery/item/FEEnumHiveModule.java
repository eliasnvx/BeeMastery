package io.github.elias.beemastery.item;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

/**
 * Portable hive modules. All but the collector map onto one of Forestry's own bee-housing
 * hooks, so they behave exactly like the equivalent Forestry machinery would.
 *
 * <p>Forestry 2.5 has no modifier hook for climate tolerance; the climate module instead waives
 * the four climate errors of the queen's work check, which is exactly what that hook does in
 * later Forestry versions (see {@link io.github.elias.beemastery.hive.PortableHiveHousing}).
 */
public enum FEEnumHiveModule implements StringRepresentable {
    /** isAlwaysActive: ignores the bees' day/night activity and light preference. */
    LANTERN,
    /** isSealed: works in the rain. */
    CANOPY,
    /** isSunlightSimulated: works without open sky (caves, indoors). */
    SUN,
    /** Ignores biome temperature and humidity. */
    CLIMATE,
    /** Production speed x booster multiplier (server config). */
    BOOSTER,
    /** Ageing x longevity factor (server config): the queen lives longer. */
    LONGEVITY,
    /** Mutation chance x mutagen multiplier (server config). */
    MUTAGEN,
    /** No mutations at all: pure breeding. */
    STABILIZER,
    /** Moves products straight into the wearer's inventory. */
    COLLECTOR;

    public static final FEEnumHiveModule[] VALUES = values();

    public final String name;

    FEEnumHiveModule() {
        this.name = toString().toLowerCase(Locale.ENGLISH);
    }

    /** Modules that can't be installed together (more and no mutations at once make no sense). */
    public boolean conflictsWith(FEEnumHiveModule other) {
        return (this == MUTAGEN && other == STABILIZER) || (this == STABILIZER && other == MUTAGEN);
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
