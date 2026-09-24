package io.github.elias.beemastery.hive;

import forestry.api.IForestryApi;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.apiculture.hives.IHiveFrame;
import forestry.api.apiculture.genetics.IBeeSpecies;
import forestry.api.core.IError;
import forestry.api.core.genetics.IMutation;
import io.github.elias.beemastery.config.BeeMasteryConfig;
import io.github.elias.beemastery.item.FEEnumHiveModule;
import forestry.api.core.genetics.IGenome;
import forestry.api.core.genetics.IIndividual;
import forestry.api.core.genetics.capability.IIndividualHandlerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Lets Forestry's real beekeeping logic run on a hive worn by a player.
 *
 * <p>Forestry exposes {@link forestry.api.apiculture.hives.IHiveManager#createBeekeepingLogic(IBeeHousing)} for any housing,
 * so breeding, ageing, production, flower and climate checks are all Forestry's own. "Where is the
 * hive and what is around it" comes from the player ({@link PlayerBeeHousing}), so a portable hive
 * works in whatever biome the player carries it to.
 */
public class PortableHiveHousing extends PlayerBeeHousing {

    /** Re-read climate and flower caches once the player has moved this far (blocks). */
    private static final int RECHECK_DISTANCE = 4;

    private final IBeekeepingLogic logic;
    private final IBeeModifier portableModifier = new IBeeModifier() {
        @Override
        public float modifyProductionSpeed(IGenome genome, float currentSpeed) {
            float speed = currentSpeed * tier.productionMultiplier();
            return modules.contains(FEEnumHiveModule.BOOSTER) ? speed * BeeMasteryConfig.SERVER.boosterMultiplier() : speed;
        }

        @Override
        public float modifyAging(IGenome genome, IGenome mate, float currentAging) {
            return modules.contains(FEEnumHiveModule.LONGEVITY) ? currentAging * BeeMasteryConfig.SERVER.longevityAging() : currentAging;
        }

        @Override
        public float modifyMutationChance(IGenome genome, IGenome mate, IMutation<IBeeSpecies> mutation, float currentChance) {
            if (modules.contains(FEEnumHiveModule.STABILIZER)) {
                return 0;
            }
            return modules.contains(FEEnumHiveModule.MUTAGEN) ? currentChance * BeeMasteryConfig.SERVER.mutagenMultiplier() : currentChance;
        }

        @Override
        public boolean isAlwaysActive(IGenome genome) {
            return modules.contains(FEEnumHiveModule.LANTERN);
        }

        @Override
        public boolean isSealed() {
            return modules.contains(FEEnumHiveModule.CANOPY);
        }

        @Override
        public boolean isSunlightSimulated() {
            return modules.contains(FEEnumHiveModule.SUN);
        }

        @Override
        public boolean isClimateFullyTolerant() {
            return modules.contains(FEEnumHiveModule.CLIMATE);
        }

        /** A suitable flower carried in the hive replaces the flowers Forestry looks for around it. */
        @Override
        public boolean providesFlowers() {
            return HiveFlowers.suits(inventory.getFlower(), IIndividualHandlerItem.getGenome(inventory.getQueen()),
                    getLevel(), getBlockPos());
        }
    };
    private final IBeeListener frameListener = new IBeeListener() {
        @Override
        public void wearOutEquipment(int amount) {
            wearFrame(amount);
        }
    };

    private HiveStackContainer inventory;
    private BlockPos anchor;
    // read from the hive item once per tick; Forestry queries the modifier many times per tick
    private HiveTier tier = HiveTier.BASIC;
    private Set<FEEnumHiveModule> modules = Set.of();

    public PortableHiveHousing(Player player, ItemStack hive) {
        super(player);
        this.inventory = new HiveStackContainer(hive);
        this.logic = IForestryApi.INSTANCE.getHiveManager().createBeekeepingLogic(this);
        refreshUpgrades();
    }

    /** Point this housing at the hive stack currently worn (it may have been swapped). */
    void bind(ItemStack hive) {
        if (inventory.hive() != hive) {
            inventory = new HiveStackContainer(hive);
            logic.clearCachedValues();
        }
    }

    /** {@link PortableHiveMenu#STATUS_OK} or the numeric id of the first Forestry error. */
    int status() {
        Set<IError> errors = getErrorLogic().getErrors();
        return errors.isEmpty() ? PortableHiveMenu.STATUS_OK
                : IForestryApi.INSTANCE.getErrorManager().getNumericId(errors.iterator().next());
    }

    /** Forestry's own progress figure: the queen's remaining life, or mating progress for a princess. */
    int progressPercent() {
        return logic.getBeeProgressPercent();
    }

    /** One server tick, mirroring what Forestry's own bee housings do. */
    void tick() {
        BlockPos pos = player.blockPosition();
        if (anchor == null || anchor.distManhattan(pos) > RECHECK_DISTANCE) {
            anchor = pos;
            resetClimate();
            logic.clearCachedValues();
        }
        refreshUpgrades();
        if (logic.canWork()) {
            if (BeeMasteryConfig.SERVER.ownHiveProtection()) {
                OwnHiveShield.run(player, logic::doWork);  // bee effects never hit the wearer
            } else {
                logic.doWork();
            }
        }
        if (modules.contains(FEEnumHiveModule.COLLECTOR) && player.tickCount % BeeMasteryConfig.SERVER.collectorInterval() == 0) {
            collectProducts();
        }
    }

    boolean hasModule(FEEnumHiveModule module) {
        return modules.contains(module);
    }

    private void refreshUpgrades() {
        tier = HiveTier.of(inventory.hive());
        modules = inventory.getModules();
    }

    /** Collector module: products go straight into the wearer's inventory; what doesn't fit stays in the hive. */
    private void collectProducts() {
        for (int slot = HiveStackContainer.PRODUCTS_START; slot < HiveStackContainer.PRODUCTS_END; slot++) {
            ItemStack product = inventory.getItem(slot);
            if (product.isEmpty()) {
                continue;
            }
            ItemStack rest = product.copy();
            player.getInventory().add(rest);
            if (rest.getCount() != product.getCount()) {
                inventory.setItem(slot, rest);
            }
        }
    }

    private void wearFrame(int amount) {
        ItemStack frame = inventory.getFrame();
        if (!(frame.getItem() instanceof IHiveFrame hiveFrame)) {
            return;
        }
        IIndividual queen = IIndividualHandlerItem.getIndividual(inventory.getQueen());
        if (queen instanceof IBee bee) {
            inventory.setFrame(hiveFrame.frameUsed(this, frame.copy(), bee, amount));
        }
    }

    // --- IBeeHousing ------------------------------------------------------------------------

    // Only this housing's own modifiers/listeners: the beekeeping logic wraps them in Forestry's
    // aggregate (createBeeHousingModifier/Listener) itself, and listing that aggregate here would
    // make it iterate over itself forever.
    @Override
    public Iterable<IBeeModifier> getBeeModifiers() {
        List<IBeeModifier> modifiers = new ArrayList<>(2);
        modifiers.add(portableModifier);
        ItemStack frame = inventory.getFrame();
        if (frame.getItem() instanceof IHiveFrame hiveFrame) {
            modifiers.add(hiveFrame.getBeeModifier(frame));
        }
        return modifiers;
    }

    @Override
    public Iterable<IBeeListener> getBeeListeners() {
        return List.of(frameListener);
    }

    @Override
    public IBeeHousingInventory getBeeInventory() {
        return inventory;
    }

    @Override
    public IBeekeepingLogic getBeekeepingLogic() {
        return logic;
    }
}
