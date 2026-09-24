package io.github.elias.beemastery.client;

import io.github.elias.beemastery.ForestryExtras;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/** Client-only entry point: the "Config" button in the mod list opens NeoForge's settings screen. */
@Mod(value = ForestryExtras.MOD_ID, dist = Dist.CLIENT)
public final class BeeMasteryClientMod {

    public BeeMasteryClientMod(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
