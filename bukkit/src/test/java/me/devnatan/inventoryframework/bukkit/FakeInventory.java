package me.devnatan.inventoryframework.bukkit;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public class FakeInventory implements Inventory {

    private final int size;
    private final InventoryType type;
    private final ItemStack[] items;

    public FakeInventory(int size, InventoryType type) {
        this.size = size;
        this.type = type;
        this.items = new ItemStack[size];
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getMaxStackSize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxStackSize(int size) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public ItemStack getItem(int index) {
        return items[index];
    }

    @Override
    public void setItem(int index, @Nullable ItemStack item) {
        items[index] = item;
    }

    @NotNull
    @Override
    public HashMap<Integer, ItemStack> addItem(@NotNull ItemStack... items) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public HashMap<Integer, ItemStack> removeItem(@NotNull ItemStack... items) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public ItemStack[] getContents() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setContents(@NotNull ItemStack[] items) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public ItemStack[] getStorageContents() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStorageContents(@NotNull ItemStack[] items) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(@NotNull Material material) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(@Nullable ItemStack item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(@NotNull Material material, int amount) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(@Nullable ItemStack item, int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAtLeast(@Nullable ItemStack item, int amount) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public HashMap<Integer, ? extends ItemStack> all(@NotNull Material material) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public HashMap<Integer, ? extends ItemStack> all(@Nullable ItemStack item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int first(@NotNull Material material) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int first(@NotNull ItemStack item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int firstEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(@NotNull Material material) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(@NotNull ItemStack item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public List<HumanEntity> getViewers() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public InventoryType getType() {
        return type;
    }

    @Nullable
    @Override
    public InventoryHolder getHolder() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public ListIterator<ItemStack> iterator() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public ListIterator<ItemStack> iterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public Location getLocation() {
        throw new UnsupportedOperationException();
    }
}
