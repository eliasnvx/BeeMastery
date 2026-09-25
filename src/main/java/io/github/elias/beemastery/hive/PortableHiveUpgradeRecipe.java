package io.github.elias.beemastery.hive;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.elias.beemastery.item.FEItemPortableHive;
import io.github.elias.beemastery.registry.ModItems;
import io.github.elias.beemastery.registry.ModRecipeSerializers;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

/**
 * Upgrades a portable hive to the next tier. A shaped recipe in every other respect — so JEI and
 * the recipe book show it like any other — but the hive keeps everything it carries (bees, frame,
 * flower, modules, built-in armour), and only a hive of exactly the previous tier fits.
 *
 * <p>JSON: a {@code minecraft:crafting_shaped} recipe without {@code result}, plus
 * {@code "tier": "reinforced" | "draconic" | "legendary"}.
 */
public class PortableHiveUpgradeRecipe extends ShapedRecipe {

    private final HiveTier tier;

    public PortableHiveUpgradeRecipe(ShapedRecipe shape, HiveTier tier) {
        super(shape.getId(), shape.getGroup(), shape.category(), shape.getWidth(), shape.getHeight(), shape.getIngredients(),
                HiveTier.withTier(new ItemStack(ModItems.PORTABLE_HIVE.get()), tier), shape.showNotification());
        this.tier = tier;
    }

    public HiveTier tier() {
        return tier;
    }

    private static ItemStack findHive(CraftingContainer input) {
        for (int i = 0; i < input.getContainerSize(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem() instanceof FEItemPortableHive) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(CraftingContainer input, Level level) {
        return super.matches(input, level) && HiveTier.of(findHive(input)) == tier.previous();
    }

    @Override
    public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
        return HiveTier.withTier(findHive(input), tier);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.PORTABLE_HIVE_UPGRADE.get();
    }

    /** Pattern, key, group and category are parsed by vanilla's shaped serializer; only the tier is ours. */
    public static class Serializer implements RecipeSerializer<PortableHiveUpgradeRecipe> {

        @Override
        public PortableHiveUpgradeRecipe fromJson(ResourceLocation id, JsonObject json) {
            String name = GsonHelper.getAsString(json, "tier");
            HiveTier tier = HiveTier.byName(name);
            if (tier == null || tier == HiveTier.BASIC) {
                throw new JsonSyntaxException("Invalid portable hive tier '" + name + "': the basic tier is crafted, not upgraded to");
            }
            JsonObject shaped = json.deepCopy();
            JsonObject result = new JsonObject();
            result.addProperty("item", ModItems.PORTABLE_HIVE.getId().toString());
            shaped.add("result", result);  // replaced by the tiered hive; the shaped parser just requires one
            return new PortableHiveUpgradeRecipe(RecipeSerializer.SHAPED_RECIPE.fromJson(id, shaped), tier);
        }

        @Override
        public PortableHiveUpgradeRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            ShapedRecipe shape = RecipeSerializer.SHAPED_RECIPE.fromNetwork(id, buf);
            return new PortableHiveUpgradeRecipe(shape, HiveTier.VALUES[buf.readVarInt()]);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, PortableHiveUpgradeRecipe recipe) {
            RecipeSerializer.SHAPED_RECIPE.toNetwork(buf, recipe);
            buf.writeVarInt(recipe.tier.ordinal());
        }
    }
}
