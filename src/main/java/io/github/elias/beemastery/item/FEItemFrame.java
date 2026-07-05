package io.github.elias.beemastery.item;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.apiculture.genetics.IBeeSpecies;
import forestry.api.apiculture.hives.IHiveFrame;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutation;
import forestry.core.items.ItemForestry;
import forestry.core.items.definitions.IColoredItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * A Bee Mastery hive frame.
 *
 * <p>Implements {@link IHiveFrame} so it can be placed in the frame slots of apiaries and
 * bee houses. Its {@link IBeeModifier} applies the tuning stored on the {@link FEEnumFrame}
 * variant (production / lifespan / mutation / territory).
 */
public class FEItemFrame extends ItemForestry implements IColoredItem, IHiveFrame {
    private final FEEnumFrame type;
    private final IBeeModifier beeModifier;

    public FEItemFrame(FEEnumFrame type) {
        super(new Properties().stacksTo(1).durability(type.maxDamage));
        this.type = type;
        this.beeModifier = new FrameBeeModifier(type);
    }

    public FEEnumFrame getType() {
        return type;
    }

    @Override
    public ItemStack frameUsed(IBeeHousing housing, ItemStack frame, IBee queen, int wear) {
        Level level = housing.getWorldObj();
        if (level == null) {
            return frame;
        }
        if (frame.hurt(wear, level.getRandom(), null)) {
            return ItemStack.EMPTY;
        }
        return frame;
    }

    @Override
    public IBeeModifier getBeeModifier(ItemStack frame) {
        return beeModifier;
    }

    @Override
    public int getColorFromItemStack(ItemStack itemstack, int tintIndex) {
        return type.color;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        // Показываем информацию всегда
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("beemastery.frame.production",
            formatModifier(type.productionModifier)).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("beemastery.frame.lifespan",
            formatModifier(type.lifespanModifier)).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("beemastery.frame.mutation",
            formatModifier(type.mutationModifier)).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("beemastery.frame.territory",
            formatModifier(type.territoryModifier)).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("beemastery.frame.durability",
            String.valueOf(type.maxDamage)).withStyle(ChatFormatting.GRAY));
    }

    private String formatModifier(float modifier) {
        if (modifier == 1.0F) {
            return "1.0x";
        } else if (modifier > 1.0F) {
            return String.format("+%.1fx", modifier);
        } else {
            return String.format("%.1fx", modifier);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return type == FEEnumFrame.LEGENDARY || type == FEEnumFrame.DRACONIC || super.isFoil(stack);
    }

    /**
     * Applies a {@link FEEnumFrame}'s tuning to a hosted bee.
     *
     * <p>Semantics mirror Forestry's own frames: production/mutation/territory scale
     * multiplicatively, while a higher lifespan modifier slows aging (aging is divided
     * by it).
     */
    private static final class FrameBeeModifier implements IBeeModifier {
        private final FEEnumFrame type;

        FrameBeeModifier(FEEnumFrame type) {
            this.type = type;
        }

        @Override
        public float modifyProductionSpeed(IGenome genome, float currentSpeed) {
            return currentSpeed * type.productionModifier;
        }

        @Override
        public float modifyAging(IGenome genome, IGenome mate, float currentAging) {
            if (type.lifespanModifier <= 0.0F) {
                return currentAging;
            }
            return currentAging / type.lifespanModifier;
        }

        @Override
        public float modifyMutationChance(IGenome genome, IGenome mate, IMutation<IBeeSpecies> mutation, float currentChance) {
            return currentChance * type.mutationModifier;
        }

        @Override
        public Vec3i modifyTerritory(IGenome genome, Vec3i currentModifier) {
            if (type.territoryModifier == 1.0F) {
                return currentModifier;
            }
            return new Vec3i(
                Math.round(currentModifier.getX() * type.territoryModifier),
                Math.round(currentModifier.getY() * type.territoryModifier),
                Math.round(currentModifier.getZ() * type.territoryModifier));
        }
    }
}
