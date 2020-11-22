package me.saiintbrisson.minecraft;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ViewSlotContext extends ViewContext {

    private final int slot;
    private InventoryClickEvent clickOrigin;
    private ItemStack item;
    private boolean changed;
    private final Map<String, Object> slotData;

    public ViewSlotContext(View view, Player player, Inventory inventory, int slot, ItemStack item) {
        super(view, player, inventory);
        this.slot = slot;
        this.item = item == null ? null : item.clone();
        slotData = new HashMap<>();
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
        changed = true;
    }

    public Map<String, Object> getSlotData() {
        return slotData;
    }

    @SuppressWarnings("unchecked")
    public <T> T getSlotData(String key) {
        if (!slotData.containsKey(key))
            return null;

        return (T) slotData.get(key);
    }

    public <T> T getSlotData(String key, Supplier<T> defaultValue) {
        T value = getSlotData(key);
        if (value == null)
            return defaultValue.get();

        return value;
    }

    public void setSlotData(String key, Object value) {
        slotData.put(key, value);
    }

    public boolean hasSlotData(String key) {
        return slotData.containsKey(key);
    }

    public InventoryClickEvent getClickOrigin() {
        return clickOrigin;
    }

    void setClickOrigin(InventoryClickEvent clickOrigin) {
        this.clickOrigin = clickOrigin;
    }

    boolean hasChanged() {
        return changed;
    }

    public void updateSlot() {
        view.update(this, slot);
    }

    @Override
    public String toString() {
        return "ViewSlotContext{" +
                "slot=" + slot +
                ", clickOrigin=" + clickOrigin +
                ", item=" + item +
                ", changed=" + changed +
                "} " + super.toString();
    }

}