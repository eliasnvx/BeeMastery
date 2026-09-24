package io.github.elias.beemastery.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.elias.beemastery.ForestryExtras;
import io.github.elias.beemastery.hive.HiveTier;
import io.github.elias.beemastery.hive.OpenWornHivePayload;
import io.github.elias.beemastery.registry.ModItems;
import io.github.elias.beemastery.item.FEItemPortableHive;
import io.github.elias.beemastery.registry.ModMenuTypes;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

/** Client wiring for the portable hive: GUI screen, "open worn hive" key, and the back render. */
@EventBusSubscriber(modid = ForestryExtras.MOD_ID, value = Dist.CLIENT)
public final class PortableHiveClient {

    public static final KeyMapping OPEN_HIVE = new KeyMapping(
            "key.beemastery.open_hive", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, "key.categories.beemastery");

    private PortableHiveClient() {
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.PORTABLE_HIVE.get(), PortableHiveScreen::new);
    }

    @SubscribeEvent
    public static void registerHud(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR,
                ResourceLocation.fromNamespaceAndPath(ForestryExtras.MOD_ID, "portable_hive"), HiveHud::render);
    }

    /** Item model switch for the tier icons (see models/item/portable_hive.json overrides). */
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(ModItems.PORTABLE_HIVE.get(),
                ResourceLocation.fromNamespaceAndPath(ForestryExtras.MOD_ID, "tier"),
                (stack, level, entity, seed) -> HiveTier.of(stack).ordinal() / (float) (HiveTier.VALUES.length - 1)));
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
        for (PlayerSkin.Model skin : event.getSkins()) {
            if (event.getSkin(skin) instanceof PlayerRenderer renderer) {
                renderer.addLayer(new HiveBackLayer(renderer, event.getContext().getBlockRenderDispatcher()));
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        while (OPEN_HIVE.consumeClick()) {
            if (mc.player != null && mc.screen == null
                    && mc.player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof FEItemPortableHive) {
                PacketDistributor.sendToServer(OpenWornHivePayload.INSTANCE);
            }
        }
    }
}
