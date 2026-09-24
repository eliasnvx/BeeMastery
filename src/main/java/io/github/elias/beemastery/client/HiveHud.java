package io.github.elias.beemastery.client;

import io.github.elias.beemastery.config.BeeMasteryConfig;
import io.github.elias.beemastery.hive.HiveStackContainer;
import io.github.elias.beemastery.hive.HiveStatusPayload;
import io.github.elias.beemastery.item.FEItemPortableHive;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

/**
 * Small top-left panel while a portable hive is worn: the queen, Forestry's progress bar (queen's
 * life / mating) and the hive status — so you know what your bees are doing without opening it.
 */
final class HiveHud {

    private static final int MARGIN = 4, PAD = 3;
    private static final int BAR_W = 3;
    private static final int BACKGROUND = 0x90000000;

    private HiveHud() {
    }

    static void render(GuiGraphics graphics, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || mc.getDebugOverlay().showDebugScreen()
                || !BeeMasteryConfig.CLIENT.showHud()) {
            return;
        }
        ItemStack worn = mc.player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(worn.getItem() instanceof FEItemPortableHive)) {
            return;
        }
        HiveStatusPayload state = HiveStatusPayload.of(mc.player);
        if (state == null) {
            return;  // nothing received from the server yet
        }

        Font font = mc.font;
        ItemStack queen = new HiveStackContainer(worn).getQueen();
        Component name = queen.isEmpty()
                ? worn.getHoverName().copy().withStyle(ChatFormatting.WHITE)
                : queen.getHoverName().copy().withStyle(ChatFormatting.WHITE);
        Component status = HiveStatusView.title(state.status());

        int width = PAD + 16 + 2 + BAR_W + 2 + 16 + 4 + Math.max(font.width(name), font.width(status)) + PAD;
        int height = 16 + PAD * 2;
        BeeMasteryConfig.Client.Corner corner = BeeMasteryConfig.CLIENT.hudCorner();
        boolean right = corner == BeeMasteryConfig.Client.Corner.TOP_RIGHT || corner == BeeMasteryConfig.Client.Corner.BOTTOM_RIGHT;
        boolean bottom = corner == BeeMasteryConfig.Client.Corner.BOTTOM_LEFT || corner == BeeMasteryConfig.Client.Corner.BOTTOM_RIGHT;
        int x = right ? graphics.guiWidth() - MARGIN - width : MARGIN;
        int y = bottom ? graphics.guiHeight() - MARGIN - height : MARGIN;
        int barX = x + PAD + 16 + 2;
        int iconX = barX + BAR_W + 2;
        int textX = iconX + 16 + 4;

        graphics.fill(x, y, x + width, y + height, BACKGROUND);
        if (!queen.isEmpty()) {
            graphics.renderItem(queen, x + PAD, y + PAD);
        }
        int top = y + PAD;
        graphics.fill(barX, top, barX + BAR_W, top + 16, 0xFF373737);
        if (state.progress() >= 0) {
            int filled = Math.max(1, Math.round(state.progress() * 16 / 100f));
            graphics.fill(barX, top + 16 - filled, barX + BAR_W, top + 16, HiveStatusView.progressColor(state.progress()));
        }
        graphics.blit(HiveStatusView.icon(state.status()), iconX, top, 0, 0, 16, 16, 16, 16);
        graphics.drawString(font, name, textX, top - 1, 0xFFFFFF);
        graphics.drawString(font, status, textX, top + 9, 0xFFFFFF);
    }
}
