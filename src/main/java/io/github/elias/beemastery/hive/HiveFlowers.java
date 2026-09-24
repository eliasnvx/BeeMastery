package io.github.elias.beemastery.hive;

import forestry.api.IForestryApi;
import forestry.api.apiculture.IFlowerType;
import forestry.api.core.genetics.IGenome;
import forestry.api.core.genetics.alleles.BeeChromosomes;
import forestry.core.engine.genetics.flowers.TagFlowerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

/**
 * The portable hive's flower slot: a flower carried in the hive stands in for flowers around it.
 *
 * <p>Almost every Forestry flower type is a {@link TagFlowerType} (a block tag such as
 * {@code forestry:flowers/nether}), so an item qualifies when its block is in the queen's tag.
 * Types that aren't tag-based (e.g. photosynthesis, which wants daylight rather than a plant)
 * can't be satisfied from a slot.
 */
public final class HiveFlowers {

    private HiveFlowers() {
    }

    /** Whether this item is a flower for any bee at all — the slot's input filter. */
    public static boolean isAnyFlower(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem item)) {
            return false;
        }
        for (IFlowerType type : IForestryApi.INSTANCE.getFlowerTypeManager().getAllFlowerTypes().values()) {
            if (type instanceof TagFlowerType tagType && item.getBlock().defaultBlockState().is(tagType.acceptableFlowers())) {
                return true;
            }
        }
        return false;
    }

    /** Whether the flower in the slot is one this bee accepts at this spot (some types are biome-bound). */
    public static boolean suits(ItemStack flower, IGenome genome, Level level, BlockPos pos) {
        if (!(flower.getItem() instanceof BlockItem item) || !(flowerType(genome) instanceof TagFlowerType tagType)) {
            return false;
        }
        return item.getBlock().defaultBlockState().is(tagType.acceptableFlowers())
                && (tagType.biomes() == null || level.getBiome(pos).is(tagType.biomes()));
    }

    /** A flower this bee would accept, for showing in the empty slot; empty if there is none to show. */
    public static ItemStack example(IGenome genome) {
        if (!(flowerType(genome) instanceof TagFlowerType tagType)) {
            return ItemStack.EMPTY;
        }
        return BuiltInRegistries.BLOCK.getTag(tagType.acceptableFlowers()).stream()
                .flatMap(set -> set.stream())
                .map(Holder::value)
                .map(Block::asItem)
                .map(ItemStack::new)
                .filter(stack -> !stack.isEmpty())
                .findFirst()
                .orElse(ItemStack.EMPTY);
    }

    private static IFlowerType flowerType(IGenome genome) {
        return Optional.ofNullable(genome)
                .map(g -> g.getActiveValue(BeeChromosomes.FLOWER_TYPE))
                .map(id -> IForestryApi.INSTANCE.getFlowerTypeManager().getFlowerTypeSafe(id))
                .orElse(null);
    }
}
