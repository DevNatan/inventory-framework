package me.devnatan.inventoryframework;

import java.util.ArrayList;
import java.util.Objects;
import me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BukkitViewContainer implements ViewContainer {

    private final Inventory inventory;
    private final boolean shared;
    private final ViewType type;
    private final boolean proxied;

    public BukkitViewContainer(@NotNull Inventory inventory, boolean shared, ViewType type, boolean proxied) {
        this.inventory = inventory;
        this.shared = shared;
        this.type = type;
        this.proxied = proxied;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public boolean isShared() {
        return shared;
    }

    @Override
    public boolean isProxied() {
        return proxied;
    }

    @Override
    public String getTitle() {
        final boolean diffTitle = inventory.getViewers().stream()
                .map(HumanEntity::getOpenInventory)
                .map(InventoryView::getTitle)
                .distinct()
                .findAny()
                .isPresent();

        if (diffTitle && shared) throw new IllegalStateException("Cannot get unique title of shared inventory");

        return inventory.getViewers().get(0).getOpenInventory().getTitle();
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
        return 0;
    }

    @Override
    public int getLastSlot() {
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
        return shared == that.shared
                && Objects.equals(inventory, that.inventory)
                && Objects.equals(getType(), that.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventory, shared, getType());
    }

    @Override
    public String toString() {
        return "BukkitViewContainer{" + "inventory=" + inventory + ", shared=" + shared + ", type=" + type + '}';
    }
}
