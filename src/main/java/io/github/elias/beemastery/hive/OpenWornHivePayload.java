package io.github.elias.beemastery.hive;

import io.github.elias.beemastery.ForestryExtras;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Client -> server: "open the portable hive I'm wearing" (sent by the keybind). */
@EventBusSubscriber(modid = ForestryExtras.MOD_ID)
public record OpenWornHivePayload() implements CustomPacketPayload {

    public static final OpenWornHivePayload INSTANCE = new OpenWornHivePayload();
    public static final Type<OpenWornHivePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ForestryExtras.MOD_ID, "open_worn_hive"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenWornHivePayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(TYPE, STREAM_CODEC, OpenWornHivePayload::handle);
    }

    private static void handle(OpenWornHivePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                PortableHiveMenu.open(player, PortableHiveMenu.Source.CHEST);  // no-op unless a hive is worn
            }
        });
    }
}
