package io.github.elias.beemastery.registry;

import io.github.elias.beemastery.ForestryExtras;
import io.github.elias.beemastery.hive.PortableHiveArmorRecipes;
import io.github.elias.beemastery.hive.PortableHiveUpgradeRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, ForestryExtras.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<PortableHiveArmorRecipes.Attach>> PORTABLE_HIVE_ATTACH_ARMOR =
            SERIALIZERS.register("portable_hive_attach_armor", () -> new SimpleCraftingRecipeSerializer<>(PortableHiveArmorRecipes.Attach::new));

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<PortableHiveArmorRecipes.Detach>> PORTABLE_HIVE_DETACH_ARMOR =
            SERIALIZERS.register("portable_hive_detach_armor", () -> new SimpleCraftingRecipeSerializer<>(PortableHiveArmorRecipes.Detach::new));

    public static final DeferredHolder<RecipeSerializer<?>, PortableHiveUpgradeRecipe.Serializer> PORTABLE_HIVE_UPGRADE =
            SERIALIZERS.register("portable_hive_upgrade", PortableHiveUpgradeRecipe.Serializer::new);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
