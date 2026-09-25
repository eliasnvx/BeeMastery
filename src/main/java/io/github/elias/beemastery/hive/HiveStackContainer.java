package io.github.elias.beemastery.hive;

import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.genetics.BeeLifeStage;
import forestry.api.apiculture.hives.IHiveFrame;
import forestry.api.genetics.capability.IIndividualHandlerItem;
import io.github.elias.beemastery.item.FEEnumHiveModule;
import io.github.elias.beemastery.item.FEItemHiveModule;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.Set;

/**
 * The portable hive's inventory, living inside the hive item's {@value #TAG} NBT list
 * (entries {@code {Slot: b, id, Count, tag}}, like a chest's {@code Items}).
 *
 * <p>There is deliberately no cached copy: every read and write goes straight to the item
 * stack. The GUI and the per-tick beekeeping logic can then both touch the same hive without
 * one overwriting the other's changes with stale data.
 */
public class HiveStackContainer implements Container, IBeeHousingInventory {

    /** NBT list on the hive item holding its contents. */
    public static final String TAG = "hive_contents";

    public static final int QUEEN = 0;
    public static final int DRONE = 1;
    public static final int FRAME = 2;
    public static final int PRODUCTS_START = 3;
    public static final int PRODUCTS_END = 9;
    /** Added after the product slots so hives filled before the flower slot existed keep their layout. */
    public static final int FLOWER = 9;
    /** Module slots; how many of them are usable depends on the hive's {@link HiveTier}. */
    public static final int MODULES_START = 10;
    public static final int MODULE_SLOTS = 4;
    public static final int SIZE = MODULES_START + MODULE_SLOTS;

    private final ItemStack hive;
    /** The drone stack last handed to Forestry, see {@link #commitDrone()}. */
    private ItemStack lentDrone = ItemStack.EMPTY;
    private int lentDroneCount;

    public HiveStackContainer(ItemStack hive) {
        this.hive = hive;
    }

    public ItemStack hive() {
        return hive;
    }

    private ListTag entries() {
        CompoundTag tag = hive.getTag();
        return tag == null ? new ListTag() : tag.getList(TAG, Tag.TAG_COMPOUND);
    }

    private static int slotOf(CompoundTag entry) {
        return entry.getByte("Slot") & 255;
    }

    private NonNullList<ItemStack> load() {
        NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ListTag entries = entries();
        for (int i = 0; i < entries.size(); i++) {
            CompoundTag entry = entries.getCompound(i);
            int slot = slotOf(entry);
            if (slot < SIZE) {
                items.set(slot, ItemStack.of(entry));
            }
        }
        return items;
    }

    private void store(NonNullList<ItemStack> items) {
        ListTag entries = new ListTag();
        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack stack = items.get(slot);
            if (!stack.isEmpty()) {
                CompoundTag entry = new CompoundTag();
                entry.putByte("Slot", (byte) slot);
                stack.save(entry);
                entries.add(entry);
            }
        }
        if (!entries.isEmpty()) {
            hive.getOrCreateTag().put(TAG, entries);
            return;
        }
        CompoundTag tag = hive.getTag();
        if (tag != null) {
            tag.remove(TAG);
            if (tag.isEmpty()) {
                hive.setTag(null);  // an emptied hive is identical to a freshly crafted one again
            }
        }
    }

    // --- Container -------------------------------------------------------------------------

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        return load().stream().allMatch(ItemStack::isEmpty);
    }

    /** Parses just the requested slot: Forestry and the menu read single slots many times a tick. */
    @Override
    public ItemStack getItem(int slot) {
        ListTag entries = entries();
        for (int i = 0; i < entries.size(); i++) {
            CompoundTag entry = entries.getCompound(i);
            if (slotOf(entry) == slot) {
                return ItemStack.of(entry);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        NonNullList<ItemStack> items = load();
        ItemStack taken = items.get(slot).split(amount);
        store(items);
        return taken;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        NonNullList<ItemStack> items = load();
        ItemStack taken = items.get(slot);
        items.set(slot, ItemStack.EMPTY);
        store(items);
        return taken;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        NonNullList<ItemStack> items = load();
        items.set(slot, stack);
        store(items);
    }

    @Override
    public void setChanged() {
        // writes are already persisted immediately
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        store(NonNullList.withSize(SIZE, ItemStack.EMPTY));
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return switch (slot) {
            case QUEEN -> IIndividualHandlerItem.filter(stack, (ind, stage) ->
                    stage == BeeLifeStage.PRINCESS || stage == BeeLifeStage.QUEEN);
            case DRONE -> IIndividualHandlerItem.filter(stack, (ind, stage) -> stage == BeeLifeStage.DRONE);
            case FRAME -> stack.getItem() instanceof IHiveFrame;
            case FLOWER -> HiveFlowers.isAnyFlower(stack);
            default -> slot >= MODULES_START && canInstallModule(slot, stack);  // products are output-only
        };
    }

    /** Whether the slot is unlocked by the hive's tier. */
    public boolean isModuleSlotUnlocked(int slot) {
        return slot >= MODULES_START && slot - MODULES_START < HiveTier.of(hive).moduleSlots();
    }

    /** One of each module, none that conflicts with an installed one, and only into an unlocked slot. */
    private boolean canInstallModule(int slot, ItemStack stack) {
        if (!(stack.getItem() instanceof FEItemHiveModule module) || !isModuleSlotUnlocked(slot)) {
            return false;
        }
        for (int i = MODULES_START; i < SIZE; i++) {
            if (i != slot && getItem(i).getItem() instanceof FEItemHiveModule installed
                    && (installed.getType() == module.getType() || installed.getType().conflictsWith(module.getType()))) {
                return false;
            }
        }
        return true;
    }

    /** Installed modules; a module left in a slot the tier doesn't unlock does nothing. */
    public Set<FEEnumHiveModule> getModules() {
        Set<FEEnumHiveModule> modules = EnumSet.noneOf(FEEnumHiveModule.class);
        NonNullList<ItemStack> items = load();
        for (int i = MODULES_START; i < SIZE; i++) {
            if (isModuleSlotUnlocked(i) && items.get(i).getItem() instanceof FEItemHiveModule module) {
                modules.add(module.getType());
            }
        }
        return modules;
    }

    // --- IBeeHousingInventory ---------------------------------------------------------------

    @Override
    public ItemStack getQueen() {
        return getItem(QUEEN);
    }

    @Override
    public ItemStack getDrone() {
        ItemStack drone = getItem(DRONE);
        lentDrone = drone;
        lentDroneCount = drone.getCount();
        return drone;
    }

    /**
     * Forestry uses up a drone when a princess mates by shrinking the very stack {@link #getDrone()}
     * returned (and only calls {@link #setDrone} once it is empty). Here that stack is a copy read
     * from the item, so a shrink of a larger drone stack would be lost; write it back instead.
     * Called after each work tick.
     */
    public void commitDrone() {
        ItemStack lent = lentDrone;
        lentDrone = ItemStack.EMPTY;
        if (lent.isEmpty() || lent.getCount() == lentDroneCount) {
            return;  // untouched, or used up (Forestry has already emptied the slot itself)
        }
        ItemStack stored = getItem(DRONE);
        if (stored.getCount() == lentDroneCount && ItemStack.isSameItemSameTags(stored, lent)) {
            setItem(DRONE, lent.copy());
        }
    }

    @Override
    public void setQueen(ItemStack stack) {
        setItem(QUEEN, stack);
    }

    @Override
    public void setDrone(ItemStack stack) {
        setItem(DRONE, stack);
    }

    public ItemStack getFrame() {
        return getItem(FRAME);
    }

    public void setFrame(ItemStack stack) {
        setItem(FRAME, stack);
    }

    public ItemStack getFlower() {
        return getItem(FLOWER);
    }

    /**
     * Merge a product into the output slots. With {@code all} set, the whole stack must fit or
     * nothing is added (Forestry's contract for produce/offspring).
     */
    @Override
    public boolean addProduct(ItemStack product, boolean all) {
        NonNullList<ItemStack> items = load();
        ItemStack remaining = product.copy();
        for (int i = PRODUCTS_START; i < PRODUCTS_END && !remaining.isEmpty(); i++) {
            ItemStack slot = items.get(i);
            if (slot.isEmpty()) {
                items.set(i, remaining);
                remaining = ItemStack.EMPTY;
            } else if (ItemStack.isSameItemSameTags(slot, remaining)) {
                int move = Math.min(remaining.getCount(), slot.getMaxStackSize() - slot.getCount());
                slot.grow(move);
                remaining.shrink(move);
            }
        }
        if (all && !remaining.isEmpty()) {
            return false;
        }
        store(items);
        return remaining.isEmpty();
    }
}
