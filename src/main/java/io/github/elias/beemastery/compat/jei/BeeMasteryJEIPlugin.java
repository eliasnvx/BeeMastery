package io.github.elias.beemastery.compat.jei;

import io.github.elias.beemastery.ForestryExtras;
import io.github.elias.beemastery.item.FEEnumAura;
import io.github.elias.beemastery.registry.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

@JeiPlugin
public class BeeMasteryJEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ForestryExtras.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new AuraJeiCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // JEI автоматически загружает все крафт рецепты из datapack
        // Наши рецепты рамок и палок будут показаны автоматически
        // если они правильно зарегистрированы в data/beemastery/recipes/
        registration.addRecipes(AuraJeiCategory.TYPE, Arrays.asList(FEEnumAura.VALUES));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // Thermal рецепты автоматически появятся в JEI через Thermal Integration
        registration.addRecipeCatalyst(new ItemStack(ModItems.AURA_BELT.get()), AuraJeiCategory.TYPE);
        for (FEEnumAura aura : FEEnumAura.VALUES) {
            registration.addRecipeCatalyst(new ItemStack(ModItems.AURA_CHARMS.get(aura).get()), AuraJeiCategory.TYPE);
        }
    }
}
