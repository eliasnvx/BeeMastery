package io.github.elias.beemastery.hive;

import com.mojang.authlib.GameProfile;
import forestry.api.IForestryApi;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.climate.IClimateProvider;
import forestry.api.core.HumidityType;
import forestry.api.core.IError;
import forestry.api.core.IErrorLogic;
import forestry.api.core.TemperatureType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

/**
 * The "where is the hive and what is around it" half of a bee housing, answered from the player
 * wearing it. Shared by the server-side housing that runs the bees and the client-side one that
 * only draws them.
 */
public abstract class PlayerBeeHousing implements IBeeHousing {

    /** How far behind the body's centre the hive sits, and how high (blocks) — where bees fly out. */
    private static final double BACK_OFFSET = 0.35, BACK_HEIGHT = 1.1;

    protected final Player player;
    private final IErrorLogic errors = IForestryApi.INSTANCE.getErrorManager().createErrorLogic();
    /** Forestry's error logic, minus the errors this housing {@link #waives}. */
    private final IErrorLogic errorLogic = new IErrorLogic() {
        @Override
        public boolean setCondition(boolean condition, IError error) {
            return errors.setCondition(condition && !waives(error), error);
        }

        @Override
        public boolean contains(IError error) {
            return errors.contains(error);
        }

        @Override
        public boolean hasErrors() {
            return errors.hasErrors();
        }

        @Override
        public void clearErrors() {
            errors.clearErrors();
        }

        @Override
        public Set<IError> getErrors() {
            return errors.getErrors();
        }
    };
    private IClimateProvider climate;

    protected PlayerBeeHousing(Player player) {
        this.player = player;
    }

    /**
     * Errors Forestry's beekeeping logic reports that this housing overrules, e.g. "no flowers"
     * while a flower is carried in the hive. Forestry 2.5 has no modifier hooks for these (3.x
     * has {@code providesFlowers}/{@code isClimateFullyTolerant}, which only lift the same errors).
     */
    protected boolean waives(IError error) {
        return false;
    }

    /** Drop the cached climate, e.g. after the player walked into another biome. */
    protected void resetClimate() {
        climate = null;
    }

    private IClimateProvider climate() {
        if (climate == null) {
            climate = IForestryApi.INSTANCE.getClimateManager().createClimateProvider(getWorldObj(), getCoordinates());
        }
        return climate;
    }

    @Override
    public int getBlockLightValue() {
        return getWorldObj().getMaxLocalRawBrightness(getCoordinates().above());
    }

    @Override
    public boolean canBlockSeeTheSky() {
        return getWorldObj().canSeeSky(getCoordinates().above());
    }

    @Override
    public boolean isRaining() {
        return getWorldObj().isRainingAt(getCoordinates().above());
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
    public BlockPos getCoordinates() {
        return player.blockPosition();
    }

    @Override
    public Level getWorldObj() {
        return player.level();
    }

    @Override
    public Holder<Biome> getBiome() {
        return getWorldObj().getBiome(getCoordinates());
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
