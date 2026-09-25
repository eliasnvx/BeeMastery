package io.github.elias.beemastery.hive;

import io.github.elias.beemastery.config.BeeMasteryConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Lantern module: real light around the wearer. Vanilla has no moving light sources, so an
 * invisible {@code minecraft:light} block follows the player's head (feet if the head is in a
 * block). It lights the area for everyone, stops mobs spawning next to you, and can be built over.
 *
 * <p>Only a spot that is air (or still water) is used, and only a light block this class placed
 * is ever removed. Keyed by UUID, since a respawned player is a new entity object.
 */
final class HiveLantern {

    private static final Map<UUID, Placed> PLACED = new HashMap<>();

    private record Placed(Level level, BlockPos pos) {
    }

    private HiveLantern() {
    }

    /** Called every tick for a player wearing a hive: keep the light on their head, or remove it. */
    static void update(Player player, boolean lit) {
        Placed old = PLACED.get(player.getUUID());
        BlockPos target = lit ? target(player, old) : null;
        if (old != null && target != null && old.level() == player.level() && old.pos().equals(target)) {
            return;
        }
        remove(player.getUUID());
        if (target != null) {
            place(player.level(), target);
            PLACED.put(player.getUUID(), new Placed(player.level(), target));
        }
    }

    static void remove(UUID player) {
        Placed placed = PLACED.remove(player);
        if (placed == null || !placed.level().isLoaded(placed.pos())) {
            return;
        }
        BlockState state = placed.level().getBlockState(placed.pos());
        if (state.is(Blocks.LIGHT)) {
            placed.level().setBlock(placed.pos(), state.getValue(LightBlock.WATERLOGGED)
                    ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    /** Remove every light, e.g. when the server stops, so none are saved into the world. */
    static void removeAll() {
        for (UUID player : PLACED.keySet().toArray(UUID[]::new)) {
            remove(player);
        }
    }

    private static BlockPos target(Player player, Placed current) {
        Level level = player.level();
        BlockPos head = BlockPos.containing(player.getEyePosition());
        if (canHost(level, head, current)) {
            return head;
        }
        BlockPos feet = player.blockPosition();
        return canHost(level, feet, current) ? feet : null;
    }

    private static boolean canHost(Level level, BlockPos pos, Placed current) {
        if (current != null && current.level() == level && current.pos().equals(pos)) {
            return true;  // our own light is already there
        }
        BlockState state = level.getBlockState(pos);
        return state.isAir() || (state.is(Blocks.WATER) && state.getFluidState().isSource());
    }

    private static void place(Level level, BlockPos pos) {
        boolean water = level.getBlockState(pos).is(Blocks.WATER);
        level.setBlock(pos, Blocks.LIGHT.defaultBlockState()
                .setValue(LightBlock.LEVEL, BeeMasteryConfig.SERVER.lanternLightLevel())
                .setValue(LightBlock.WATERLOGGED, water), Block.UPDATE_ALL);
    }
}
