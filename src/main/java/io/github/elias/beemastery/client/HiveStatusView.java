package io.github.elias.beemastery.client;

import forestry.api.IForestryApi;
import forestry.api.core.IError;
import io.github.elias.beemastery.hive.PortableHiveMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** How a portable hive status code looks: icon, short text and colours. Shared by the GUI and the HUD. */
final class HiveStatusView {

    private HiveStatusView() {
    }

    static IError error(int status) {
        return status >= 0 ? IForestryApi.INSTANCE.getErrorManager().getError((short) status) : null;
    }

    /** Forestry's error icons also ship as plain 16x16 textures next to the atlas sources, so they can be blitted directly. */
    static ResourceLocation icon(int status) {
        IError error = error(status);
        if (error == null) {
            String name = status == PortableHiveMenu.STATUS_OK ? "ok" : "disabled";
            return new ResourceLocation("forestry", "textures/forestry/atlas/gui/errors/" + name + ".png");
        }
        String path = error.getSprite().getPath();
        return new ResourceLocation(error.getSprite().getNamespace(),
                "textures/forestry/atlas/gui/errors/" + path.substring(path.lastIndexOf('/') + 1) + ".png");
    }

    static Component title(int status) {
        if (status == PortableHiveMenu.STATUS_OK) {
            return Component.translatable("beemastery.hive.status.ok").withStyle(ChatFormatting.GREEN);
        }
        IError error = error(status);
        return (error == null
                ? Component.translatable("beemastery.hive.status.not_worn")
                : Component.translatable(error.getDescriptionTranslationKey())).withStyle(ChatFormatting.RED);
    }

    static Component help(int status) {
        IError error = error(status);
        if (error != null) {
            return Component.translatable(error.getHelpTranslationKey());
        }
        return status == PortableHiveMenu.STATUS_OK ? Component.empty()
                : Component.translatable("beemastery.hive.status.not_worn.desc");
    }

    /** Red at 0 %, green at 100 %. */
    static int progressColor(int percent) {
        return 0xFF000000 | Mth.hsvToRgb(Mth.clamp(percent, 0, 100) / 300f, 1f, 0.85f);
    }
}
