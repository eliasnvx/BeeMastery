package io.github.elias.beemastery.registry;

import io.github.elias.beemastery.ForestryExtras;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Armour materials. On 1.20.1 {@link ArmorMaterial} is a plain interface rather than a registry
 * entry, so there is nothing to register.
 */
public enum ModArmorMaterials implements ArmorMaterial {
    /**
     * The portable hive's own material: no protection at all. Protection only comes from a
     * chestplate built into it (see FEItemPortableHive), which also supplies the look.
     * Its own texture is {@code textures/models/armor/portable_hive_layer_1.png} (leather straps).
     */
    PORTABLE_HIVE(ForestryExtras.MOD_ID + ":portable_hive");

    private final String name;

    ModArmorMaterials(String name) {
        this.name = name;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return 0;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return 0;
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_LEATHER;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.EMPTY;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getToughness() {
        return 0.0f;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.0f;
    }
}
