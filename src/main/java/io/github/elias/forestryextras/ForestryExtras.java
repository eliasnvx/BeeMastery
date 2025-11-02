package io.github.elias.forestryextras;

import com.mojang.logging.LogUtils;
import io.github.elias.forestryextras.registry.ModBlocks;
import io.github.elias.forestryextras.registry.ModCreativeTabs;
import io.github.elias.forestryextras.registry.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ForestryExtras.MOD_ID)
public class ForestryExtras {
    public static final String MOD_ID = "forestryextras";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static ForestryExtras instance;

    public ForestryExtras() {
        instance = this;
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Регистрация предметов и блоков
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("ForestryExtras: Common setup complete");
    }
}
