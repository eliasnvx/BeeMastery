package io.github.elias.beemastery.effect;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.genetics.IBeeEffect;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.alleles.BeeChromosomes;
import io.github.elias.beemastery.item.FEEnumAura;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * A Bee Mastery aura, run by an apiary/bee house each work cycle.
 *
 * <p>One instance is registered per {@link FEEnumAura}. The pulse region is derived from
 * the bee's TERRITORY gene, so frames/modifiers that widen territory also widen the aura.
 * The actual behaviour lives in {@link AuraActions} so the Aura Charm can reuse it.
 *
 * <p>Hives running the same aura amplify each other when clustered: every pulse announces this
 * housing's position to {@link AuraHiveRegistry}, and the pulse area is scaled up by how many
 * other same-aura hives have pulsed nearby recently. This rewards building a themed bee-yard
 * instead of scattering hives at random.
 */
public class AuraBeeEffect implements IBeeEffect {

    /** Hives within this many blocks of each other count as the same cluster. */
    private static final double CLUSTER_RADIUS = 12.0;
    /** Extra area scale per clustered neighbour (e.g. 3 neighbours = +45%). */
    private static final double CLUSTER_BONUS_PER_HIVE = 0.15;
    /** Stop counting bonus neighbours past this many, so mega bee-yards don't scale forever. */
    private static final int CLUSTER_MAX_NEIGHBOURS = 4;

    private final FEEnumAura aura;

    public AuraBeeEffect(FEEnumAura aura) {
        this.aura = aura;
    }

    public FEEnumAura getAura() {
        return aura;
    }

    @Override
    public IEffectData validateStorage(IEffectData storedData) {
        return storedData;
    }

    @Override
    public boolean isCombinable() {
        return false;
    }

    @Override
    public boolean isDominant() {
        return true;
    }

    @Override
    public IEffectData doEffect(IGenome genome, IEffectData storedData, IBeeHousing housing) {
        Level level = housing.getWorldObj();
        if (level == null || level.isClientSide) {
            return storedData;
        }
        if (level.getGameTime() % aura.apiaryInterval != 0) {
            return storedData;
        }

        BlockPos center = housing.getCoordinates();
        long gameTime = level.getGameTime();

        AuraHiveRegistry.announce(aura, level, center, gameTime);
        int neighbours = Math.min(CLUSTER_MAX_NEIGHBOURS,
                AuraHiveRegistry.countNearby(aura, level, center, CLUSTER_RADIUS, gameTime, aura.apiaryInterval * 3L));
        double clusterScale = 1.0 + neighbours * CLUSTER_BONUS_PER_HIVE;

        Vec3i territory = genome.getActiveValue(BeeChromosomes.TERRITORY);
        AABB area = new AABB(center).inflate(
                Math.max(1, territory.getX()) / 2.0 * clusterScale,
                Math.max(1, territory.getY()) / 2.0 * clusterScale,
                Math.max(1, territory.getZ()) / 2.0 * clusterScale);

        AuraActions.pulse(aura, level, area);
        return storedData;
    }
}
