package io.github.elias.beemastery.plugin;

import forestry.api.apiculture.*;
import forestry.api.plugin.*;
import forestry.api.core.*;
import forestry.api.genetics.ForestryTaxa;
import io.github.elias.beemastery.registry.ModItems;
import io.github.elias.beemastery.item.FEEnumHoneyComb;
import io.github.elias.beemastery.item.FEEnumIngot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Плагин для регистрации пчел ForestryExtras в Forestry CE
 */
public class ForestryExtrasPlugin implements IForestryPlugin {

    @Override
    public ResourceLocation id() {
        System.out.println("ForestryExtrasPlugin: Loading plugin...");
        return new ResourceLocation("beemastery", "plugin");
    }

    @Override
    public void registerApiculture(IApicultureRegistration registration) {
        System.out.println("ForestryExtrasPlugin: Registering bees...");
        // Регистрируем пчел
        registerBees(registration);
        System.out.println("ForestryExtrasPlugin: Bees registered successfully!");
        
        // Регистрируем мутации
        System.out.println("ForestryExtrasPlugin: Registering bee mutations...");
        registerMutations(registration);
        System.out.println("ForestryExtrasPlugin: Bee mutations registered successfully!");
    }

    private void registerBees(IApicultureRegistration registration) {
        try {
            // Draconic Bee
            IBeeSpeciesBuilder draconic = registration.registerSpecies(
                new ResourceLocation("beemastery", "draconic"),
                ForestryTaxa.GENUS_HEROIC,
                "Draconic",
                false,
                TextColor.fromRgb(0x990000) // Dark Red
            );
            
            draconic.setAuthority("beemastery")
                    .setBody(TextColor.fromRgb(0x990000))
                    .setStripes(TextColor.fromRgb(0xFFFFCC))
                    .setTemperature(TemperatureType.HELLISH)
                    .setHumidity(HumidityType.ARID)
                    .addProduct(new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.DRACONIC).get()), 0.12f)
                    .addProduct(new ItemStack(Items.HONEYCOMB), 0.1f)
                    .setComplexity(4)
                    .setGlint(true)
                    .addMutations(mutations -> {
                        mutations.add(new ResourceLocation("beemastery", "witheria"), new ResourceLocation("beemastery", "reinforced"), 5);
                    });
            
            System.out.println("ForestryExtrasPlugin: Draconic bee registered!");
            
            // Legendary Bee  
            IBeeSpeciesBuilder legendary = registration.registerSpecies(
                new ResourceLocation("beemastery", "legendary"),
                ForestryTaxa.GENUS_NOBLE,
                "Legendary",
                false,
                TextColor.fromRgb(0x0000CD) // Medium Blue
            );
            
            legendary.setAuthority("beemastery")
                    .setBody(TextColor.fromRgb(0x0000CD))
                    .setStripes(TextColor.fromRgb(0xFFFFCC))
                    .setTemperature(TemperatureType.HELLISH)
                    .setHumidity(HumidityType.ARID)
                    .addProduct(new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.LEGENDARY).get()), 0.12f)
                    .addProduct(new ItemStack(ModItems.INGOTS.get(FEEnumIngot.LEGENDARY).get()), 0.05f)
                    .addProduct(new ItemStack(Items.HONEYCOMB), 0.1f)
                    .setComplexity(5)
                    .setGlint(true)
                    .addMutations(mutations -> {
                        mutations.add(new ResourceLocation("beemastery", "witheria"), new ResourceLocation("beemastery", "draconic"), 5);
                    });
            
            System.out.println("ForestryExtrasPlugin: Legendary bee registered!");
            
            // Reinforced Bee
            IBeeSpeciesBuilder reinforced = registration.registerSpecies(
                new ResourceLocation("beemastery", "reinforced"),
                ForestryTaxa.GENUS_INDUSTRIOUS,
                "Reinforced",
                false,
                TextColor.fromRgb(0xCCCC99) // Beige
            );
            
            reinforced.setAuthority("beemastery")
                    .setBody(TextColor.fromRgb(0xCCCC99))
                    .setStripes(TextColor.fromRgb(0xFFFFCC))
                    .setTemperature(TemperatureType.NORMAL)
                    .setHumidity(HumidityType.NORMAL)
                    .addProduct(new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.REINFORCED).get()), 0.25f)
                    .addProduct(new ItemStack(ModItems.INGOTS.get(FEEnumIngot.REINFORCED).get()), 0.1f)
                    .addProduct(new ItemStack(Items.HONEYCOMB), 0.1f)
                    .setComplexity(3)
                    .addMutations(mutations -> {
                        mutations.add(ForestryBeeSpecies.VALIANT, ForestryBeeSpecies.NOBLE, 5);
                    });
            
            System.out.println("ForestryExtrasPlugin: Reinforced bee registered!");
            
            // Witheria Bee
            IBeeSpeciesBuilder witheria = registration.registerSpecies(
                new ResourceLocation("beemastery", "witheria"),
                ForestryTaxa.GENUS_INFERNAL,
                "Witheria",
                false,
                TextColor.fromRgb(0x000000) // Black
            );
            
            witheria.setAuthority("beemastery")
                    .setBody(TextColor.fromRgb(0x000000))
                    .setStripes(TextColor.fromRgb(0xFFFFCC))
                    .setTemperature(TemperatureType.HELLISH)
                    .setHumidity(HumidityType.ARID)
                    .addProduct(new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.WITHERIA).get()), 0.12f)
                    .addProduct(new ItemStack(Items.HONEYCOMB), 0.1f)
                    .setComplexity(3)
                    .addMutations(mutations -> {
                        mutations.add(ForestryBeeSpecies.INDUSTRIOUS, new ResourceLocation("beemastery", "mutated"), 5);
                    });
            
            System.out.println("ForestryExtrasPlugin: Witheria bee registered!");
            
            // Mutated Bee
            IBeeSpeciesBuilder mutated = registration.registerSpecies(
                new ResourceLocation("beemastery", "mutated"),
                ForestryTaxa.GENUS_AUSTERE, // Используем существующий genus "modapis"
                "Mutated",
                false,
                TextColor.fromRgb(0x99CC00) // Lime Green
            );
            
            mutated.setAuthority("beemastery")
                    .setBody(TextColor.fromRgb(0x99CC00))
                    .setStripes(TextColor.fromRgb(0xFFFFCC))
                    .setTemperature(TemperatureType.NORMAL)
                    .setHumidity(HumidityType.DAMP)
                    .addProduct(new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.MUTATED).get()), 0.25f)
                    .addProduct(new ItemStack(ModItems.INGOTS.get(FEEnumIngot.MUTATED_IRON).get()), 0.1f)
                    .addProduct(new ItemStack(Items.HONEYCOMB), 0.1f)
                    .setComplexity(3)
                    .addMutations(mutations -> {
                        mutations.add(ForestryBeeSpecies.MAJESTIC, new ResourceLocation("beemastery", "reinforced"), 5);
                    });
            
            System.out.println("ForestryExtrasPlugin: Mutated bee registered!");
            
            // Clayious Bee
            IBeeSpeciesBuilder clayious = registration.registerSpecies(
                new ResourceLocation("beemastery", "clayious"),
                ForestryTaxa.GENUS_INDUSTRIOUS, // Используем существующий genus "industrapis"
                "Clayious",
                false,
                TextColor.fromRgb(0xB0C4DE) // Light Steel Blue
            );
            
            clayious.setAuthority("beemastery")
                    .setBody(TextColor.fromRgb(0xA0522D))
                    .setStripes(TextColor.fromRgb(0xD2691E))
                    .setTemperature(TemperatureType.NORMAL)
                    .setHumidity(HumidityType.NORMAL)
                    .addProduct(new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.CLAYIOUS).get()), 0.12f)
                    .addProduct(new ItemStack(Items.HONEYCOMB), 0.1f)
                    .setComplexity(1)
                    .addMutations(mutations -> {
                        mutations.add(ForestryBeeSpecies.MEADOWS, ForestryBeeSpecies.NOBLE, 15);
                    });
            
            System.out.println("ForestryExtrasPlugin: Clayious bee registered!");
            
            // Pig Bee
            IBeeSpeciesBuilder pig = registration.registerSpecies(
                new ResourceLocation("beemastery", "pig"),
                ForestryTaxa.GENUS_HONEY, // Используем существующий genus "apis"
                "Pig",
                false,
                TextColor.fromRgb(0xFF69B4) // Hot Pink
            );
            
            pig.setAuthority("beemastery")
                    .setBody(TextColor.fromRgb(0xFFC0CB))
                    .setStripes(TextColor.fromRgb(0xFFB6C1))
                    .setTemperature(TemperatureType.NORMAL)
                    .setHumidity(HumidityType.NORMAL)
                    .addProduct(new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.PIG).get()), 0.5f)
                    .addProduct(new ItemStack(Items.HONEYCOMB), 0.1f)
                    .setComplexity(1)
                    .addMutations(mutations -> {
                        mutations.add(ForestryBeeSpecies.STEADFAST, new ResourceLocation("beemastery", "carrot"), 30);
                    });
            
            System.out.println("ForestryExtrasPlugin: Pig bee registered!");
            
            // Cow Bee
            IBeeSpeciesBuilder cow = registration.registerSpecies(
                new ResourceLocation("beemastery", "cow"),
                ForestryTaxa.GENUS_HONEY, // Используем существующий genus "apis"
                "Cow",
                false,
                TextColor.fromRgb(0x8B4513) // Saddle Brown
            );
            
            cow.setAuthority("beemastery")
                    .setBody(TextColor.fromRgb(0x8B4513))
                    .setStripes(TextColor.fromRgb(0xFFFFFF))
                    .setTemperature(TemperatureType.NORMAL)
                    .setHumidity(HumidityType.NORMAL)
                    .addProduct(new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.COW).get()), 0.5f)
                    .addProduct(new ItemStack(Items.HONEYCOMB), 0.1f)
                    .setComplexity(1)
                    .addMutations(mutations -> {
                        mutations.add(ForestryBeeSpecies.STEADFAST, new ResourceLocation("beemastery", "potato"), 30);
                    });
            
            System.out.println("ForestryExtrasPlugin: Cow bee registered!");
            
            // Sheep Bee
            IBeeSpeciesBuilder sheep = registration.registerSpecies(
                new ResourceLocation("beemastery", "sheep"),
                ForestryTaxa.GENUS_HONEY, // Используем существующий genus "apis"
                "Sheep",
                false,
                TextColor.fromRgb(0xFFFFFF) // White
            );
            
            sheep.setAuthority("beemastery")
                    .setBody(TextColor.fromRgb(0xFFFFFF))
                    .setStripes(TextColor.fromRgb(0xE9967A))
                    .setTemperature(TemperatureType.NORMAL)
                    .setHumidity(HumidityType.NORMAL)
                    .addProduct(new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.SHEEP).get()), 0.5f)
                    .addProduct(new ItemStack(Items.HONEYCOMB), 0.1f)
                    .setComplexity(1)
                    .addMutations(mutations -> {
                        mutations.add(new ResourceLocation("beemastery", "carrot"), new ResourceLocation("beemastery", "potato"), 30);
                    });
            
            System.out.println("ForestryExtrasPlugin: Sheep bee registered!");
            
            // Potato Bee
            IBeeSpeciesBuilder potato = registration.registerSpecies(
                new ResourceLocation("beemastery", "potato"),
                ForestryTaxa.GENUS_HONEY, // Используем существующий genus "apis"
                "Potato",
                false,
                TextColor.fromRgb(0xEEE8AA) // Pale Goldenrod
            );
            
            potato.setAuthority("beemastery")
                    .setBody(TextColor.fromRgb(0xEEE8AA))
                    .setStripes(TextColor.fromRgb(0xF0E68C))
                    .setTemperature(TemperatureType.NORMAL)
                    .setHumidity(HumidityType.NORMAL)
                    .addProduct(new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.POTATO).get()), 0.12f)
                    .addProduct(new ItemStack(Items.HONEYCOMB), 0.1f)
                    .setComplexity(1)
                    .addMutations(mutations -> {
                        mutations.add(ForestryBeeSpecies.FOREST, ForestryBeeSpecies.MEADOWS, 15);
                    });
            
            System.out.println("ForestryExtrasPlugin: Potato bee registered!");
            
            // Carrot Bee
            IBeeSpeciesBuilder carrot = registration.registerSpecies(
                new ResourceLocation("beemastery", "carrot"),
                ForestryTaxa.GENUS_HONEY, // Используем существующий genus "apis"
                "Carrot",
                false,
                TextColor.fromRgb(0xFFA500) // Orange
            );
            
            carrot.setAuthority("beemastery")
                    .setBody(TextColor.fromRgb(0xFFA500))
                    .setStripes(TextColor.fromRgb(0xFFA500))
                    .setTemperature(TemperatureType.NORMAL)
                    .setHumidity(HumidityType.NORMAL)
                    .addProduct(new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.CARROT).get()), 0.12f)
                    .addProduct(new ItemStack(Items.HONEYCOMB), 0.1f)
                    .setComplexity(1)
                    .addMutations(mutations -> {
                        mutations.add(ForestryBeeSpecies.FOREST, ForestryBeeSpecies.MEADOWS, 15);
                    });
            
            System.out.println("ForestryExtrasPlugin: Carrot bee registered!");
            
        } catch (Exception e) {
            System.err.println("ForestryExtrasPlugin: Error registering bees: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void registerMutations(IApicultureRegistration registration) {
        // Мутации регистрируются через .addMutations() в каждом виде
        // См. registerBees() выше
        System.out.println("ForestryExtrasPlugin: Mutations are registered via .addMutations() in species builders");
    }
}
