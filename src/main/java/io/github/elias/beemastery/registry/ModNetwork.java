package io.github.elias.beemastery.registry;

import io.github.elias.beemastery.ForestryExtras;
import io.github.elias.beemastery.hive.HiveStatusPayload;
import io.github.elias.beemastery.hive.OpenWornHivePayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

/** The mod's network channel (portable hive status and the "open worn hive" key). */
public final class ModNetwork {

    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ForestryExtras.MOD_ID, "main"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    private ModNetwork() {
    }

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, HiveStatusPayload.class, HiveStatusPayload::encode, HiveStatusPayload::decode,
                HiveStatusPayload::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(id++, OpenWornHivePayload.class, OpenWornHivePayload::encode, OpenWornHivePayload::decode,
                OpenWornHivePayload::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
}
