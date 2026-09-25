package io.github.elias.beemastery.hive;

import io.github.elias.beemastery.item.FEEnumHiveModule;
import io.github.elias.beemastery.item.FEItemPortableHive;
import io.github.elias.beemastery.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;

import java.util.Set;

/**
 * Container for a portable hive: queen, drone, frame and six product slots.
 *
 * <p>The hive can be opened from either hand or while worn. When it is opened from a hotbar
 * slot, that slot is locked for as long as the menu is open, so the hive can't be moved or
 * dropped out from under its own open GUI.
 */
public class PortableHiveMenu extends AbstractContainerMenu {

    public enum Source {
        MAIN_HAND, OFF_HAND, CHEST;

        ItemStack find(Player player) {
            return switch (this) {
                case MAIN_HAND -> player.getMainHandItem();
                case OFF_HAND -> player.getOffhandItem();
                case CHEST -> player.getItemBySlot(EquipmentSlot.CHEST);
            };
        }
    }

    public static final int HIVE_SLOTS = HiveStackContainer.SIZE;

    /** Status values besides a Forestry error id: the hive isn't worn (so it isn't ticking), or all is well. */
    public static final int STATUS_NOT_WORN = -2;
    public static final int STATUS_OK = -1;

    // Item positions of the hive slots; the GUI texture is drawn around these.
    public static final int QUEEN_X = 26, QUEEN_Y = 19;
    public static final int DRONE_X = 26, DRONE_Y = 51;
    public static final int FRAME_X = 52, FRAME_Y = 19;
    public static final int FLOWER_X = 52, FLOWER_Y = 51;
    public static final int PRODUCTS_X = 106, PRODUCTS_Y = 27;
    /** Module slots sit in a side panel right of the main window, one above the other. */
    public static final int MODULES_X = 180, MODULES_Y = 8;

    private final Player player;
    private final DataSlot status = DataSlot.standalone();
    private final DataSlot progress = DataSlot.standalone();
    private final Source source;
    private final HiveStackContainer hive;
    private final int lockedHotbarSlot;

    public static void open(ServerPlayer player, Source source) {
        ItemStack stack = source.find(player);
        if (!(stack.getItem() instanceof FEItemPortableHive)) {
            return;
        }
        NetworkHooks.openScreen(player,
                new SimpleMenuProvider((id, inv, p) -> new PortableHiveMenu(id, inv, source), stack.getHoverName()),
                buf -> buf.writeEnum(source));
    }

    /** Client-side constructor (called from the menu type factory). */
    public PortableHiveMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory, buf.readEnum(Source.class));
    }

    public PortableHiveMenu(int id, Inventory inventory, Source source) {
        super(ModMenuTypes.PORTABLE_HIVE.get(), id);
        this.player = inventory.player;
        this.source = source;
        this.hive = new HiveStackContainer(source.find(inventory.player));
        this.lockedHotbarSlot = source == Source.MAIN_HAND ? inventory.selected : -1;

        addSlot(new HiveSlot(hive, HiveStackContainer.QUEEN, QUEEN_X, QUEEN_Y));
        addSlot(new HiveSlot(hive, HiveStackContainer.DRONE, DRONE_X, DRONE_Y));
        addSlot(new HiveSlot(hive, HiveStackContainer.FRAME, FRAME_X, FRAME_Y));
        for (int i = 0; i < 6; i++) {
            addSlot(new HiveSlot(hive, HiveStackContainer.PRODUCTS_START + i,
                    PRODUCTS_X + (i % 3) * 18, PRODUCTS_Y + (i / 3) * 18));
        }
        addSlot(new HiveSlot(hive, HiveStackContainer.FLOWER, FLOWER_X, FLOWER_Y));
        for (int i = 0; i < HiveStackContainer.MODULE_SLOTS; i++) {
            addSlot(new HiveSlot(hive, HiveStackContainer.MODULES_START + i, MODULES_X, MODULES_Y + i * 18));
        }
        status.set(STATUS_NOT_WORN);
        progress.set(-1);
        addDataSlot(status);
        addDataSlot(progress);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(col == lockedHotbarSlot
                    ? new LockedSlot(inventory, col, 8 + col * 18, 142)
                    : new Slot(inventory, col, 8 + col * 18, 142));
        }
    }

    /** The hive item this menu edits (the client's synced copy on the client). */
    public ItemStack hiveStack() {
        return hive.hive();
    }

    public HiveTier tier() {
        return HiveTier.of(hive.hive());
    }

    public Set<FEEnumHiveModule> installedModules() {
        return hive.getModules();
    }

    public boolean isModuleSlotUnlocked(int slot) {
        return hive.isModuleSlotUnlocked(slot);
    }

    /** {@link #STATUS_NOT_WORN}, {@link #STATUS_OK} or the numeric id of the first Forestry error. */
    public int getStatus() {
        return status.get();
    }

    /** Forestry's progress (queen's life or mating), 0..100, or -1 when the hive isn't running. */
    public int getProgress() {
        return progress.get();
    }

    @Override
    public void broadcastChanges() {
        PortableHiveHousing housing = housing();
        status.set(housing == null ? STATUS_NOT_WORN : housing.status());
        progress.set(housing == null ? -1 : housing.progressPercent());
        super.broadcastChanges();
    }

    private PortableHiveHousing housing() {
        // only the worn hive runs; one opened from a hand is just storage
        return source == Source.CHEST ? PortableHiveTicker.get(player) : null;
    }

    @Override
    public boolean stillValid(Player player) {
        // closes if the hive was unequipped / swapped away while the menu was open
        return source.find(player) == hive.hive();
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        // block number-key swaps into the hotbar slot holding the open hive
        if (clickType == ClickType.SWAP && button == lockedHotbarSlot) {
            return;
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < HIVE_SLOTS) {
            if (!moveItemStackTo(stack, HIVE_SLOTS, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, 0, HiveStackContainer.PRODUCTS_START, false)
                // separate calls: one range over the products would merge matching items into them
                && !moveItemStackTo(stack, HiveStackContainer.FLOWER, HiveStackContainer.FLOWER + 1, false)
                && !moveItemStackTo(stack, HiveStackContainer.MODULES_START, HiveStackContainer.SIZE, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    private static final class HiveSlot extends Slot {
        HiveSlot(HiveStackContainer container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return container.canPlaceItem(getContainerSlot(), stack);
        }

        @Override
        public int getMaxStackSize() {
            int index = getContainerSlot();
            return index == HiveStackContainer.QUEEN || index == HiveStackContainer.FRAME || index == HiveStackContainer.FLOWER
                    || index >= HiveStackContainer.MODULES_START ? 1 : super.getMaxStackSize();
        }
    }

    private static final class LockedSlot extends Slot {
        LockedSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }
}
