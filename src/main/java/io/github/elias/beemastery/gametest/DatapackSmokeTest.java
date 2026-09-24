package io.github.elias.beemastery.gametest;

import forestry.api.IForestryApi;
import forestry.api.core.genetics.ForestrySpeciesTypes;
import forestry.api.core.genetics.ISpeciesType;
import io.github.elias.beemastery.ForestryExtras;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.Set;

/**
 * Smoke test run by {@code ./gradlew runGameTestServer} (and CI before every release).
 *
 * <p>Starting the game-test server creates a real world, which is exactly where datapack
 * problems surface: bad recipe JSON, wrong folder names after a version bump, or a
 * dependency crashing during loot/recipe reload. This test then checks that the recipes
 * our progression depends on actually made it into the recipe manager — most of them fail
 * silently otherwise (a broken recipe is just skipped with a log line).
 *
 * <p>Only registered when {@code neoforge.enabledGameTestNamespaces} includes our mod id,
 * so it never runs for players.
 */
@GameTestHolder(ForestryExtras.MOD_ID)
@PrefixGameTestTemplate(false)
public class DatapackSmokeTest {

    private static final List<String> REQUIRED_RECIPES = List.of(
            // Frame tandem
            "frame_reinforced", "frame_draconic", "frame_legendary", "frame_ranch",
            // Tools, belt, guide book
            "grafter_legendary", "aura_belt", "bee_mastery_guide_book",
            "portable_hive", "portable_hive_attach_armor", "portable_hive_detach_armor",
            "portable_hive_upgrade_reinforced", "portable_hive_upgrade_draconic", "portable_hive_upgrade_legendary",
            "hive_module_lantern", "hive_module_canopy", "hive_module_sun", "hive_module_climate", "hive_module_booster",
            "hive_module_longevity", "hive_module_mutagen", "hive_module_stabilizer", "hive_module_collector",
            // Forestry centrifuge (propolis source)
            "centrifuge/draconic_comb",
            // Forestry bee mutations — without these our bees can't be bred at all
            "bee_mutation/reinforced", "bee_mutation/witheria", "bee_mutation/draconic",
            "bee_mutation/legendary", "bee_mutation/mutated", "bee_mutation/clayious",
            "bee_mutation/potato", "bee_mutation/carrot", "bee_mutation/pig",
            "bee_mutation/cow", "bee_mutation/sheep"
    );

    private static final List<String> SPECIES = List.of(
            "reinforced", "witheria", "draconic", "legendary", "mutated",
            "clayious", "potato", "carrot", "pig", "cow", "sheep"
    );

    /**
     * Forestry 3.x rebuilds the bee species list from {@code data/<ns>/bee_species/*.json} on
     * every datapack load and discards anything registered from code, so this is the only
     * place a missing or broken species file shows up.
     */
    @GameTest(template = "empty")
    public static void beeSpeciesLoaded(GameTestHelper helper) {
        ISpeciesType<?, ?> bees = IForestryApi.INSTANCE.getGeneticManager().getSpeciesType(ForestrySpeciesTypes.BEE);
        Set<ResourceLocation> loaded = bees.getAllSpeciesIds();
        for (String name : SPECIES) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(ForestryExtras.MOD_ID, name);
            if (!loaded.contains(id)) {
                helper.fail("Bee species not loaded: " + id + " (" + loaded.size() + " species loaded in total)");
            }
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void requiredRecipesLoaded(GameTestHelper helper) {
        RecipeManager recipes = helper.getLevel().getRecipeManager();
        for (String path : REQUIRED_RECIPES) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(ForestryExtras.MOD_ID, path);
            if (recipes.byKey(id).isEmpty()) {
                helper.fail("Recipe missing after datapack load: " + id);
            }
        }
        helper.succeed();
    }
}
