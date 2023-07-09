package me.devnatan.inventoryframework.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public final class BukkitViewContainer implements ViewContainer {

    private final Inventory inventory;
    private final boolean shared;
    private final ViewType type;

    public BukkitViewContainer(@NotNull Inventory inventory, boolean shared, ViewType type) {
        this.inventory = inventory;
        this.shared = shared;
        this.type = type;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public boolean isShared() {
        return shared;
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
    public String getTitle(@NotNull Viewer viewer) {
        return ((BukkitViewer) viewer).getPlayer().getOpenInventory().getTitle();
    }

    @Override
    public @NotNull ViewType getType() {
        return type;
    }

    @Override
    public int getRowsCount() {
        return type.getRows();
    }

    @Override
    public int getColumnsCount() {
        return type.getColumns();
    }

    @Override
    public @NotNull @Unmodifiable List<Viewer> getViewers() {
        return Collections.unmodifiableList(new ArrayList<>(inventory.getViewers().stream()
                .filter(humanEntity -> humanEntity instanceof Player)
                .map(humanEntity -> new BukkitViewer((Player) humanEntity))
                .collect(Collectors.toList())));
    }

    @Override
    public void renderItem(int slot, Object item) {
        requireSupportedItem(item);
        inventory.setItem(slot, (ItemStack) item);
    }

    @Override
    public void removeItem(int slot) {
        inventory.setItem(slot, null);
    }

    @Override
    public boolean matchesItem(int slot, Object item, boolean exactly) {
        requireSupportedItem(item);
        final ItemStack target = inventory.getItem(slot);
        if (target == null) return item == null;
        if (item instanceof ItemStack) return exactly ? target.equals(item) : target.isSimilar((ItemStack) item);

        return false;
    }

    @Override
    public boolean isSupportedItem(Object item) {
        return item == null || item instanceof ItemStack;
    }

    private void requireSupportedItem(Object item) {
        if (isSupportedItem(item)) return;

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
        return inventory.getSize() - 1;
    }

    @Override
    public int getFirstSlot() {
        return 0;
    }

    @Override
    public int getLastSlot() {
        return getSlotsCount();
    }

    @Override
    public void changeTitle(@Nullable final String title) {
        for (final Viewer viewer : getViewers()) changeTitle(title, viewer);
    }

    @Override
    public void changeTitle(@Nullable String title, @NotNull Viewer target) {
        InventoryUpdate.updateInventory(((BukkitViewer) target).getPlayer(), title);
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
