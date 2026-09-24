package io.github.elias.beemastery.hive;

import io.github.elias.beemastery.ForestryExtras;
import io.github.elias.beemastery.config.BeeMasteryConfig;
import io.github.elias.beemastery.item.FEEnumHiveModule;
import io.github.elias.beemastery.item.FEItemPortableHive;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Runs the bees in a portable hive while it is worn in the chest slot (server side only).
 *
 * <p>One housing per player is kept in memory; Forestry's logic keeps its own short-lived state
 * there (cycle progress, flower cache). Queen age and health live on the queen item itself, so
 * nothing important is lost if the housing is rebuilt after a relog.
 */
@EventBusSubscriber(modid = ForestryExtras.MOD_ID)
public final class PortableHiveTicker {

    private static final Map<Player, PortableHiveHousing> HOUSINGS = new WeakHashMap<>();
    /** How often the hive state is sent to the wearer and players around them (ticks). */
    private static final int STATUS_INTERVAL = 10;

    private PortableHiveTicker() {
    }

    /** The running housing of the hive this player wears, or null if none is worn. */
    public static PortableHiveHousing get(Player player) {
        return HOUSINGS.get(player);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }
        ItemStack worn = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(worn.getItem() instanceof FEItemPortableHive)) {
            HOUSINGS.remove(player);
            HiveLantern.remove(player.getUUID());
            return;
        }
        boolean fresh = !HOUSINGS.containsKey(player);
        PortableHiveHousing housing = HOUSINGS.computeIfAbsent(player, p -> new PortableHiveHousing(p, worn));
        housing.bind(worn);
        housing.tick();
        HiveLantern.update(player, housing.hasModule(FEEnumHiveModule.LANTERN) && BeeMasteryConfig.SERVER.lanternLight());
        if ((fresh || player.tickCount % STATUS_INTERVAL == 0) && player instanceof ServerPlayer) {
            // the wearer for the HUD, nearby players for the bee particles
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
                    new HiveStatusPayload(player.getId(), housing.status(), housing.progressPercent()));
        }
    }

    /** Tells the player how to open the hive once it's on their back — there's no hand to right-click it with. */
    @SubscribeEvent
    public static void onEquip(LivingEquipmentChangeEvent event) {
        if (event.getSlot() == EquipmentSlot.CHEST
                && event.getEntity() instanceof ServerPlayer player
                && event.getTo().getItem() instanceof FEItemPortableHive
                && !(event.getFrom().getItem() instanceof FEItemPortableHive)) {
            player.displayClientMessage(Component.translatable("beemastery.hive.worn_hint",
                    Component.keybind("key.beemastery.open_hive")), true);
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        HOUSINGS.remove(event.getEntity());
        HiveLantern.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        HiveLantern.removeAll();
    }
}
