package io.github.elias.beemastery.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.elias.beemastery.ForestryExtras;
import io.github.elias.beemastery.hive.HiveTier;
import io.github.elias.beemastery.hive.OpenWornHivePayload;
import io.github.elias.beemastery.item.FEItemPortableHive;
import io.github.elias.beemastery.registry.ModItems;
import io.github.elias.beemastery.registry.ModMenuTypes;
import io.github.elias.beemastery.registry.ModNetwork;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

/** Client wiring for the portable hive: GUI screen, "open worn hive" key, HUD and the back render. */
@Mod.EventBusSubscriber(modid = ForestryExtras.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class PortableHiveClient {

    public static final KeyMapping OPEN_HIVE = new KeyMapping(
            "key.beemastery.open_hive", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, "key.categories.beemastery");

    private PortableHiveClient() {
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.PORTABLE_HIVE.get(), PortableHiveScreen::new);
            // item model switch for the tier icons (see models/item/portable_hive.json overrides)
            ItemProperties.register(ModItems.PORTABLE_HIVE.get(), new ResourceLocation(ForestryExtras.MOD_ID, "tier"),
                    (stack, level, entity, seed) -> HiveTier.of(stack).ordinal() / (float) (HiveTier.VALUES.length - 1));
        });
    }

    @SubscribeEvent
    public static void registerHud(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "portable_hive", HiveHud::render);
    }

    /** The 3D hive models drawn on the player's back aren't tied to a block, so load them explicitly. */
    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        HiveBackLayer.MODELS.forEach(event::register);
    }

    /** An armoured hive shows its built-in chestplate as a small icon in the slot's bottom-right corner. */
    @SubscribeEvent
    public static void registerDecorations(RegisterItemDecorationsEvent event) {
        event.register(ModItems.PORTABLE_HIVE.get(), (graphics, font, stack, x, y) -> {
            ItemStack armor = FEItemPortableHive.getArmor(stack);
            if (armor.isEmpty()) {
                return false;
            }
            graphics.pose().pushPose();
            graphics.pose().translate(x + 8, y + 8, 100);  // above the hive icon itself
            graphics.pose().scale(0.5f, 0.5f, 1f);
            graphics.renderItem(armor, 0, 0);
            graphics.pose().popPose();
            return true;
        });
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(OPEN_HIVE);
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        for (String skin : event.getSkins()) {
            if (event.getSkin(skin) instanceof PlayerRenderer renderer) {
                renderer.addLayer(new HiveBackLayer(renderer, event.getContext().getBlockRenderDispatcher()));
            }
        }
    }

    /** Game (Forge bus) events: the key press. */
    @Mod.EventBusSubscriber(modid = ForestryExtras.MOD_ID, value = Dist.CLIENT)
    public static final class GameEvents {
        private GameEvents() {
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            Minecraft mc = Minecraft.getInstance();
            while (OPEN_HIVE.consumeClick()) {
                if (mc.player != null && mc.screen == null
                        && mc.player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof FEItemPortableHive) {
                    ModNetwork.CHANNEL.sendToServer(OpenWornHivePayload.INSTANCE);
                }
            }
        }
    }
}
