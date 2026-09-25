package io.github.elias.beemastery.hive;

import com.mojang.logging.LogUtils;
import forestry.api.apiculture.IFlowerType;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.alleles.BeeChromosomes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The portable hive's flower slot: a flower carried in the hive stands in for flowers around it.
 *
 * <p>Almost every Forestry flower type accepts the blocks of one block tag (such as
 * {@code forestry:flowers/nether}), so an item qualifies when its block is in the queen's tag.
 * Types that aren't tag-based (e.g. photosynthesis, which wants daylight rather than a plant)
 * can't be satisfied from a slot.
 *
 * <p>Forestry 2.5 keeps that tag in a private field of its flower type classes
 * ({@code FlowerType}, {@code EndFlowerType}, {@code WaterFlowerType}) with no getter, so it is
 * read reflectively once per type. Forestry's own classes are not obfuscated, so the lookup
 * works the same in development and in a packed game; a type without such a field simply
 * counts as "not tag-based".
 */
public final class HiveFlowers {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<IFlowerType, Optional<TagKey<Block>>> TAGS = new ConcurrentHashMap<>();

    private HiveFlowers() {
    }

    /** Whether this item is a flower for any bee at all — the slot's input filter. */
    public static boolean isAnyFlower(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem item)) {
            return false;
        }
        BlockState state = item.getBlock().defaultBlockState();
        for (IFlowerType type : BeeChromosomes.FLOWER_TYPE.values()) {
            TagKey<Block> tag = tagOf(type);
            if (tag != null && state.is(tag)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Whether the flower in the slot is one this bee accepts. In Forestry 2.5 no flower type is
     * restricted by biome (the End type only <em>widens</em> its tag with End biomes), so the tag
     * decides on its own.
     */
    public static boolean suits(ItemStack flower, @Nullable IGenome genome) {
        TagKey<Block> tag = genome == null ? null : tagOf(genome.getActiveValue(BeeChromosomes.FLOWER_TYPE));
        return tag != null && flower.getItem() instanceof BlockItem item && item.getBlock().defaultBlockState().is(tag);
    }

    /** A flower this bee would accept, for showing in the empty slot; empty if there is none to show. */
    public static ItemStack example(@Nullable IGenome genome) {
        TagKey<Block> tag = genome == null ? null : tagOf(genome.getActiveValue(BeeChromosomes.FLOWER_TYPE));
        if (tag == null) {
            return ItemStack.EMPTY;
        }
        return BuiltInRegistries.BLOCK.getTag(tag).stream()
                .flatMap(set -> set.stream())
                .map(Holder::value)
                .map(Block::asItem)
                .map(ItemStack::new)
                .filter(stack -> !stack.isEmpty())
                .findFirst()
                .orElse(ItemStack.EMPTY);
    }

    @Nullable
    private static TagKey<Block> tagOf(@Nullable IFlowerType type) {
        return type == null ? null : TAGS.computeIfAbsent(type, HiveFlowers::findTag).orElse(null);
    }

    @SuppressWarnings("unchecked")
    private static Optional<TagKey<Block>> findTag(IFlowerType type) {
        for (Class<?> c = type.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                if (TagKey.class.isAssignableFrom(field.getType()) && !Modifier.isStatic(field.getModifiers())) {
                    try {
                        field.setAccessible(true);
                        if (field.get(type) instanceof TagKey<?> tag && tag.isFor(BuiltInRegistries.BLOCK.key())) {
                            return Optional.of((TagKey<Block>) tag);
                        }
                    } catch (ReflectiveOperationException | RuntimeException e) {
                        LOGGER.debug("Can't read the flower tag of {}", type, e);
                    }
                }
            }
        }
        return Optional.empty();
    }
}
