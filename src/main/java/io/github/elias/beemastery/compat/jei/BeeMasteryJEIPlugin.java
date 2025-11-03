package io.github.elias.beemastery.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import net.minecraft.resources.ResourceLocation;
import io.github.elias.beemastery.ForestryExtras;

@JeiPlugin
public class BeeMasteryJEIPlugin implements IModPlugin {
    
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ForestryExtras.MOD_ID, "jei_plugin");
    }
    
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        // Forestry CE уже регистрирует категории для рамок и палок
        // Наши предметы автоматически появятся в JEI
    }
    
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // Thermal рецепты автоматически появятся в JEI через Thermal Integration
        // Наши рамки и палки автоматически появятся через Forestry CE
    }
}
