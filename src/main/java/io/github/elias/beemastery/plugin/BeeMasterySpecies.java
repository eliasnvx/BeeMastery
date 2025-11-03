package io.github.elias.beemastery.plugin;

import forestry.api.apiculture.IBeeSpecies;
import forestry.api.genetics.ILifeStage;
import net.minecraft.resources.ResourceLocation;

/**
 * Хранилище ссылок на виды пчёл Bee Mastery
 */
public class BeeMasterySpecies {
    public static IBeeSpecies DRACONIC;
    public static IBeeSpecies LEGENDARY;
    public static IBeeSpecies REINFORCED;
    public static IBeeSpecies WITHERIA;
    public static IBeeSpecies MUTATED;
    public static IBeeSpecies COW;
    public static IBeeSpecies PIG;
    public static IBeeSpecies SHEEP;
    public static IBeeSpecies CARROT;
    public static IBeeSpecies POTATO;
    public static IBeeSpecies CLAYIOUS;
    
    public static ResourceLocation id(String name) {
        return new ResourceLocation("beemastery", name);
    }
}
