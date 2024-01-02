package me.devnatan.inventoryframework;

import java.util.ArrayList;
import java.util.Objects;
import me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BukkitViewContainer implements ViewContainer {

    private final Inventory inventory;
    private final ViewType type;
    private final boolean proxied, external;

    public BukkitViewContainer(@NotNull Inventory inventory, ViewType type) {
        this(inventory, type, false, false);
    }

    public BukkitViewContainer(@NotNull Inventory inventory, ViewType type, boolean proxied, boolean external) {
        this.inventory = inventory;
        this.type = type;
        this.proxied = proxied;
        this.external = external;
    }

    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public boolean isProxied() {
        return proxied;
    }

    @Override
    public ViewContainer at(int slot) {
        return slot >= getFirstSlot() && slot <= getLastSlot() ? this : null;
    }

    @Override
    public boolean isExternal() {
        return external;
    }

    @Override
    public @NotNull ViewType getType() {
        return type;
    }

    @Override
    public int getRowsCount() {
        return getSize() / getColumnsCount();
    }

    @Override
    public int getColumnsCount() {
        return type.getColumns();
    }

    public void renderItem(int slot, ItemStack item) {
        requireSupportedItem(item);
        inventory.setItem(slot, item);
    }

    @Override
    public void removeItem(int slot) {
        inventory.setItem(slot, null);
    }

    @Override
    public void renderItem(int slot, Object platformItem) {
        final int fixedSlot = isEntityContainer() && isProxied() ? getLastSlot() - slot : slot;
        inventory.setItem(fixedSlot, (ItemStack) platformItem);
    }

    private void requireSupportedItem(Object item) {
        if (item == null || item instanceof ItemStack) return;

        throw new IllegalStateException(
                "Unsupported item type: " + item.getClass().getName());
    }

    @Override
    public boolean hasItem(int slot) {
        return inventory.getItem(slot) != null;
    }

    @Override
    public int getSize() {
        return inventory.getSize();
    }

    @Override
    public int getSlotsCount() {
        return getSize() - 1;
    }

    @Override
    public int getFirstSlot() {
        return getType() == ViewType.PLAYER ? 45 : 0;
    }

    @Override
    public int getLastSlot() {
        if (isEntityContainer()) return inventory.getSize() - 1;

        final int[] resultSlots = getType().getResultSlots();
        int lastSlot = getSlotsCount();
        if (resultSlots != null) {
            for (final int resultSlot : resultSlots) {
                if (resultSlot == lastSlot) lastSlot--;
            }
        }

        return lastSlot;
    }

    @Override
    public void changeTitle(@Nullable String title, @NotNull Viewer target) {
        changeTitle(title, ((BukkitViewer) target).getPlayer());
    }

    public void changeTitle(@Nullable String title, @NotNull Player target) {
        InventoryUpdate.updateInventory(target, title);
    }

    @Override
    public boolean isEntityContainer() {
        return inventory instanceof PlayerInventory;
    }

    @Override
    public void open(@NotNull final Viewer viewer) {
        viewer.open(this);
    }

    @Override
    public void close() {
        new ArrayList<>(inventory.getViewers()).forEach(HumanEntity::closeInventory);
    }

    @Override
    public void close(@NotNull Viewer viewer) {
        viewer.close();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BukkitViewContainer that = (BukkitViewContainer) o;
        return Objects.equals(inventory, that.inventory) && Objects.equals(getType(), that.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventory, getType());
    }

    @Override
    public String toString() {
        return "BukkitViewContainer{" + "inventory=" + inventory + ", type=" + type + '}';
    }
}
