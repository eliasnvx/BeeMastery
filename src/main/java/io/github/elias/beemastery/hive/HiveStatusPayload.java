package io.github.elias.beemastery.hive;

import io.github.elias.beemastery.ForestryExtras;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server -> client: state of the hive a player is wearing. The wearer's client shows it in the HUD;
 * everyone nearby uses it to draw bees only around hives that are actually working. The error set
 * lives in the server-side housing only, so it has to be sent; the queen itself already reaches
 * clients with the worn item.
 */
@EventBusSubscriber(modid = ForestryExtras.MOD_ID)
public record HiveStatusPayload(int entityId, int status, int progress) implements CustomPacketPayload {

    public static final Type<HiveStatusPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ForestryExtras.MOD_ID, "hive_status"));
    public static final StreamCodec<RegistryFriendlyByteBuf, HiveStatusPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, HiveStatusPayload::entityId,
            ByteBufCodecs.VAR_INT, HiveStatusPayload::status,
            ByteBufCodecs.VAR_INT, HiveStatusPayload::progress,
            HiveStatusPayload::new);

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

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(TYPE, STREAM_CODEC, HiveStatusPayload::handle);
    }

    private static void handle(HiveStatusPayload payload, IPayloadContext context) {
        RECEIVED.put(payload.entityId(), new Received(payload, System.currentTimeMillis()));
    }
}
