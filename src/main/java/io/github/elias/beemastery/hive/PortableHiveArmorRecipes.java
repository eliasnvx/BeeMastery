package io.github.elias.beemastery.hive;

import io.github.elias.beemastery.item.FEItemPortableHive;
import io.github.elias.beemastery.registry.ModRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/** Crafting-grid recipes that build a chestplate into a portable hive, or take it back out. */
public final class PortableHiveArmorRecipes {

    private PortableHiveArmorRecipes() {
    }

    /** Plain hive + any chestplate (shapeless) -> armoured hive. */
    public static class Attach extends CustomRecipe {
        public Attach(CraftingBookCategory category) {
            super(category);
        }

        private record Parts(ItemStack hive, ItemStack chestplate) {
        }

        private static Parts find(CraftingInput input) {
            ItemStack hive = ItemStack.EMPTY;
            ItemStack chest = ItemStack.EMPTY;
            for (int i = 0; i < input.size(); i++) {
                ItemStack stack = input.getItem(i);
                if (stack.isEmpty()) {
                    continue;
                }
                if (stack.getItem() instanceof FEItemPortableHive && !FEItemPortableHive.isArmored(stack) && hive.isEmpty()) {
                    hive = stack;
                } else if (FEItemPortableHive.isChestplate(stack) && chest.isEmpty()) {
                    chest = stack;
                } else {
                    return null;
                }
            }
            return hive.isEmpty() || chest.isEmpty() ? null : new Parts(hive, chest);
        }

        @Override
        public boolean matches(CraftingInput input, Level level) {
            return find(input) != null;
        }

        @Override
        public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
            Parts parts = find(input);
            return parts == null ? ItemStack.EMPTY : FEItemPortableHive.withArmor(parts.hive(), parts.chestplate());
        }

        @Override
        public boolean canCraftInDimensions(int width, int height) {
            return width * height >= 2;
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return ModRecipeSerializers.PORTABLE_HIVE_ATTACH_ARMOR.get();
        }
    }

    /** Armoured hive alone in the grid -> plain hive; the chestplate stays behind in the grid. */
    public static class Detach extends CustomRecipe {
        public Detach(CraftingBookCategory category) {
            super(category);
        }

        private static int findHive(CraftingInput input) {
            int found = -1;
            for (int i = 0; i < input.size(); i++) {
                ItemStack stack = input.getItem(i);
                if (stack.isEmpty()) {
                    continue;
                }
                if (found >= 0 || !(stack.getItem() instanceof FEItemPortableHive) || !FEItemPortableHive.isArmored(stack)) {
                    return -1;
                }
                found = i;
            }
            return found;
        }

        @Override
        public boolean matches(CraftingInput input, Level level) {
            return findHive(input) >= 0;
        }

        @Override
        public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
            int slot = findHive(input);
            if (slot < 0) {
                return ItemStack.EMPTY;
            }
            ItemStack plain = input.getItem(slot).copyWithCount(1);
            FEItemPortableHive.stripArmor(plain);
            return plain;
        }

        @Override
        public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
            NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
            int slot = findHive(input);
            if (slot >= 0) {
                remaining.set(slot, FEItemPortableHive.extractArmor(input.getItem(slot)));
            }
            return remaining;
        }

        @Override
        public boolean canCraftInDimensions(int width, int height) {
            return width * height >= 1;
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return ModRecipeSerializers.PORTABLE_HIVE_DETACH_ARMOR.get();
        }
    }
}
