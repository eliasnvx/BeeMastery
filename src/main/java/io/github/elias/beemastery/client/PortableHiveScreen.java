package io.github.elias.beemastery.client;

import forestry.api.IForestryApi;
import forestry.api.apiculture.ForestryBeeSpecies;
import forestry.api.apiculture.genetics.BeeLifeStage;
import forestry.api.core.ForestryError;
import forestry.api.genetics.ForestrySpeciesTypes;
import forestry.api.genetics.IIndividualLiving;
import forestry.api.genetics.capability.IIndividualHandlerItem;
import io.github.elias.beemastery.ForestryExtras;
import io.github.elias.beemastery.hive.BeeStacks;
import io.github.elias.beemastery.hive.HiveFlowers;
import io.github.elias.beemastery.hive.HiveStackContainer;
import io.github.elias.beemastery.hive.HiveTier;
import io.github.elias.beemastery.item.FEEnumHiveModule;
import io.github.elias.beemastery.item.FEItemHiveModule;
import io.github.elias.beemastery.registry.ModItems;
import io.github.elias.beemastery.hive.PortableHiveMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Portable hive GUI. Everything a new player needs is on the screen itself: faded example items in
 * the empty bee/frame slots, a hint on hovering any empty slot, a status icon that says why the bees
 * aren't working (reusing Forestry's own error icons and texts), the queen's remaining life, and a
 * "?" with the full how-to.
 */
public class PortableHiveScreen extends AbstractContainerScreen<PortableHiveMenu> {

    private static final ResourceLocation BACKGROUND =
            new ResourceLocation(ForestryExtras.MOD_ID, "textures/gui/portable_hive.png");

    private static final int ARROW_X = 76, ARROW_Y = 37, ARROW_W = 22, ARROW_H = 15;
    /** Where the golden "working" arrow and the padlock live in the texture, right of the window + side panel. */
    private static final int ARROW_U = 208, LOCK_U = 208, LOCK_V = 16;
    private static final int STATUS_X = 79, STATUS_Y = 18;
    private static final int LIFE_X = 19, LIFE_Y = 19, LIFE_W = 3, LIFE_H = 16;
    private static final int HELP_X = 157, HELP_Y = 4, HELP_SIZE = 9;
    /** Tier corner brackets: 10x10 sprites at (208 + corner * 10, 32 + (tier - 1) * 10); corners TL, TR, BL, BR. */
    private static final int BRACKET_U = 208, BRACKET_V = 32, BRACKET = 10;
    private static final int[][] CORNERS = {{0, 0}, {165, 0}, {0, 155}, {165, 155}};
    /** Empty module slots cycle through the modules that could still go in, one step per this many ticks. */
    private static final int MODULE_GHOST_TICKS = 30;
    /** The hive's own (tier) icon, top right next to the "?" button. */
    private static final int BADGE_X = 139, BADGE_Y = 2;
    private static final int TOOLTIP_WIDTH = 180;
    private static final float ARROW_CYCLE = 40f;
    private static final ItemStack DEFAULT_FLOWER = new ItemStack(Items.POPPY);

    private final ItemStack[] ghosts = new ItemStack[HiveStackContainer.SIZE];

    public PortableHiveScreen(PortableHiveMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 203;  // 176 main window + module side panel
        imageHeight = 166;
        inventoryLabelY = imageHeight - 94;
        Arrays.fill(ghosts, ItemStack.EMPTY);
        ghosts[HiveStackContainer.QUEEN] = bee(BeeLifeStage.PRINCESS);
        ghosts[HiveStackContainer.DRONE] = bee(BeeLifeStage.DRONE);
        ghosts[HiveStackContainer.FRAME] = new ItemStack(
                BuiltInRegistries.ITEM.get(new ResourceLocation("forestry", "frame_untreated")));
    }

    private static ItemStack bee(BeeLifeStage stage) {
        try {
            return IForestryApi.INSTANCE.getGeneticManager().getSpeciesType(ForestrySpeciesTypes.BEE)
                    .createStack(ForestryBeeSpecies.FOREST, stage);
        } catch (RuntimeException e) {
            return ItemStack.EMPTY;  // a missing example icon is cosmetic only
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        HiveTier tier = menu.tier();
        if (tier != HiveTier.BASIC) {
            for (int corner = 0; corner < CORNERS.length; corner++) {
                graphics.blit(BACKGROUND, leftPos + CORNERS[corner][0], topPos + CORNERS[corner][1],
                        BRACKET_U + corner * BRACKET, BRACKET_V + (tier.ordinal() - 1) * BRACKET, BRACKET, BRACKET);
            }
        }

        int status = menu.getStatus();
        if (status == PortableHiveMenu.STATUS_OK && minecraft != null && minecraft.player != null) {
            // bees at work: the golden arrow keeps filling, like a machine's progress arrow
            float cycle = ((minecraft.player.tickCount + partialTick) % ARROW_CYCLE) / ARROW_CYCLE;
            int width = Math.max(1, Math.round(cycle * ARROW_W));
            graphics.blit(BACKGROUND, leftPos + ARROW_X, topPos + ARROW_Y, ARROW_U, 0, width, ARROW_H);
        }
        graphics.blit(HiveStatusView.icon(status), leftPos + STATUS_X, topPos + STATUS_Y, 0, 0, 16, 16, 16, 16);

        int progress = progress();
        if (progress >= 0) {
            int filled = Math.max(1, Math.round(progress * LIFE_H / 100f));
            graphics.fill(leftPos + LIFE_X, topPos + LIFE_Y + LIFE_H - filled,
                    leftPos + LIFE_X + LIFE_W, topPos + LIFE_Y + LIFE_H, HiveStatusView.progressColor(progress));
        }

        for (int i = HiveStackContainer.MODULES_START; i < HiveStackContainer.SIZE; i++) {
            if (!menu.isModuleSlotUnlocked(i)) {
                var slot = menu.getSlot(i);
                graphics.blit(BACKGROUND, leftPos + slot.x, topPos + slot.y, LOCK_U, LOCK_V, 16, 16);
            }
        }

        for (int i = 0; i < ghosts.length; i++) {
            var slot = menu.getSlot(i);
            ItemStack ghost = i == HiveStackContainer.FLOWER ? flowerGhost()
                    : i >= HiveStackContainer.MODULES_START ? moduleGhost(i) : ghosts[i];
            if (!slot.hasItem() && !ghost.isEmpty()) {
                int x = leftPos + slot.x, y = topPos + slot.y;
                graphics.renderFakeItem(ghost, x, y);
                graphics.fill(RenderType.guiGhostRecipeOverlay(), x, y, x + 16, y + 16, 0xA08B8B8B);
            }
        }
    }

    /**
     * The title is the plain hive name in the usual label colour: the tier-coloured "Legendary Portable
     * Hive" would be unreadable (white on grey) and too long; the tier shows as the badge and brackets.
     */
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, Component.translatable(menu.hiveStack().getItem().getDescriptionId()),
                titleLabelX, titleLabelY, 0x404040, false);
        graphics.renderItem(menu.hiveStack(), BADGE_X, BADGE_Y);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);  // 1.20.1: the dimmed world behind the window is drawn by the screen itself
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        List<FormattedCharSequence> lines = null;
        if (menu.getCarried().isEmpty() && hoveredSlot != null && !hoveredSlot.hasItem()
                && hoveredSlot.index < PortableHiveMenu.HIVE_SLOTS) {
            lines = slotHint(hoveredSlot.getContainerSlot());
        } else if (isHovering(STATUS_X, STATUS_Y, 16, 16, mouseX, mouseY)) {
            lines = statusHint(menu.getStatus());
        } else if (isHovering(BADGE_X, BADGE_Y, 16, 16, mouseX, mouseY)) {
            HiveTier tier = menu.tier();
            lines = tooltip(Component.translatable("beemastery.tier." + tier.name).withStyle(tier.color),
                    Component.translatable("beemastery.hive.tier_info", Math.round(tier.productionMultiplier() * 100), tier.moduleSlots()));
        } else if (isHovering(HELP_X, HELP_Y, HELP_SIZE, HELP_SIZE, mouseX, mouseY)) {
            lines = helpHint();
        } else if (isHovering(LIFE_X - 1, LIFE_Y - 1, LIFE_W + 2, LIFE_H + 2, mouseX, mouseY) && progress() >= 0) {
            lines = List.of(Component.translatable(hasQueen() ? "beemastery.hive.life" : "beemastery.hive.breeding",
                    progress()).getVisualOrderText());
        }
        if (lines != null) {
            graphics.renderTooltip(font, lines, mouseX, mouseY);
        } else {
            super.renderTooltip(graphics, mouseX, mouseY);
        }
    }

    private List<FormattedCharSequence> slotHint(int index) {
        if (index >= HiveStackContainer.MODULES_START) {
            if (menu.isModuleSlotUnlocked(index)) {
                ItemStack example = moduleGhost(index);
                if (example.getItem() instanceof FEItemHiveModule module) {
                    return tooltip(Component.translatable("beemastery.hive.slot.module"),
                            Component.translatable("beemastery.hive.slot.module.desc"),
                            Component.translatable("beemastery.hive.slot.module.example", example.getHoverName())
                                    .withStyle(ChatFormatting.AQUA),
                            Component.translatable("beemastery.module." + module.getType().getSerializedName() + ".desc"));
                }
                return tooltip(Component.translatable("beemastery.hive.slot.module"),
                        Component.translatable("beemastery.hive.slot.module.desc"));
            }
            HiveTier needed = HiveTier.VALUES[index - HiveStackContainer.MODULES_START];
            return tooltip(Component.translatable("beemastery.hive.slot.locked").withStyle(ChatFormatting.RED),
                    Component.translatable("beemastery.hive.slot.locked.desc",
                            Component.translatable("beemastery.tier." + needed.name).withStyle(needed.color)));
        }
        String key = switch (index) {
            case HiveStackContainer.QUEEN -> "queen";
            case HiveStackContainer.DRONE -> "drone";
            case HiveStackContainer.FRAME -> "frame";
            case HiveStackContainer.FLOWER -> "flower";
            default -> "product";
        };
        return tooltip(Component.translatable("beemastery.hive.slot." + key),
                Component.translatable("beemastery.hive.slot." + key + ".desc"));
    }

    private List<FormattedCharSequence> statusHint(int status) {
        if (HiveStatusView.error(status) == ForestryError.NO_FLOWER) {
            return tooltip(HiveStatusView.title(status), HiveStatusView.help(status),
                    Component.translatable("beemastery.hive.status.flower_slot").withStyle(ChatFormatting.YELLOW));
        }
        return tooltip(HiveStatusView.title(status), HiveStatusView.help(status));
    }

    private List<FormattedCharSequence> helpHint() {
        return tooltip(Component.translatable("beemastery.hive.help.title").withStyle(ChatFormatting.GOLD),
                Component.translatable("beemastery.hive.help.1"),
                Component.translatable("beemastery.hive.help.2"),
                Component.translatable("beemastery.hive.help.3"),
                Component.translatable("beemastery.hive.help.4", Component.keybind("key.beemastery.open_hive")
                        .withStyle(ChatFormatting.YELLOW)),
                Component.translatable("beemastery.hive.help.5"),
                Component.translatable("beemastery.hive.help.6"));
    }

    private List<FormattedCharSequence> tooltip(Component title, Component... body) {
        List<FormattedCharSequence> lines = new ArrayList<>();
        lines.add(title.getVisualOrderText());
        for (Component line : body) {
            if (line.getString().isEmpty()) {
                continue;
            }
            lines.addAll(font.split(line.copy().withStyle(ChatFormatting.GRAY), TOOLTIP_WIDTH));
        }
        return lines;
    }

    /**
     * A module that could still be installed, for an empty unlocked module slot: not already in the
     * hive, not conflicting with one that is, cycling over time and staggered per slot so the open
     * slots show different modules at once. Empty for locked slots.
     */
    private ItemStack moduleGhost(int slot) {
        if (!menu.isModuleSlotUnlocked(slot) || minecraft == null || minecraft.player == null) {
            return ItemStack.EMPTY;
        }
        Set<FEEnumHiveModule> installed = menu.installedModules();
        List<FEEnumHiveModule> options = Arrays.stream(FEEnumHiveModule.VALUES)
                .filter(m -> !installed.contains(m) && installed.stream().noneMatch(m::conflictsWith))
                .toList();
        if (options.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int step = minecraft.player.tickCount / MODULE_GHOST_TICKS + (slot - HiveStackContainer.MODULES_START);
        return new ItemStack(ModItems.HIVE_MODULES.get(options.get(step % options.size())).get());
    }

    /** The flower the current bee wants, so the empty slot shows e.g. nether wart for a nether bee. */
    private ItemStack flowerGhost() {
        ItemStack bee = menu.getSlot(HiveStackContainer.QUEEN).getItem();
        ItemStack wanted = bee.isEmpty() ? ItemStack.EMPTY : HiveFlowers.example(BeeStacks.genome(bee));
        return wanted.isEmpty() ? DEFAULT_FLOWER : wanted;
    }

    private boolean hasQueen() {
        return BeeStacks.stage(menu.getSlot(HiveStackContainer.QUEEN).getItem()) == BeeLifeStage.QUEEN;
    }

    /**
     * 0..100 or -1. A worn hive reports Forestry's own figure (queen's life, or mating progress);
     * one opened from a hand isn't running, so fall back to the queen's health read off the item.
     */
    private int progress() {
        if (menu.getProgress() >= 0) {
            return menu.getProgress();
        }
        ItemStack queen = menu.getSlot(HiveStackContainer.QUEEN).getItem();
        if (!hasQueen() || !(IIndividualHandlerItem.getIndividual(queen) instanceof IIndividualLiving bee)
                || bee.getMaxHealth() <= 0) {
            return -1;
        }
        return Mth.clamp(Math.round(100f * bee.getHealth() / bee.getMaxHealth()), 0, 100);
    }
}
