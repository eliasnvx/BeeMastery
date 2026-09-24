package io.github.elias.beemastery.hive;

import com.mojang.authlib.GameProfile;
import forestry.api.IForestryApi;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.HumidityType;
import forestry.api.core.IErrorLogic;
import forestry.api.core.TemperatureType;
import forestry.api.core.climate.IClimateProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;

/**
 * The "where is the hive and what is around it" half of a bee housing, answered from the player
 * wearing it. Shared by the server-side housing that runs the bees and the client-side one that
 * only draws them.
 */
public abstract class PlayerBeeHousing implements IBeeHousing {

    /** How far behind the body's centre the hive sits, and how high (blocks) — where bees fly out. */
    private static final double BACK_OFFSET = 0.35, BACK_HEIGHT = 1.1;

    protected final Player player;
    private final IErrorLogic errorLogic = IForestryApi.INSTANCE.getErrorManager().createErrorLogic();
    private IClimateProvider climate;

    protected PlayerBeeHousing(Player player) {
        this.player = player;
    }

    /** Drop the cached climate, e.g. after the player walked into another biome. */
    protected void resetClimate() {
        climate = null;
    }

    private IClimateProvider climate() {
        if (climate == null) {
            climate = IForestryApi.INSTANCE.getClimateManager().createClimateProvider(getLevel(), getBlockPos());
        }
        return climate;
    }

    @Override
    public int getBlockLightValue() {
        return getLevel().getMaxLocalRawBrightness(getBlockPos().above());
    }

    @Override
    public boolean canBlockSeeTheSky() {
        return getLevel().canSeeSky(getBlockPos().above());
    }

    @Override
    public boolean isRaining() {
        return getLevel().isRainingAt(getBlockPos().above());
    }

    @Override
    public GameProfile getOwner() {
        return player.getGameProfile();
    }

    /** The hive on the player's back. */
    @Override
    public Vec3 getBeeFXCoordinates() {
        Vec3 forward = Vec3.directionFromRotation(0, player.yBodyRot);
        return player.position().add(0, BACK_HEIGHT, 0).subtract(forward.scale(BACK_OFFSET));
    }

    @Override
    public IErrorLogic getErrorLogic() {
        return errorLogic;
    }

    @Override
    public BlockPos getBlockPos() {
        return player.blockPosition();
    }

    @Override
    public Level getLevel() {
        return player.level();
    }

    @Override
    public Holder<Biome> getBiome() {
        return getLevel().getBiome(getBlockPos());
    }

    @Override
    public TemperatureType temperature() {
        return climate().temperature();
    }

    @Override
    public HumidityType humidity() {
        return climate().humidity();
    }
}
