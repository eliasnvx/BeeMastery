package io.github.elias.forestryextras.registry;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import io.github.elias.forestryextras.ForestryExtras;

import java.util.function.Supplier;

/**
 * Регистрация предметов для ForestryExtras
 */
public class ModItems {
    
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(ForgeRegistries.ITEMS, ForestryExtras.MOD_ID);
    
    // Соты для всех пчел
    public static final RegistryObject<Item> POTATO_COMB = register("potato_comb", 
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> CARROT_COMB = register("carrot_comb", 
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> DRACONIC_COMB = register("draconic_comb", 
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> LEGENDARY_COMB = register("legendary_comb", 
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> REINFORCED_COMB = register("reinforced_comb", 
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> WITHERIA_COMB = register("witheria_comb", 
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> MUTATED_COMB = register("mutated_comb", 
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> CLAYIOUS_COMB = register("clayious_comb", 
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> PIG_COMB = register("pig_comb", 
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> COW_COMB = register("cow_comb", 
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> SHEEP_COMB = register("sheep_comb", 
        () -> new Item(new Item.Properties()));
    
    // Слитки
    public static final RegistryObject<Item> DRACONIC_INGOT = register("draconic_ingot", 
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> LEGENDARY_INGOT = register("legendary_ingot", 
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> REINFORCED_INGOT = register("reinforced_ingot", 
        () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> MUTATED_IRON_INGOT = register("mutated_iron_ingot", 
        () -> new Item(new Item.Properties()));
    
    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }
    
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
