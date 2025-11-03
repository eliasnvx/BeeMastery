package io.github.elias.beemastery.compat.jei;

import io.github.elias.beemastery.ForestryExtras;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class BeeMasteryJEIPlugin implements IModPlugin {
    
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ForestryExtras.MOD_ID, "jei_plugin");
    }
    
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        // Forestry CE уже регистрирует категории для рамок
    }
    
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // JEI автоматически загружает все крафт рецепты из datapack
        // Наши рецепты рамок и палок будут показаны автоматически
        // если они правильно зарегистрированы в data/beemastery/recipes/
    }
    
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // Thermal рецепты автоматически появятся в JEI через Thermal Integration
    }
}
