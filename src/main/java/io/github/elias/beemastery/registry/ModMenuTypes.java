package io.github.elias.beemastery.registry;

import io.github.elias.beemastery.ForestryExtras;
import io.github.elias.beemastery.hive.PortableHiveMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ForestryExtras.MOD_ID);

    public static final RegistryObject<MenuType<PortableHiveMenu>> PORTABLE_HIVE =
            MENUS.register("portable_hive", () -> IForgeMenuType.create(PortableHiveMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
