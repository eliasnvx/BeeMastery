package io.github.elias.beemastery.gametest;

import forestry.api.IForestryApi;
import forestry.api.apiculture.ForestryBeeSpecies;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.genetics.BeeLifeStage;
import forestry.api.core.ForestryError;
import forestry.api.core.IError;
import forestry.api.genetics.ForestrySpeciesTypes;
import forestry.api.genetics.ILifeStage;
import io.github.elias.beemastery.ForestryExtras;
import io.github.elias.beemastery.hive.BeeStacks;
import io.github.elias.beemastery.hive.HiveFlowers;
import io.github.elias.beemastery.hive.HiveStackContainer;
import io.github.elias.beemastery.hive.HiveTier;
import io.github.elias.beemastery.hive.OwnHiveShield;
import io.github.elias.beemastery.hive.PortableHiveHousing;
import io.github.elias.beemastery.hive.PortableHiveTicker;
import io.github.elias.beemastery.item.FEEnumHiveModule;
import io.github.elias.beemastery.item.FEEnumIngot;
import io.github.elias.beemastery.item.FEEnumPropolis;
import io.github.elias.beemastery.item.FEItemPortableHive;
import io.github.elias.beemastery.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A worn portable hive with a queen runs Forestry's real beekeeping logic on the player tick.
 * That path calls into Forestry's modifier/listener aggregation every tick, so a wiring mistake
 * there (e.g. the aggregate listing itself) crashes the server — this catches it without a client.
 *
 * <p>Run with {@code ./gradlew runGameTestServer}; only registered when
 * {@code forge.enabledGameTestNamespaces} includes our mod id, so it never runs for players.
 */
@GameTestHolder(ForestryExtras.MOD_ID)
@PrefixGameTestTemplate(false)
public class PortableHiveTest {

    private static final List<String> HIVE_RECIPES = List.of(
            "portable_hive", "portable_hive_attach_armor", "portable_hive_detach_armor",
            "portable_hive_upgrade_reinforced", "portable_hive_upgrade_draconic", "portable_hive_upgrade_legendary",
            "hive_module_lantern", "hive_module_canopy", "hive_module_sun", "hive_module_climate", "hive_module_booster",
            "hive_module_longevity", "hive_module_mutagen", "hive_module_stabilizer", "hive_module_collector");

    /** The 1.20.1 recipe files (folder name, result format, forge: tags) actually load. */
    @GameTest(template = "empty")
    public static void hiveRecipesLoaded(GameTestHelper helper) {
        RecipeManager recipes = helper.getLevel().getRecipeManager();
        for (String path : HIVE_RECIPES) {
            ResourceLocation id = new ResourceLocation(ForestryExtras.MOD_ID, path);
            if (recipes.byKey(id).isEmpty()) {
                helper.fail("Recipe missing after datapack load: " + id);
            }
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void wornHiveTicks(GameTestHelper helper) {
        Player player = wearHive(helper, ItemStack.EMPTY);
        helper.assertTrue(PortableHiveTicker.get(player) != null, "worn hive was not picked up by the ticker");
        helper.assertTrue(!new HiveStackContainer(player.getItemBySlot(EquipmentSlot.CHEST)).getQueen().isEmpty(),
                "queen vanished from the worn hive");
        helper.succeed();
    }

    /** The test world has no flowers, so without the slot a forest queen reports "no flowers"... */
    @GameTest(template = "empty")
    public static void noFlowerSlotNoFlowers(GameTestHelper helper) {
        helper.assertTrue(errors(wearHive(helper, ItemStack.EMPTY)).contains(ForestryError.NO_FLOWER),
                "expected NO_FLOWER with an empty flower slot and no flowers around");
        helper.succeed();
    }

    /** ...a poppy in the slot satisfies her... */
    @GameTest(template = "empty")
    public static void flowerSlotProvidesFlowers(GameTestHelper helper) {
        Set<IError> errors = errors(wearHive(helper, new ItemStack(Items.POPPY)));
        helper.assertTrue(!errors.contains(ForestryError.NO_FLOWER), "poppy in the slot should satisfy a forest bee, got " + errors);
        helper.succeed();
    }

    /** ...but a flower of another type (nether wart is for nether bees) does not. */
    @GameTest(template = "empty")
    public static void wrongFlowerDoesNotCount(GameTestHelper helper) {
        helper.assertTrue(errors(wearHive(helper, new ItemStack(Items.NETHER_WART))).contains(ForestryError.NO_FLOWER),
                "nether wart must not satisfy a forest bee");
        helper.assertTrue(HiveFlowers.isAnyFlower(new ItemStack(Items.NETHER_WART)), "nether wart should fit the slot");
        helper.assertTrue(!HiveFlowers.isAnyFlower(new ItemStack(Items.DIRT)), "dirt must not fit the flower slot");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void moduleSlotRules(GameTestHelper helper) {
        ItemStack hive = new ItemStack(ModItems.PORTABLE_HIVE.get());
        HiveStackContainer basic = new HiveStackContainer(hive);
        int first = HiveStackContainer.MODULES_START;
        helper.assertTrue(basic.canPlaceItem(first, module(FEEnumHiveModule.LANTERN)), "basic hive: first module slot is open");
        helper.assertTrue(!basic.canPlaceItem(first + 1, module(FEEnumHiveModule.CANOPY)), "basic hive: second module slot is locked");
        helper.assertTrue(!basic.canPlaceItem(first, new ItemStack(Items.DIRT)), "only modules go into module slots");

        HiveStackContainer legendary = new HiveStackContainer(HiveTier.withTier(hive, HiveTier.LEGENDARY));
        legendary.setItem(first, module(FEEnumHiveModule.LANTERN));
        legendary.setItem(first + 1, module(FEEnumHiveModule.MUTAGEN));
        helper.assertTrue(legendary.canPlaceItem(first + 3, module(FEEnumHiveModule.CANOPY)), "legendary hive: all four slots open");
        helper.assertTrue(!legendary.canPlaceItem(first + 2, module(FEEnumHiveModule.LANTERN)), "one module of each kind");
        helper.assertTrue(!legendary.canPlaceItem(first + 2, module(FEEnumHiveModule.STABILIZER)), "mutagen and stabilizer conflict");
        helper.succeed();
    }

    /**
     * Speed follows the tier; the booster stacks on top; the flag modules switch on Forestry's matching hooks.
     * Forestry 2.5 chains housing modifiers multiplicatively from the speed gene, like 3.x: basic = 0.25,
     * legendary + booster = 0.75 * 1.25.
     */
    @GameTest(template = "empty")
    public static void tierAndModulesReachForestry(GameTestHelper helper) {
        PortableHiveHousing basic = PortableHiveTicker.get(wearHive(helper, HiveTier.BASIC, c -> {}));
        helper.assertTrue(Math.abs(productionSpeed(basic) - 0.25f) < 1e-4, "basic hive should work at 25% (like a Bee House), got " + productionSpeed(basic));

        PortableHiveHousing full = PortableHiveTicker.get(wearHive(helper, HiveTier.LEGENDARY, c -> {
            c.setItem(HiveStackContainer.MODULES_START, module(FEEnumHiveModule.BOOSTER));
            c.setItem(HiveStackContainer.MODULES_START + 1, module(FEEnumHiveModule.CANOPY));
            c.setItem(HiveStackContainer.MODULES_START + 2, module(FEEnumHiveModule.SUN));
            c.setItem(HiveStackContainer.MODULES_START + 3, module(FEEnumHiveModule.CLIMATE));
        }));
        helper.assertTrue(Math.abs(productionSpeed(full) - 0.9375f) < 1e-4, "legendary + booster should be 93.75%, got " + productionSpeed(full));
        IBeeModifier all = IForestryApi.INSTANCE.getHiveManager().createBeeHousingModifier(full);
        helper.assertTrue(all.isSealed() && all.isSunlightSimulated(), "canopy/sun modules should switch on Forestry's rain/sky hooks");
        helper.assertTrue(!all.isAlwaysActive(null), "no lantern installed");
        // Forestry 2.5 has no climate hook: the climate module lifts the climate errors instead
        full.getErrorLogic().clearErrors();
        full.getErrorLogic().setCondition(true, ForestryError.TOO_HOT);
        full.getErrorLogic().setCondition(true, ForestryError.TOO_ARID);
        helper.assertTrue(!full.getErrorLogic().hasErrors(), "climate module should waive too hot / too arid");
        basic.getErrorLogic().setCondition(true, ForestryError.TOO_COLD);
        helper.assertTrue(basic.getErrorLogic().contains(ForestryError.TOO_COLD), "without the climate module the climate still counts");
        helper.succeed();
    }

    /** Lantern module: a light block rides on the wearer's head and is cleaned up when the hive comes off. */
    @GameTest(template = "empty")
    public static void lanternLightsTheWearer(GameTestHelper helper) {
        Player player = helper.makeMockSurvivalPlayer();
        player.moveTo(Vec3.atBottomCenterOf(helper.absolutePos(BlockPos.ZERO)));
        ItemStack hive = new ItemStack(ModItems.PORTABLE_HIVE.get());
        new HiveStackContainer(hive).setItem(HiveStackContainer.MODULES_START, module(FEEnumHiveModule.LANTERN));
        player.setItemSlot(EquipmentSlot.CHEST, hive);
        for (int i = 0; i < 5; i++) {
            player.tick();
        }
        BlockPos head = BlockPos.containing(player.getEyePosition());
        helper.assertTrue(helper.getLevel().getBlockState(head).is(Blocks.LIGHT), "expected a light block at the wearer's head");

        player.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
        player.tick();
        helper.assertTrue(!helper.getLevel().getBlockState(head).is(Blocks.LIGHT), "light must be removed with the hive");
        helper.succeed();
    }

    /** Whatever the wearer's own hive does to them while it runs is undone; the same hit outside of it lands. */
    @GameTest(template = "empty")
    public static void ownHiveNeverHurtsTheWearer(GameTestHelper helper) {
        Player player = helper.makeMockSurvivalPlayer();
        Vec3 start = Vec3.atBottomCenterOf(helper.absolutePos(BlockPos.ZERO));
        player.moveTo(start);
        float health = player.getHealth();

        OwnHiveShield.run(player, () -> {
            player.hurt(player.damageSources().magic(), 4);                      // aggressive / radioactive
            player.addEffect(new MobEffectInstance(MobEffects.POISON, 200));      // harmful potion effect
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200)); // a beneficial one stays
            player.setSecondsOnFire(5);                                           // ignition
            player.teleportTo(start.x + 5, start.y, start.z);                     // phasing
        });

        helper.assertTrue(player.getHealth() == health, "damage should be cancelled, health " + player.getHealth());
        helper.assertTrue(!player.hasEffect(MobEffects.POISON), "harmful effect should be refused");
        helper.assertTrue(player.hasEffect(MobEffects.REGENERATION), "beneficial effect should still apply");
        helper.assertTrue(player.getRemainingFireTicks() <= 0, "fire should be put out");
        helper.assertTrue(player.position().distanceTo(start) < 0.01, "random teleport should be reverted");

        player.hurt(player.damageSources().magic(), 4);
        helper.assertTrue(player.getHealth() < health, "outside the own hive's tick damage must still apply");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void collectorEmptiesProducts(GameTestHelper helper) {
        Player player = wearHive(helper, HiveTier.BASIC, c -> {
            c.setItem(HiveStackContainer.MODULES_START, module(FEEnumHiveModule.COLLECTOR));
            c.setItem(HiveStackContainer.PRODUCTS_START, new ItemStack(Items.HONEYCOMB, 5));
        });
        helper.assertTrue(player.getInventory().countItem(Items.HONEYCOMB) >= 5, "collector should move products to the inventory");
        helper.assertTrue(new HiveStackContainer(player.getItemBySlot(EquipmentSlot.CHEST)).getItem(HiveStackContainer.PRODUCTS_START).isEmpty(),
                "product slot should be empty after collecting");
        helper.succeed();
    }

    /**
     * A princess mates with one drone of a stack: the hive's inventory is read from the item, so the
     * drone Forestry uses up has to be written back (see HiveStackContainer#commitDrone).
     */
    @GameTest(template = "empty", timeoutTicks = 200)
    public static void matingUsesOneDrone(GameTestHelper helper) {
        ItemStack drones = bee(BeeLifeStage.DRONE);
        drones.setCount(3);
        Player player = wearHive(helper, HiveTier.BASIC, c -> {
            c.setQueen(bee(BeeLifeStage.PRINCESS));
            c.setDrone(drones);
        }, 120);  // Forestry's mating takes 100 work ticks
        HiveStackContainer hive = new HiveStackContainer(player.getItemBySlot(EquipmentSlot.CHEST));
        ILifeStage stage = BeeStacks.stage(hive.getQueen());
        helper.assertTrue(stage == BeeLifeStage.QUEEN, "princess should have mated into a queen, got " + stage);
        helper.assertTrue(hive.getDrone().getCount() == 2, "mating should use exactly one drone, left: " + hive.getDrone().getCount());
        helper.succeed();
    }

    /** The upgrade keeps the bees and takes only the previous tier. */
    @GameTest(template = "empty")
    public static void upgradeRecipe(GameTestHelper helper) {
        var level = helper.getLevel();
        var recipe = level.getRecipeManager().byKey(new ResourceLocation(ForestryExtras.MOD_ID, "portable_hive_upgrade_reinforced"))
                .orElseThrow();
        ItemStack hive = new ItemStack(ModItems.PORTABLE_HIVE.get());
        new HiveStackContainer(hive).setQueen(queen());

        CraftingContainer input = upgradeGrid(hive);
        helper.assertTrue(recipe instanceof CraftingRecipe crafting && crafting.matches(input, level), "basic hive + reinforced ingots should match");
        ItemStack out = ((CraftingRecipe) recipe).assemble(input, level.registryAccess());
        helper.assertTrue(HiveTier.of(out) == HiveTier.REINFORCED, "result should be reinforced");
        helper.assertTrue(!new HiveStackContainer(out).getQueen().isEmpty(), "queen must survive the upgrade");
        helper.assertTrue(!((CraftingRecipe) recipe).matches(upgradeGrid(out), level), "a reinforced hive can't be reinforced again");
        helper.succeed();
    }

    /** Chestplate + hive in a crafting grid (as a player lays them out) -> armoured hive, and back. */
    @GameTest(template = "empty")
    public static void chestplateCraftsIntoTheHive(GameTestHelper helper) {
        var level = helper.getLevel();
        ItemStack hive = new ItemStack(ModItems.PORTABLE_HIVE.get());
        CraftingContainer grid = grid(2, 1, new ItemStack(Items.DIAMOND_CHESTPLATE), hive);
        var match = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, grid, level);
        helper.assertTrue(match.isPresent(), "no recipe matches chestplate + hive");
        ItemStack armoured = match.get().assemble(grid, level.registryAccess());
        helper.assertTrue(FEItemPortableHive.isArmored(armoured), "result should be an armoured hive, got " + armoured
                + " from " + match.get().getId());
        helper.assertTrue(armoured.getMaxDamage() == Items.DIAMOND_CHESTPLATE.getMaxDamage(), "the hive takes the chestplate's durability");

        CraftingContainer alone = grid(1, 1, armoured);
        var back = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, alone, level);
        helper.assertTrue(back.isPresent() && !FEItemPortableHive.isArmored(back.get().assemble(alone, level.registryAccess())),
                "an armoured hive alone should give the plain hive back");
        NonNullList<ItemStack> left = back.get().getRemainingItems(alone);
        helper.assertTrue(left.get(0).is(Items.DIAMOND_CHESTPLATE), "the chestplate should stay behind in the grid, got " + left.get(0));
        helper.succeed();
    }

    /** When the built-in chestplate wears out, only the chestplate is lost: the hive, its tier and its bees stay. */
    @GameTest(template = "empty")
    public static void brokenChestplateLeavesThePlainHive(GameTestHelper helper) {
        Player player = helper.makeMockSurvivalPlayer();
        ItemStack hive = HiveTier.withTier(new ItemStack(ModItems.PORTABLE_HIVE.get()), HiveTier.DRACONIC);
        new HiveStackContainer(hive).setQueen(queen());
        ItemStack armoured = FEItemPortableHive.withArmor(hive, new ItemStack(Items.IRON_CHESTPLATE));
        player.setItemSlot(EquipmentSlot.CHEST, armoured);

        armoured.hurtAndBreak(10, player, p -> p.broadcastBreakEvent(EquipmentSlot.CHEST));
        ItemStack worn = player.getItemBySlot(EquipmentSlot.CHEST);
        helper.assertTrue(FEItemPortableHive.isArmored(worn) && worn.getDamageValue() == 10, "chestplate should take the wear first");

        worn.hurtAndBreak(10_000, player, p -> p.broadcastBreakEvent(EquipmentSlot.CHEST));
        player.tick();
        worn = player.getItemBySlot(EquipmentSlot.CHEST);
        helper.assertTrue(worn.getItem() instanceof FEItemPortableHive, "the hive must survive, got " + worn);
        helper.assertTrue(!FEItemPortableHive.isArmored(worn), "the broken chestplate should be gone");
        helper.assertTrue(!worn.isDamageableItem(), "a plain hive has no durability");
        helper.assertTrue(HiveTier.of(worn) == HiveTier.DRACONIC, "tier must be kept");
        helper.assertTrue(!new HiveStackContainer(worn).getQueen().isEmpty(), "bees must be kept");
        helper.succeed();
    }

    private static CraftingContainer upgradeGrid(ItemStack hive) {
        ItemStack ingot = new ItemStack(ModItems.INGOTS.get(FEEnumIngot.REINFORCED).get());
        ItemStack propolis = new ItemStack(ModItems.PROPOLIS.get(FEEnumPropolis.REINFORCED).get());
        return grid(3, 3, ingot, propolis, ingot, ingot, hive, ingot, ingot, ingot, ingot);
    }

    /** A crafting grid outside of any screen (1.20.1 has no CraftingInput yet). */
    private static CraftingContainer grid(int width, int height, ItemStack... items) {
        CraftingContainer grid = new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
            @Override
            public ItemStack quickMoveStack(Player player, int index) {
                return ItemStack.EMPTY;
            }

            @Override
            public boolean stillValid(Player player) {
                return true;
            }
        }, width, height);
        for (int i = 0; i < items.length; i++) {
            grid.setItem(i, items[i].copy());
        }
        return grid;
    }

    private static float productionSpeed(PortableHiveHousing housing) {
        return IForestryApi.INSTANCE.getHiveManager().createBeeHousingModifier(housing).modifyProductionSpeed(null, 1f);
    }

    private static ItemStack module(FEEnumHiveModule type) {
        return new ItemStack(ModItems.HIVE_MODULES.get(type).get());
    }

    private static ItemStack queen() {
        return bee(BeeLifeStage.QUEEN);
    }

    private static ItemStack bee(BeeLifeStage stage) {
        return IForestryApi.INSTANCE.getGeneticManager().getSpeciesType(ForestrySpeciesTypes.BEE)
                .createStack(ForestryBeeSpecies.FOREST, stage);
    }

    private static Player wearHive(GameTestHelper helper, ItemStack flower) {
        return wearHive(helper, HiveTier.BASIC, c -> c.setItem(HiveStackContainer.FLOWER, flower));
    }

    private static Player wearHive(GameTestHelper helper, HiveTier tier, Consumer<HiveStackContainer> setup) {
        return wearHive(helper, tier, c -> {
            c.setQueen(queen());
            setup.accept(c);
        }, 40);
    }

    /**
     * A plain server-side player (no network connection, so nothing tries to send it packets) wearing
     * a hive, ticked {@code ticks} times.
     */
    private static Player wearHive(GameTestHelper helper, HiveTier tier, Consumer<HiveStackContainer> setup, int ticks) {
        Player player = helper.makeMockSurvivalPlayer();
        ItemStack hive = HiveTier.withTier(new ItemStack(ModItems.PORTABLE_HIVE.get()), tier);
        setup.accept(new HiveStackContainer(hive));
        player.setItemSlot(EquipmentSlot.CHEST, hive);
        for (int i = 0; i < ticks; i++) {
            player.tick();
        }
        return player;
    }

    private static Set<IError> errors(Player player) {
        return PortableHiveTicker.get(player).getErrorLogic().getErrors();
    }
}
