package io.github.elias.beemastery.compat.jei;

import io.github.elias.beemastery.ForestryExtras;
import io.github.elias.beemastery.registry.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

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
        // Добавляем информацию о рамках
        var level = registration.getVanillaRecipeFactory().getMinecraft().level;
        if (level != null) {
            var recipeManager = level.getRecipeManager();
            
            // Получаем все крафт рецепты для наших предметов
            var recipes = recipeManager.getAllRecipesFor(RecipeType.CRAFTING);
            
            // Фильтруем только наши рецепты (рамки и палки)
            var ourRecipes = recipes.stream()
                .filter(recipe -> {
                    var result = recipe.value().getResultItem(level.registryAccess());
                    var id = result.getItem().toString();
                    return id.contains("beemastery");
                })
                .map(recipe -> (CraftingRecipe) recipe.value())
                .toList();
            
            // Регистрируем рецепты в JEI
            registration.addRecipes(mezz.jei.api.constants.RecipeTypes.CRAFTING, ourRecipes);
        }
    }
    
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // Thermal рецепты автоматически появятся в JEI через Thermal Integration
    }
}
