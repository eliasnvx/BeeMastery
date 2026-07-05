package io.github.elias.beemastery.compat.jei;

import io.github.elias.beemastery.ForestryExtras;
import io.github.elias.beemastery.item.FEEnumAura;
import io.github.elias.beemastery.registry.ModItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * A JEI page per {@link FEEnumAura}, showing which charm carries it, how often it pulses and
 * what it actually does — none of that is visible anywhere else except the charm's tooltip and
 * the in-world action-bar text while it's running.
 */
public class AuraJeiCategory implements IRecipeCategory<FEEnumAura> {

    public static final RecipeType<FEEnumAura> TYPE =
            RecipeType.create(ForestryExtras.MOD_ID, "aura", FEEnumAura.class);

    private static final int WIDTH = 150;
    private static final int HEIGHT = 60;

    private final IDrawable background;
    private final IDrawable icon;

    public AuraJeiCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.AURA_BELT.get()));
    }

    @Override
    public RecipeType<FEEnumAura> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("beemastery.jei.category.aura");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FEEnumAura aura, IFocusGroup focuses) {
        ItemStack charm = new ItemStack(ModItems.AURA_CHARMS.get(aura).get());
        builder.addSlot(RecipeIngredientRole.CATALYST, 6, 6).addItemStack(charm);
    }

    @Override
    public void draw(FEEnumAura aura, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        int textX = 30;

        Component name = Component.translatable("beemastery.aura." + aura.getSerializedName())
                .withStyle(style -> style.withColor(TextColor.fromRgb(aura.color)).withBold(true));
        guiGraphics.drawString(font, name, textX, 6, 0x404040, false);

        Component description = Component.translatable("beemastery.jei.aura." + aura.getSerializedName());
        guiGraphics.drawString(font, description, textX, 20, 0x404040, false);

        Component radius = Component.translatable("beemastery.jei.aura.radius", (int) aura.personalRadius)
                .withStyle(ChatFormatting.DARK_GRAY);
        guiGraphics.drawString(font, radius, textX, 34, 0x707070, false);

        Component interval = Component.translatable("beemastery.jei.aura.interval", aura.charmInterval / 20)
                .withStyle(ChatFormatting.DARK_GRAY);
        guiGraphics.drawString(font, interval, textX, 46, 0x707070, false);
    }

    @Override
    public ResourceLocation getRegistryName(FEEnumAura aura) {
        return new ResourceLocation(ForestryExtras.MOD_ID, aura.getSerializedName() + "_aura");
    }
}
