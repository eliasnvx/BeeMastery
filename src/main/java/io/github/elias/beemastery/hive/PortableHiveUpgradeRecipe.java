package io.github.elias.beemastery.hive;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.elias.beemastery.item.FEItemPortableHive;
import io.github.elias.beemastery.registry.ModItems;
import io.github.elias.beemastery.registry.ModRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;

/**
 * Upgrades a portable hive to the next tier. A shaped recipe in every other respect — so JEI and
 * the recipe book show it like any other — but the hive keeps everything it carries (bees, frame,
 * flower, modules, built-in armour), and only a hive of exactly the previous tier fits.
 */
public class PortableHiveUpgradeRecipe extends ShapedRecipe {

    private final ShapedRecipePattern pattern;
    private final HiveTier tier;

    public PortableHiveUpgradeRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, HiveTier tier) {
        super(group, category, pattern, HiveTier.withTier(new ItemStack(ModItems.PORTABLE_HIVE.get()), tier));
        this.pattern = pattern;
        this.tier = tier;
    }

    public HiveTier tier() {
        return tier;
    }

    private static ItemStack findHive(CraftingInput input) {
        for (ItemStack stack : input.items()) {
            if (stack.getItem() instanceof FEItemPortableHive) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return super.matches(input, level) && HiveTier.of(findHive(input)) == tier.previous();
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return HiveTier.withTier(findHive(input), tier);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.PORTABLE_HIVE_UPGRADE.get();
    }

    public static class Serializer implements RecipeSerializer<PortableHiveUpgradeRecipe> {
        private static final MapCodec<PortableHiveUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                com.mojang.serialization.Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.EQUIPMENT).forGetter(ShapedRecipe::category),
                ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
                HiveTier.CODEC.validate(tier -> tier == HiveTier.BASIC
                        ? DataResult.error(() -> "the basic tier is crafted, not upgraded to")
                        : DataResult.success(tier)).fieldOf("tier").forGetter(PortableHiveUpgradeRecipe::tier)
        ).apply(instance, PortableHiveUpgradeRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, PortableHiveUpgradeRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, ShapedRecipe::getGroup,
                CraftingBookCategory.STREAM_CODEC, ShapedRecipe::category,
                ShapedRecipePattern.STREAM_CODEC, recipe -> recipe.pattern,
                ByteBufCodecs.VAR_INT.map(i -> HiveTier.VALUES[i], HiveTier::ordinal), PortableHiveUpgradeRecipe::tier,
                PortableHiveUpgradeRecipe::new);

        @Override
        public MapCodec<PortableHiveUpgradeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PortableHiveUpgradeRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
