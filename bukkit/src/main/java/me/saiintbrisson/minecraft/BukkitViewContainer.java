package me.saiintbrisson.minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import me.devnatan.inventoryframework.internal.platform.Viewer;
import me.saiintbrisson.minecraft.thirdparty.InventoryUpdate;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

@RequiredArgsConstructor
final class BukkitViewContainer implements ViewContainer {

    @Getter
    @NotNull
    private final Inventory inventory;

    @Override
    public @NotNull ViewType getType() {
        //		if (type == null)
        //			throw new IllegalStateException("View type cannot be null for " + inventory.getType() + " inventory type");

        // TODO do proper conversion from InventoryType to supported Bukkit type
        return ViewType.CHEST;
    }

    @Override
    public int getRowsCount() {
        // TODO this "9" only works for chest types
        return getSize() / 9;
    }

    @Override
    public int getColumnsCount() {
        // TODO this "9" only works for chest types
        return 9;
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
        inventory.setItem(slot, convertItem(item));
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
        if (item instanceof Material) return target.getType() == item;

        return false;
    }

    @Override
    public ItemStack convertItem(Object source) {
        requireSupportedItem(source);

        if (source instanceof ItemStack) return ((ItemStack) source).clone();
        if (source instanceof Material) return new ItemStack((Material) source);

        return null;
    }

    @Override
    public boolean isSupportedItem(Object item) {
        return item == null || item instanceof ItemStack || item instanceof Material;
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
        for (final Viewer viewer : getViewers())
            InventoryUpdate.updateInventory(((BukkitViewer) viewer).getPlayer(), title);
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
}
