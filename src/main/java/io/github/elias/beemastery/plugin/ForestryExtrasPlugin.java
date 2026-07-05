package io.github.elias.beemastery.plugin;

import com.mojang.logging.LogUtils;
import forestry.api.apiculture.*;
import forestry.api.plugin.*;
import forestry.api.core.*;
import forestry.api.genetics.ForestryTaxa;
import forestry.api.apiculture.genetics.IBeeEffect;
import forestry.api.genetics.alleles.BeeChromosomes;
import forestry.api.genetics.alleles.ForestryAlleles;
import forestry.api.genetics.alleles.IRegistryAllele;
import io.github.elias.beemastery.effect.AuraBeeEffect;
import io.github.elias.beemastery.item.FEEnumAura;
import io.github.elias.beemastery.registry.ModItems;
import io.github.elias.beemastery.item.FEEnumHoneyComb;
import io.github.elias.beemastery.item.FEEnumNugget;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;

/**
 * Плагин для регистрации пчел ForestryExtras в Forestry CE
 */
public class ForestryExtrasPlugin implements IForestryPlugin {

    private static final Logger LOGGER = LogUtils.getLogger();

    /** Functional block that registers a single species, so failures can be isolated. */
    @FunctionalInterface
    private interface SpeciesRegistration {
        void register(IApicultureRegistration registration);
    }

    @Override
    public ResourceLocation id() {
        return new ResourceLocation("beemastery", "plugin");
    }

    @Override
    public void registerApiculture(IApicultureRegistration registration) {
        LOGGER.info("[BeeMastery] registerApiculture called - registering auras and bees...");
        try {
            registerAuras(registration);
        } catch (Throwable t) {
            LOGGER.error("[BeeMastery] Failed to register aura effects", t);
        }
        registerBees(registration);
        LOGGER.info("[BeeMastery] Apiculture registration finished.");
    }

    /**
     * Registers one species inside its own guard, so a failure in one bee never aborts
     * the registration of the others (previously a single throw hid ALL bees).
     */
    private void registerOne(IApicultureRegistration registration, String name, SpeciesRegistration reg) {
        try {
            reg.register(registration);
            LOGGER.info("[BeeMastery] Registered bee: {}", name);
        } catch (Throwable t) {
            LOGGER.error("[BeeMastery] FAILED to register bee '{}'", name, t);
        }
    }

    private void registerBees(IApicultureRegistration registration) {
        // Draconic Bee
        registerOne(registration, "draconic", r -> {
            IBeeSpeciesBuilder draconic = r.registerSpecies(
                new ResourceLocation("beemastery", "draconic"),
                ForestryTaxa.GENUS_HEROIC,
                "Draconic",
                false,
                TextColor.fromRgb(0x990000));
            draconic.setAuthority("beemastery")
                    .setGenome(g -> g.set(BeeChromosomes.EFFECT, auraAllele(FEEnumAura.DRACONIC)))
                    .setBody(TextColor.fromRgb(0x990000))
                    .setStripes(TextColor.fromRgb(0xFFFFCC))
                    .setTemperature(TemperatureType.HELLISH)
                    .setHumidity(HumidityType.ARID)
                    .addProduct(new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.DRACONIC).get()), 0.12f)
                    .addProduct(new ItemStack(ModItems.NUGGETS.get(FEEnumNugget.DRACONIC).get()), 0.05f)
                    .addProduct(new ItemStack(Items.HONEYCOMB), 0.1f)
                    .setComplexity(4)
                    .setGlint(true)
                    .addMutations(mutations -> mutations.add(
                            new ResourceLocation("beemastery", "witheria"),
                            new ResourceLocation("beemastery", "reinforced"), 5));
        });

        // Legendary Bee
        registerOne(registration, "legendary", r -> {
            IBeeSpeciesBuilder legendary = r.registerSpecies(
                new ResourceLocation("beemastery", "legendary"),
                ForestryTaxa.GENUS_NOBLE,
                "Legendary",
                false,
                TextColor.fromRgb(0x0000CD));
            legendary.setAuthority("beemastery")
                    .setGenome(g -> g.set(BeeChromosomes.EFFECT, auraAllele(FEEnumAura.LEGENDARY)))
                    .setBody(TextColor.fromRgb(0x0000CD))
                    .setStripes(TextColor.fromRgb(0xFFFFCC))
                    .setTemperature(TemperatureType.HELLISH)
                    .setHumidity(HumidityType.ARID)
                    .addProduct(new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.LEGENDARY).get()), 0.12f)
                    .addProduct(new ItemStack(ModItems.NUGGETS.get(FEEnumNugget.LEGENDARY).get()), 0.05f)
                    .addProduct(new ItemStack(Items.HONEYCOMB), 0.1f)
                    .setComplexity(5)
                    .setGlint(true)
                    .addMutations(mutations -> mutations.add(
                            new ResourceLocation("beemastery", "witheria"),
                            new ResourceLocation("beemastery", "draconic"), 5));
        });

        // Reinforced Bee
        registerOne(registration, "reinforced", r -> {
            IBeeSpeciesBuilder reinforced = r.registerSpecies(
                new ResourceLocation("beemastery", "reinforced"),
                ForestryTaxa.GENUS_INDUSTRIOUS,
                "Reinforced",
                false,
                TextColor.fromRgb(0xCCCC99));
            reinforced.setAuthority("beemastery")
                    .setBody(TextColor.fromRgb(0xCCCC99))
                    .setStripes(TextColor.fromRgb(0xFFFFCC))
                    .setTemperature(TemperatureType.NORMAL)
                    .setHumidity(HumidityType.NORMAL)
                    .addProduct(new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.REINFORCED).get()), 0.25f)
                    .addProduct(new ItemStack(ModItems.NUGGETS.get(FEEnumNugget.REINFORCED).get()), 0.1f)
                    .addProduct(new ItemStack(Items.HONEYCOMB), 0.1f)
                    .setComplexity(3)
                    .addMutations(mutations -> mutations.add(
                            ForestryBeeSpecies.VALIANT, ForestryBeeSpecies.NOBLE, 5));
        });

        // Witheria Bee
        registerOne(registration, "witheria", r -> {
            IBeeSpeciesBuilder witheria = r.registerSpecies(
                new ResourceLocation("beemastery", "witheria"),
                ForestryTaxa.GENUS_INFERNAL,
                "Witheria",
                false,
                TextColor.fromRgb(0x000000));
            witheria.setAuthority("beemastery")
                    .setGenome(g -> g.set(BeeChromosomes.EFFECT, auraAllele(FEEnumAura.WITHERIA)))
                    .setBody(TextColor.fromRgb(0x000000))
                    .setStripes(TextColor.fromRgb(0xFFFFCC))
                    .setTemperature(TemperatureType.HELLISH)
                    .setHumidity(HumidityType.ARID)
                    .addProduct(new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.WITHERIA).get()), 0.12f)
                    .addProduct(new ItemStack(ModItems.NUGGETS.get(FEEnumNugget.WITHERIA).get()), 0.05f)
                    .addProduct(new ItemStack(Items.HONEYCOMB), 0.1f)
                    .setComplexity(3)
                    .addMutations(mutations -> mutations.add(
                            ForestryBeeSpecies.INDUSTRIOUS,
                            new ResourceLocation("beemastery", "mutated"), 5));
        });

        // Mutated Bee
        registerOne(registration, "mutated", r -> {
            IBeeSpeciesBuilder mutated = r.registerSpecies(
                new ResourceLocation("beemastery", "mutated"),
                ForestryTaxa.GENUS_AUSTERE,
                "Mutated",
                false,
                TextColor.fromRgb(0x99CC00));
            mutated.setAuthority("beemastery")
                    .setGenome(g -> g.set(BeeChromosomes.EFFECT, auraAllele(FEEnumAura.MUTAGENIC)))
                    .setBody(TextColor.fromRgb(0x99CC00))
                    .setStripes(TextColor.fromRgb(0xFFFFCC))
                    .setTemperature(TemperatureType.NORMAL)
                    .setHumidity(HumidityType.DAMP)
                    .addProduct(new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.MUTATED).get()), 0.25f)
                    .addProduct(new ItemStack(ModItems.NUGGETS.get(FEEnumNugget.MUTATED_IRON).get()), 0.1f)
                    .addProduct(new ItemStack(Items.HONEYCOMB), 0.1f)
                    .setComplexity(3)
                    .addMutations(mutations -> mutations.add(
                            ForestryBeeSpecies.MAJESTIC,
                            new ResourceLocation("beemastery", "reinforced"), 5));
        });

        // Clayious Bee
        registerOne(registration, "clayious", r -> {
            IBeeSpeciesBuilder clayious = r.registerSpecies(
                new ResourceLocation("beemastery", "clayious"),
                ForestryTaxa.GENUS_INDUSTRIOUS,
                "Clayious",
                false,
                TextColor.fromRgb(0xB0C4DE));
            clayious.setAuthority("beemastery")
                    .setBody(TextColor.fromRgb(0xA0522D))
                    .setStripes(TextColor.fromRgb(0xD2691E))
                    .setTemperature(TemperatureType.NORMAL)
                    .setHumidity(HumidityType.NORMAL)
                    .addProduct(new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.CLAYIOUS).get()), 0.12f)
                    .addProduct(new ItemStack(Items.HONEYCOMB), 0.1f)
                    .setComplexity(1)
                    .addMutations(mutations -> mutations.add(
                            ForestryBeeSpecies.MEADOWS, ForestryBeeSpecies.NOBLE, 15));
        });

        // Pig Bee
        registerOne(registration, "pig", r -> {
            IBeeSpeciesBuilder pig = r.registerSpecies(
                new ResourceLocation("beemastery", "pig"),
                ForestryTaxa.GENUS_HONEY,
                "Pig",
                false,
                TextColor.fromRgb(0xFF69B4));
            pig.setAuthority("beemastery")
                    .setBody(TextColor.fromRgb(0xFFC0CB))
                    .setStripes(TextColor.fromRgb(0xFFB6C1))
                    .setTemperature(TemperatureType.NORMAL)
                    .setHumidity(HumidityType.NORMAL)
                    .addProduct(new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.PIG).get()), 0.5f)
                    .addProduct(new ItemStack(Items.HONEYCOMB), 0.1f)
                    .setComplexity(1)
                    .addMutations(mutations -> mutations.add(
                            ForestryBeeSpecies.STEADFAST,
                            new ResourceLocation("beemastery", "carrot"), 30));
        });

        // Cow Bee
        registerOne(registration, "cow", r -> {
            IBeeSpeciesBuilder cow = r.registerSpecies(
                new ResourceLocation("beemastery", "cow"),
                ForestryTaxa.GENUS_HONEY,
                "Cow",
                false,
                TextColor.fromRgb(0x8B4513));
            cow.setAuthority("beemastery")
                    .setBody(TextColor.fromRgb(0x8B4513))
                    .setStripes(TextColor.fromRgb(0xFFFFFF))
                    .setTemperature(TemperatureType.NORMAL)
                    .setHumidity(HumidityType.NORMAL)
                    .addProduct(new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.COW).get()), 0.5f)
                    .addProduct(new ItemStack(Items.HONEYCOMB), 0.1f)
                    .setComplexity(1)
                    .addMutations(mutations -> mutations.add(
                            ForestryBeeSpecies.STEADFAST,
                            new ResourceLocation("beemastery", "potato"), 30));
        });

        // Sheep Bee
        registerOne(registration, "sheep", r -> {
            IBeeSpeciesBuilder sheep = r.registerSpecies(
                new ResourceLocation("beemastery", "sheep"),
                ForestryTaxa.GENUS_HONEY,
                "Sheep",
                false,
                TextColor.fromRgb(0xFFFFFF));
            sheep.setAuthority("beemastery")
                    .setBody(TextColor.fromRgb(0xFFFFFF))
                    .setStripes(TextColor.fromRgb(0xE9967A))
                    .setTemperature(TemperatureType.NORMAL)
                    .setHumidity(HumidityType.NORMAL)
                    .addProduct(new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.SHEEP).get()), 0.5f)
                    .addProduct(new ItemStack(Items.HONEYCOMB), 0.1f)
                    .setComplexity(1)
                    .addMutations(mutations -> mutations.add(
                            new ResourceLocation("beemastery", "carrot"),
                            new ResourceLocation("beemastery", "potato"), 30));
        });

        // Potato Bee
        registerOne(registration, "potato", r -> {
            IBeeSpeciesBuilder potato = r.registerSpecies(
                new ResourceLocation("beemastery", "potato"),
                ForestryTaxa.GENUS_HONEY,
                "Potato",
                false,
                TextColor.fromRgb(0xEEE8AA));
            potato.setAuthority("beemastery")
                    .setBody(TextColor.fromRgb(0xEEE8AA))
                    .setStripes(TextColor.fromRgb(0xF0E68C))
                    .setTemperature(TemperatureType.NORMAL)
                    .setHumidity(HumidityType.NORMAL)
                    .addProduct(new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.POTATO).get()), 0.12f)
                    .addProduct(new ItemStack(Items.HONEYCOMB), 0.1f)
                    .setComplexity(1)
                    .addMutations(mutations -> mutations.add(
                            ForestryBeeSpecies.FOREST, ForestryBeeSpecies.MEADOWS, 15));
        });

        // Carrot Bee
        registerOne(registration, "carrot", r -> {
            IBeeSpeciesBuilder carrot = r.registerSpecies(
                new ResourceLocation("beemastery", "carrot"),
                ForestryTaxa.GENUS_HONEY,
                "Carrot",
                false,
                TextColor.fromRgb(0xFFA500));
            carrot.setAuthority("beemastery")
                    .setGenome(g -> g.set(BeeChromosomes.EFFECT, auraAllele(FEEnumAura.HARVEST)))
                    .setBody(TextColor.fromRgb(0xFFA500))
                    .setStripes(TextColor.fromRgb(0xFFA500))
                    .setTemperature(TemperatureType.NORMAL)
                    .setHumidity(HumidityType.NORMAL)
                    .addProduct(new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.CARROT).get()), 0.12f)
                    .addProduct(new ItemStack(Items.HONEYCOMB), 0.1f)
                    .setComplexity(1)
                    .addMutations(mutations -> mutations.add(
                            ForestryBeeSpecies.FOREST, ForestryBeeSpecies.MEADOWS, 15));
        });
    }

    /**
     * Registers one {@link AuraBeeEffect} per aura under id {@code beemastery:<aura>_aura}.
     */
    private void registerAuras(IApicultureRegistration registration) {
        for (FEEnumAura aura : FEEnumAura.VALUES) {
            registration.registerBeeEffect(auraId(aura), new AuraBeeEffect(aura));
        }
        LOGGER.info("[BeeMastery] Registered {} aura effects.", FEEnumAura.VALUES.length);
    }

    private static ResourceLocation auraId(FEEnumAura aura) {
        return new ResourceLocation("beemastery", aura.getSerializedName() + "_aura");
    }

    /** Resolves the EFFECT-chromosome allele for a registered aura, for use in setGenome. */
    private static IRegistryAllele<IBeeEffect> auraAllele(FEEnumAura aura) {
        return ForestryAlleles.REGISTRY.registryAllele(auraId(aura), BeeChromosomes.EFFECT);
    }
}
