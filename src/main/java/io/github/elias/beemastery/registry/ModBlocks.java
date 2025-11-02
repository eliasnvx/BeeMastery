package io.github.elias.beemastery.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import io.github.elias.beemastery.ForestryExtras;

import java.util.function.Supplier;

/**
 * Регистрация блоков для ForestryExtras
 */
public class ModBlocks {
    
    public static final DeferredRegister<Block> BLOCKS = 
        DeferredRegister.create(ForgeRegistries.BLOCKS, ForestryExtras.MOD_ID);
    
    public static final DeferredRegister<Item> BLOCK_ITEMS = 
        DeferredRegister.create(ForgeRegistries.ITEMS, ForestryExtras.MOD_ID);
    
    // Блоки из слитков
    public static final RegistryObject<Block> DRACONIC_BLOCK = register("draconic_block", 
        () -> new Block(BlockBehaviour.Properties.of()
            .strength(5.0f, 6.0f)
            .requiresCorrectToolForDrops()));
    
    public static final RegistryObject<Block> LEGENDARY_BLOCK = register("legendary_block", 
        () -> new Block(BlockBehaviour.Properties.of()
            .strength(7.0f, 8.0f)
            .requiresCorrectToolForDrops()));
    
    public static final RegistryObject<Block> REINFORCED_BLOCK = register("reinforced_block", 
        () -> new Block(BlockBehaviour.Properties.of()
            .strength(4.0f, 5.0f)
            .requiresCorrectToolForDrops()));
    
    public static final RegistryObject<Block> MUTATED_IRON_BLOCK = register("mutated_iron_block", 
        () -> new Block(BlockBehaviour.Properties.of()
            .strength(3.0f, 4.0f)
            .requiresCorrectToolForDrops()));
    
    // Регистрация BlockItem для каждого блока
    public static final RegistryObject<Item> DRACONIC_BLOCK_ITEM = registerBlockItem("draconic_block", DRACONIC_BLOCK);
    public static final RegistryObject<Item> LEGENDARY_BLOCK_ITEM = registerBlockItem("legendary_block", LEGENDARY_BLOCK);
    public static final RegistryObject<Item> REINFORCED_BLOCK_ITEM = registerBlockItem("reinforced_block", REINFORCED_BLOCK);
    public static final RegistryObject<Item> MUTATED_IRON_BLOCK_ITEM = registerBlockItem("mutated_iron_block", MUTATED_IRON_BLOCK);
    
    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }
    
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return BLOCK_ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
    
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ITEMS.register(eventBus);
    }
}
