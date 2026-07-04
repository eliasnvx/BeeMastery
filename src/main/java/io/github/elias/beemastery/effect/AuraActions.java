package io.github.elias.beemastery.effect;

import io.github.elias.beemastery.item.FEEnumAura;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Shared implementation of every aura's per-pulse behaviour.
 *
 * <p>Both {@link AuraBeeEffect} (apiary-side) and the Aura Charm (player-side) build an
 * {@link AABB} and call {@link #pulse}, so a bee and its bottled charm behave identically.
 * All logic here runs server-side only.
 */
public final class AuraActions {

    private static final int MUTAGENIC_DURATION = 20 * 8;

    private static final MobEffect[] MUTAGENIC_POOL = {
            MobEffects.MOVEMENT_SPEED,
            MobEffects.DIG_SPEED,
            MobEffects.JUMP,
            MobEffects.DAMAGE_BOOST,
            MobEffects.LUCK,
    };

    private AuraActions() {
    }

    /**
     * Runs one pulse of the given aura within {@code area}.
     *
     * @param aura  which aura to apply
     * @param level the (server) level
     * @param area  the region the aura affects
     */
    public static void pulse(FEEnumAura aura, Level level, AABB area) {
        if (level.isClientSide) {
            return;
        }
        switch (aura) {
            case DRACONIC -> {
                damageHostiles(level, area, 6.0F);
                buff(players(level, area), MobEffects.DAMAGE_BOOST, 20 * 6, 0);
            }
            case LEGENDARY -> {
                List<Player> players = players(level, area);
                buff(players, MobEffects.REGENERATION, 20 * 6, 1);
                buff(players, MobEffects.ABSORPTION, 20 * 12, 0);
            }
            case WITHERIA -> witherHostiles(level, area);
            case MUTAGENIC -> {
                MobEffect chosen = MUTAGENIC_POOL[level.random.nextInt(MUTAGENIC_POOL.length)];
                buff(players(level, area), chosen, MUTAGENIC_DURATION, 0);
            }
            case HARVEST -> fertilize(level, area);
        }
    }

    private static List<Player> players(Level level, AABB area) {
        return level.getEntitiesOfClass(Player.class, area, p -> !p.isSpectator());
    }

    private static void buff(List<Player> players, MobEffect effect, int duration, int amplifier) {
        for (Player player : players) {
            player.addEffect(new MobEffectInstance(effect, duration, amplifier, true, true));
        }
    }

    private static void damageHostiles(Level level, AABB area, float damage) {
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area, e -> e instanceof Enemy && e.isAlive());
        for (LivingEntity target : targets) {
            target.hurt(level.damageSources().magic(), damage);
        }
    }

    private static void witherHostiles(Level level, AABB area) {
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area, e -> e instanceof Enemy && e.isAlive());
        for (LivingEntity target : targets) {
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * 4, 1, true, true));
        }
    }

    /**
     * Bonemeal-style growth: schedules a random tick on a growable block inside the area,
     * mirroring Forestry's Fertile bee effect.
     */
    private static void fertilize(Level level, AABB area) {
        for (int attempt = 0; attempt < 6; attempt++) {
            int x = randomIn(level, area.minX, area.maxX);
            int y = randomIn(level, area.minY, area.maxY);
            int z = randomIn(level, area.minZ, area.maxZ);
            if (level.getChunkSource().getChunkNow(x >> 4, z >> 4) == null) {
                continue;
            }
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();
            if (block.isRandomlyTicking(state) && block instanceof BonemealableBlock) {
                level.scheduleTick(pos, block, 5);
                return;
            }
        }
    }

    private static int randomIn(Level level, double min, double max) {
        int lo = (int) Math.floor(min);
        int hi = (int) Math.ceil(max);
        if (hi <= lo) {
            return lo;
        }
        return lo + level.random.nextInt(hi - lo);
    }
}
