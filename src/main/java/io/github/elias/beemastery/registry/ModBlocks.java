package io.github.elias.beemastery.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import io.github.elias.beemastery.ForestryExtras;

import java.util.function.Supplier;

/**
 * Регистрация блоков для ForestryExtras
 */
public class ModBlocks {
    
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ForestryExtras.MOD_ID);
    
    public static final DeferredRegister.Items BLOCK_ITEMS = DeferredRegister.createItems(ForestryExtras.MOD_ID);
    
    // Блоки из слитков
    public static final DeferredBlock<Block> DRACONIC_BLOCK = register("draconic_block",
        () -> new Block(BlockBehaviour.Properties.of()
            .strength(5.0f, 6.0f)
            .lightLevel(state -> 4)
            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> LEGENDARY_BLOCK = register("legendary_block",
        () -> new Block(BlockBehaviour.Properties.of()
            .strength(7.0f, 8.0f)
            .lightLevel(state -> 4)
            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> REINFORCED_BLOCK = register("reinforced_block",
        () -> new Block(BlockBehaviour.Properties.of()
            .strength(4.0f, 5.0f)
            .lightLevel(state -> 4)
            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> WITHERIA_BLOCK = register("witheria_block",
        () -> new Block(BlockBehaviour.Properties.of()
            .strength(4.5f, 5.5f)
            .lightLevel(state -> 4)
            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> MUTATED_IRON_BLOCK = register("mutated_iron_block",
        () -> new Block(BlockBehaviour.Properties.of()
            .strength(3.0f, 4.0f)
            .lightLevel(state -> 4)
            .requiresCorrectToolForDrops()));
    
    // Регистрация BlockItem для каждого блока
    public static final DeferredItem<Item> DRACONIC_BLOCK_ITEM = registerBlockItem("draconic_block", DRACONIC_BLOCK);
    public static final DeferredItem<Item> LEGENDARY_BLOCK_ITEM = registerBlockItem("legendary_block", LEGENDARY_BLOCK);
    public static final DeferredItem<Item> REINFORCED_BLOCK_ITEM = registerBlockItem("reinforced_block", REINFORCED_BLOCK);
    public static final DeferredItem<Item> WITHERIA_BLOCK_ITEM = registerBlockItem("witheria_block", WITHERIA_BLOCK);
    public static final DeferredItem<Item> MUTATED_IRON_BLOCK_ITEM = registerBlockItem("mutated_iron_block", MUTATED_IRON_BLOCK);
    
    private static <T extends Block> DeferredBlock<T> register(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }
    
    private static <T extends Block> DeferredItem<Item> registerBlockItem(String name, DeferredBlock<T> block) {
        return BLOCK_ITEMS.registerItem(name, properties -> new BlockItem(block.get(), properties));
    }
    
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ITEMS.register(eventBus);
    }
}
