package io.github.elias.beemastery.hive;

import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ILifeStage;
import forestry.api.genetics.capability.IIndividualHandlerItem;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Small helpers over Forestry's individual-item capability. Forestry 3.x has these as statics
 * on {@link IIndividualHandlerItem}; Forestry 2.5 only has {@code get}/{@code getIndividual}.
 */
public final class BeeStacks {

    private BeeStacks() {
    }

    /** The genome of the bee (or other individual) in this stack, or null. */
    @Nullable
    public static IGenome genome(ItemStack stack) {
        IIndividual individual = IIndividualHandlerItem.getIndividual(stack);
        return individual == null ? null : individual.getGenome();
    }

    /** The life stage (queen, princess, drone...) of the individual in this stack, or null. */
    @Nullable
    public static ILifeStage stage(ItemStack stack) {
        IIndividualHandlerItem handler = IIndividualHandlerItem.get(stack);
        return handler == null ? null : handler.getStage();
    }
}
