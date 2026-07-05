package io.github.elias.beemastery.item;

import forestry.api.arboriculture.IToolGrafter;
import forestry.core.items.ItemForestry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Unlike most Bee Mastery items, the grafter uses a hand-drawn, pre-colored texture per
 * type instead of a tinted base texture, so it does not implement {@code IColoredItem}.
 */
public class FEItemGrafter extends ItemForestry implements IToolGrafter {
    private final FEEnumGrafter type;

    public FEItemGrafter(FEEnumGrafter type) {
        super(new Properties().stacksTo(1).durability(type.maxDamage));
        this.type = type;
    }

    public FEEnumGrafter getType() {
        return type;
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
