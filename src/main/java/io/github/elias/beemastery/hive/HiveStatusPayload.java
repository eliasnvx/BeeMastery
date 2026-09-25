package io.github.elias.beemastery.hive;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Server -> client: state of the hive a player is wearing. The wearer's client shows it in the HUD;
 * everyone nearby uses it to draw bees only around hives that are actually working. The error set
 * lives in the server-side housing only, so it has to be sent; the queen itself already reaches
 * clients with the worn item. Registered in {@link io.github.elias.beemastery.registry.ModNetwork}.
 */
public record HiveStatusPayload(int entityId, int status, int progress) {

    /** A state older than this is treated as unknown (the wearer left, took the hive off, ...). */
    private static final long STALE_MS = 2000;

    /** Last state received per player entity (client side). */
    private static final Map<Integer, Received> RECEIVED = new ConcurrentHashMap<>();

    private record Received(HiveStatusPayload payload, long at) {
    }

    /** The latest fresh state for this entity, or null. */
    public static HiveStatusPayload of(Entity entity) {
        Received received = RECEIVED.get(entity.getId());
        if (received == null || System.currentTimeMillis() - received.at() > STALE_MS) {
            return null;
        }
        return received.payload();
    }

    /** Forget everything, e.g. when leaving a world (entity ids are per world). */
    public static void clear() {
        RECEIVED.clear();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeVarInt(status);
        buf.writeVarInt(progress);
    }

    public static HiveStatusPayload decode(FriendlyByteBuf buf) {
        return new HiveStatusPayload(buf.readVarInt(), buf.readVarInt(), buf.readVarInt());
    }

    public static void handle(HiveStatusPayload payload, Supplier<NetworkEvent.Context> context) {
        RECEIVED.put(payload.entityId(), new Received(payload, System.currentTimeMillis()));
        context.get().setPacketHandled(true);
    }
}
