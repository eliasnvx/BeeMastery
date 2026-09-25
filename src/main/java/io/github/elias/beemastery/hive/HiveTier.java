package io.github.elias.beemastery.hive;

import io.github.elias.beemastery.config.BeeMasteryConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

/**
 * Upgrade tiers of the portable hive, following the mod's metal line. Each tier opens one more
 * module slot and makes the bees work faster; upgrades are crafted with that tier's ingots.
 *
 * <p>Stored as the int tag {@value #TAG} on the hive item (absent = basic).
 */
public enum HiveTier implements StringRepresentable {
    BASIC(ChatFormatting.GRAY),
    REINFORCED(ChatFormatting.WHITE),
    DRACONIC(ChatFormatting.RED),
    LEGENDARY(ChatFormatting.AQUA);

    public static final HiveTier[] VALUES = values();
    public static final String TAG = "hive_tier";

    public final ChatFormatting color;
    public final String name;

    HiveTier(ChatFormatting color) {
        this.color = color;
        this.name = name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    /** The tier with this serialized name, or null. */
    public static HiveTier byName(String name) {
        for (HiveTier tier : VALUES) {
            if (tier.name.equals(name)) {
                return tier;
            }
        }
        return null;
    }

    /** The tier this one is upgraded from, or null for the basic hive. */
    public HiveTier previous() {
        return this == BASIC ? null : VALUES[ordinal() - 1];
    }

    /**
     * Production speed relative to the bees' own speed gene (server config). By default the basic
     * tier matches Forestry's Bee House (x0.25) — the hive is crafted from one — and the top tier
     * stays below full speed even with a booster module (0.75 * 1.25 < 1).
     */
    public float productionMultiplier() {
        return BeeMasteryConfig.SERVER.tierSpeed(this);
    }

    public int moduleSlots() {
        return ordinal() + 1;
    }

    public static HiveTier of(ItemStack hive) {
        CompoundTag tag = hive.getTag();
        int tier = tag == null ? 0 : tag.getInt(TAG);
        return VALUES[Mth.clamp(tier, 0, VALUES.length - 1)];
    }

    public static ItemStack withTier(ItemStack hive, HiveTier tier) {
        ItemStack out = hive.copyWithCount(1);
        if (tier == BASIC) {
            CompoundTag tag = out.getTag();
            if (tag != null) {
                tag.remove(TAG);
                if (tag.isEmpty()) {
                    out.setTag(null);
                }
            }
        } else {
            out.getOrCreateTag().putInt(TAG, tier.ordinal());
        }
        return out;
    }
}
