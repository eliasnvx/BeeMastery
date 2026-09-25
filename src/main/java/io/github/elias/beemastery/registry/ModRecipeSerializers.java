package io.github.elias.beemastery.registry;

import io.github.elias.beemastery.ForestryExtras;
import io.github.elias.beemastery.hive.PortableHiveArmorRecipes;
import io.github.elias.beemastery.hive.PortableHiveUpgradeRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ForestryExtras.MOD_ID);

    public static final RegistryObject<SimpleCraftingRecipeSerializer<PortableHiveArmorRecipes.Attach>> PORTABLE_HIVE_ATTACH_ARMOR =
            SERIALIZERS.register("portable_hive_attach_armor", () -> new SimpleCraftingRecipeSerializer<>(PortableHiveArmorRecipes.Attach::new));

    public static final RegistryObject<SimpleCraftingRecipeSerializer<PortableHiveArmorRecipes.Detach>> PORTABLE_HIVE_DETACH_ARMOR =
            SERIALIZERS.register("portable_hive_detach_armor", () -> new SimpleCraftingRecipeSerializer<>(PortableHiveArmorRecipes.Detach::new));

    public static final RegistryObject<PortableHiveUpgradeRecipe.Serializer> PORTABLE_HIVE_UPGRADE =
            SERIALIZERS.register("portable_hive_upgrade", PortableHiveUpgradeRecipe.Serializer::new);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
