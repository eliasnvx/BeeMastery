package io.github.elias.beemastery.effect;

import io.github.elias.beemastery.item.FEEnumAura;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Tracks which apiaries recently pulsed which aura, so nearby hives running the same aura can
 * amplify each other without ever inspecting a neighbor's bee genome: a housing only appears
 * here because it pulsed that exact aura itself, so counting "nearby entries" is equivalent to
 * counting "nearby hives currently running this aura."
 *
 * <p>Entries expire on their own — {@link #countNearby} prunes anything older than {@code maxAge}
 * as it scans, so a removed or re-queened hive quietly drops out within a few pulse cycles.
 */
final class AuraHiveRegistry {

    private static final Map<FEEnumAura, Map<ResourceKey<Level>, Map<BlockPos, Long>>> LAST_PULSE =
            new EnumMap<>(FEEnumAura.class);

    private AuraHiveRegistry() {
    }

    static void announce(FEEnumAura aura, Level level, BlockPos pos, long gameTime) {
        LAST_PULSE.computeIfAbsent(aura, a -> new HashMap<>())
                .computeIfAbsent(level.dimension(), d -> new HashMap<>())
                .put(pos.immutable(), gameTime);
    }

    static int countNearby(FEEnumAura aura, Level level, BlockPos self, double radius, long gameTime, long maxAge) {
        Map<ResourceKey<Level>, Map<BlockPos, Long>> byDimension = LAST_PULSE.get(aura);
        if (byDimension == null) {
            return 0;
        }
        Map<BlockPos, Long> entries = byDimension.get(level.dimension());
        if (entries == null) {
            return 0;
        }
        double radiusSq = radius * radius;
        int count = 0;
        Iterator<Map.Entry<BlockPos, Long>> it = entries.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, Long> entry = it.next();
            if (gameTime - entry.getValue() > maxAge) {
                it.remove();
                continue;
            }
            BlockPos pos = entry.getKey();
            if (pos.equals(self)) {
                continue;
            }
            if (pos.distSqr(self) <= radiusSq) {
                count++;
            }
        }
        return count;
    }
}
