package io.github.elias.beemastery.hive;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Client -> server: "open the portable hive I'm wearing" (sent by the keybind).
 * Registered in {@link io.github.elias.beemastery.registry.ModNetwork}.
 */
public record OpenWornHivePayload() {

    public static final OpenWornHivePayload INSTANCE = new OpenWornHivePayload();

    public void encode(FriendlyByteBuf buf) {
        // no payload
    }

    public static OpenWornHivePayload decode(FriendlyByteBuf buf) {
        return INSTANCE;
    }

    public static void handle(OpenWornHivePayload payload, Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                PortableHiveMenu.open(player, PortableHiveMenu.Source.CHEST);  // no-op unless a hive is worn
            }
        });
        ctx.setPacketHandled(true);
    }
}
