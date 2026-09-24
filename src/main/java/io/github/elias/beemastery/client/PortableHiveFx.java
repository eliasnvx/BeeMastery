package io.github.elias.beemastery.client;

import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.apiculture.genetics.BeeLifeStage;
import forestry.api.client.IForestryClientApi;
import forestry.api.core.genetics.IGenome;
import forestry.api.core.genetics.capability.IIndividualHandlerItem;
import io.github.elias.beemastery.ForestryExtras;
import io.github.elias.beemastery.config.BeeMasteryConfig;
import io.github.elias.beemastery.hive.HiveStackContainer;
import io.github.elias.beemastery.hive.HiveStatusPayload;
import io.github.elias.beemastery.hive.PlayerBeeHousing;
import io.github.elias.beemastery.hive.PortableHiveMenu;
import io.github.elias.beemastery.item.FEItemPortableHive;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.List;

/**
 * Bees buzzing around a worn portable hive, drawn by Forestry's own hive particle code — the same
 * explorer bees you see around a Bee House, flying out of the hive on the player's back. Shown for
 * every player in view, but only while the server reports their hive as working.
 */
@EventBusSubscriber(modid = ForestryExtras.MOD_ID, value = Dist.CLIENT)
public final class PortableHiveFx {

    /** Forestry's bee housings spawn their bee particles every 4 ticks; do the same. */
    private static final int FX_INTERVAL = 4;

    private PortableHiveFx() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.isPaused() || mc.level.getGameTime() % FX_INTERVAL != 0
                || !BeeMasteryConfig.CLIENT.beeParticles()) {
            return;
        }
        for (Player player : mc.level.players()) {
            ItemStack worn = player.getItemBySlot(EquipmentSlot.CHEST);
            if (!(worn.getItem() instanceof FEItemPortableHive)) {
                continue;
            }
            HiveStatusPayload state = HiveStatusPayload.of(player);
            if (state == null || state.status() != PortableHiveMenu.STATUS_OK) {
                continue;
            }
            ItemStack queen = new HiveStackContainer(worn).getQueen();
            if (IIndividualHandlerItem.getLifeStage(queen) != BeeLifeStage.QUEEN) {
                continue;
            }
            IGenome genome = IIndividualHandlerItem.getGenome(queen);
            if (genome != null) {
                IForestryClientApi.INSTANCE.getBeeManager().addBeeHiveParticles(new FxHousing(player, worn), genome, List.of());
            }
        }
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        HiveStatusPayload.clear();
    }

    /** Just enough of a housing for the particle code: position, world and territory (no logic runs here). */
    private static final class FxHousing extends PlayerBeeHousing {
        private final HiveStackContainer inventory;

        FxHousing(Player player, ItemStack hive) {
            super(player);
            this.inventory = new HiveStackContainer(hive);
        }

        @Override
        public Iterable<IBeeModifier> getBeeModifiers() {
            return List.of();
        }

        @Override
        public Iterable<IBeeListener> getBeeListeners() {
            return List.of();
        }

        @Override
        public IBeeHousingInventory getBeeInventory() {
            return inventory;
        }

        /** Particles never ask for it; the real logic only exists on the server. */
        @Override
        public IBeekeepingLogic getBeekeepingLogic() {
            throw new UnsupportedOperationException("client-side FX housing has no beekeeping logic");
        }
    }
}
