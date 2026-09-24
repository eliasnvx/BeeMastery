package io.github.elias.beemastery.registry;

import io.github.elias.beemastery.ForestryExtras;
import io.github.elias.beemastery.hive.PortableHiveMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ForestryExtras.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<PortableHiveMenu>> PORTABLE_HIVE =
            MENUS.register("portable_hive", () -> IMenuTypeExtension.create(PortableHiveMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
