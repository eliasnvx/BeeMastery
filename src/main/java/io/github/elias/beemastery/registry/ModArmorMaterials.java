package io.github.elias.beemastery.registry;

import io.github.elias.beemastery.ForestryExtras;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

public class ModArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, ForestryExtras.MOD_ID);

    /**
     * The portable hive's own material: no protection at all. Protection only comes from a
     * chestplate built into it (see FEItemPortableHive), which also supplies the look.
     */
    public static final Holder<ArmorMaterial> PORTABLE_HIVE = ARMOR_MATERIALS.register("portable_hive", () -> new ArmorMaterial(
            new EnumMap<>(java.util.Map.of(
                    ArmorItem.Type.HELMET, 0, ArmorItem.Type.CHESTPLATE, 0,
                    ArmorItem.Type.LEGGINGS, 0, ArmorItem.Type.BOOTS, 0, ArmorItem.Type.BODY, 0)),
            0,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            () -> Ingredient.EMPTY,
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(ForestryExtras.MOD_ID, "portable_hive"))),
            0.0f,
            0.0f));

    public static void register(IEventBus eventBus) {
        ARMOR_MATERIALS.register(eventBus);
    }
}
