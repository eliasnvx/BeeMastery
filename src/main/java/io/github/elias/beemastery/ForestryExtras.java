package io.github.elias.beemastery;

import com.mojang.logging.LogUtils;
import io.github.elias.beemastery.config.BeeMasteryConfig;
import io.github.elias.beemastery.registry.ModBlocks;
import io.github.elias.beemastery.registry.ModCreativeTabs;
import io.github.elias.beemastery.registry.ModItems;
import io.github.elias.beemastery.registry.ModDataComponents;
import io.github.elias.beemastery.registry.ModArmorMaterials;
import io.github.elias.beemastery.registry.ModMenuTypes;
import io.github.elias.beemastery.registry.ModRecipeSerializers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(ForestryExtras.MOD_ID)
public class ForestryExtras {
    public static final String MOD_ID = "beemastery";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static ForestryExtras instance;

    public ForestryExtras(IEventBus modEventBus, ModContainer modContainer) {
        instance = this;

        // Регистрация предметов и блоков
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModArmorMaterials.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipeSerializers.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.SERVER, BeeMasteryConfig.SERVER_SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, BeeMasteryConfig.CLIENT_SPEC);

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("ForestryExtras: Common setup complete");
    }
}
