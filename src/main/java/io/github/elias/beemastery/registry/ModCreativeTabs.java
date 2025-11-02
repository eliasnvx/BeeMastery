package io.github.elias.beemastery.registry;

import io.github.elias.beemastery.ForestryExtras;
import io.github.elias.beemastery.item.FEEnumHoneyComb;
import io.github.elias.beemastery.item.FEEnumIngot;
import io.github.elias.beemastery.item.FEEnumScoop;
import io.github.elias.beemastery.item.FEEnumGrafter;
import io.github.elias.beemastery.item.FEEnumNugget;
import io.github.elias.beemastery.item.FEEnumFrame;
import io.github.elias.beemastery.item.FEEnumStick;
import io.github.elias.beemastery.item.FEEnumPropolis;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ForestryExtras.MOD_ID);

    public static final RegistryObject<CreativeModeTab> FORESTRY_EXTRAS_TAB = CREATIVE_MODE_TABS.register("forestry_extras_tab",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.beemastery"))
            .icon(() -> new ItemStack(ModItems.BEE_COMBS.get(FEEnumHoneyComb.DRACONIC).get()))
            .displayItems((params, output) -> {
                // Соты
                for (FEEnumHoneyComb comb : FEEnumHoneyComb.VALUES) {
                    output.accept(ModItems.BEE_COMBS.get(comb).get());
                }
                
                // Слитки
                for (FEEnumIngot ingot : FEEnumIngot.VALUES) {
                    output.accept(ModItems.INGOTS.get(ingot).get());
                }

                // Крюки (Scoops)
                for (FEEnumScoop scoop : FEEnumScoop.VALUES) {
                    output.accept(ModItems.SCOOPS.get(scoop).get());
                }

                // Прививочные ножи (Grafters)
                for (FEEnumGrafter grafter : FEEnumGrafter.VALUES) {
                    output.accept(ModItems.GRAFTERS.get(grafter).get());
                }

                // Самородки (Nuggets)
                for (FEEnumNugget nugget : FEEnumNugget.VALUES) {
                    output.accept(ModItems.NUGGETS.get(nugget).get());
                }

                // Рамки (Frames)
                for (FEEnumFrame frame : FEEnumFrame.VALUES) {
                    output.accept(ModItems.FRAMES.get(frame).get());
                }

                // Палки (Sticks)
                for (FEEnumStick stick : FEEnumStick.VALUES) {
                    output.accept(ModItems.STICKS.get(stick).get());
                }

                // Прополис (Propolis)
                for (FEEnumPropolis propolis : FEEnumPropolis.VALUES) {
                    output.accept(ModItems.PROPOLIS.get(propolis).get());
                }
                
                // Блоки
                output.accept(ModBlocks.DRACONIC_BLOCK_ITEM.get());
                output.accept(ModBlocks.LEGENDARY_BLOCK_ITEM.get());
                output.accept(ModBlocks.REINFORCED_BLOCK_ITEM.get());
                output.accept(ModBlocks.MUTATED_IRON_BLOCK_ITEM.get());
            })
            .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
