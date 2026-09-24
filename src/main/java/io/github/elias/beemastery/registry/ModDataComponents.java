package io.github.elias.beemastery.registry;

import io.github.elias.beemastery.ForestryExtras;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.codec.ByteBufCodecs;
import com.mojang.serialization.Codec;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ForestryExtras.MOD_ID);

    public static final Supplier<DataComponentType<List<ItemStack>>> CHARMS = DATA_COMPONENTS.registerComponentType(
            "charms",
            builder -> builder.persistent(ItemStack.CODEC.listOf()).networkSynchronized(ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()))
    );

    /** Portable hive inventory: slot 0 queen/princess, 1 drone, 2 frame, 3..8 products, 9 flower, 10..13 modules. */
    public static final Supplier<DataComponentType<ItemContainerContents>> HIVE_CONTENTS = DATA_COMPONENTS.registerComponentType(
            "hive_contents",
            builder -> builder.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC)
    );

    /** Portable hive upgrade tier, see {@link io.github.elias.beemastery.hive.HiveTier} (absent = basic). */
    public static final Supplier<DataComponentType<Integer>> HIVE_TIER = DATA_COMPONENTS.registerComponentType(
            "hive_tier",
            builder -> builder.persistent(Codec.intRange(0, 3)).networkSynchronized(ByteBufCodecs.VAR_INT)
    );

    /**
     * The chestplate built into a portable hive (absent on a plain hive). Held as a one-slot
     * container: a bare ItemStack can't be a component value (it has no value equality).
     */
    public static final Supplier<DataComponentType<ItemContainerContents>> HIVE_ARMOR = DATA_COMPONENTS.registerComponentType(
            "hive_armor",
            builder -> builder.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC)
    );

    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
    }
}
