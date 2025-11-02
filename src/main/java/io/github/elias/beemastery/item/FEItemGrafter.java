package io.github.elias.beemastery.item;

import forestry.api.arboriculture.IToolGrafter;
import forestry.core.items.ItemForestry;
import forestry.core.items.definitions.IColoredItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FEItemGrafter extends ItemForestry implements IColoredItem, IToolGrafter {
    private final FEEnumGrafter type;

    public FEItemGrafter(FEEnumGrafter type) {
        super(new Properties().stacksTo(1).durability(type.maxDamage));
        this.type = type;
    }

    public FEEnumGrafter getType() {
        return type;
    }

    @Override
    public int getColorFromItemStack(ItemStack itemstack, int tintIndex) {
        return type.color;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return type == FEEnumGrafter.LEGENDARY || type == FEEnumGrafter.DRACONIC || super.isFoil(stack);
    }

    @Override
    public float getSaplingModifier(ItemStack stack, Level world, Player player, BlockPos pos) {
        return type.saplingModifier;
    }
}
