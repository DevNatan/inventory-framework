package me.saiintbrisson.minecraft;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ViewContext extends VirtualView {

    protected final View view;
    protected final Player player;
    protected final Inventory inventory;
    protected boolean cancelled;
    private Map<Integer, Map<String, Object>> slotData;

    public ViewContext(View view, Player player, Inventory inventory) {
        super(inventory == null ? null : new ViewItem[View.INVENTORY_ROW_SIZE * (inventory.getSize() / 9)]);
        this.view = view;
        this.player = player;
        this.inventory = inventory;
        slotData = new HashMap<>();
    }

    public View getView() {
        return view;
    }

    public Player getPlayer() {
        return player;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }

    public Map<String, Object> data() {
        return view.getData(player);
    }

    public void setData(Map<String, Object> data) {
        view.setData(player, data);
    }

    public Map<Integer, Map<String, Object>> slotData() {
        return slotData;
    }

    public void setSlotData(Map<Integer, Map<String, Object>> slotData) {
        this.slotData = slotData;
    }

    @Override
    public ViewItem slot(int slot) {
        final ViewItem item = super.slot(slot);
        item.setCancelOnClick(view.isCancelOnClick());
        return item;
    }

    public void close() {
        player.closeInventory();
    }

    public void update() {
        view.update(this);
    }

    public void render() {
        view.render(this);
    }

    public void clear(int slot) {
        getItems()[slot] = null;
        inventory.setItem(slot, null);
    }

    public void clear() {
        for (int i = 0; i < getItems().length; i++) {
            clear(i);
        }
    }

    public void open(Class<? extends View> view) {
        this.view.getFrame().open(view, player);
    }

    public void open(Class<? extends View> view, Map<String, Object> data) {
        this.view.getFrame().open(view, player, data);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) view.getData(player, key);
    }

    public <T> T get(String key, Supplier<T> defaultValue) {
        T value = get(key);
        if (value == null)
            return defaultValue.get();

        return value;
    }

    @SuppressWarnings("unchecked")
    public <T> T getSlotData(int slot, String key) {
        if (!slotData().containsKey(slot))
            return null;

        return (T) slotData().get(slot).get(key);
    }

    public <T> T getSlotData(int slot, String key, Supplier<T> defaultValue) {
        T value = getSlotData(slot, key);
        if (value == null)
            return defaultValue.get();

        return value;
    }

    public void set(String key, Object value) {
        data().put(key, value);
    }

    public void setSlotData(int slot, String key, Object value) {
        slotData().computeIfAbsent(slot, ($) -> Maps.newHashMap()).put(key, value);
    }

    public boolean has(String key) {
        return view.hasData(player, key);
    }

    public boolean hasSlotData(int slot, String key) {
        return slotData().containsKey(slot) && slotData().get(slot).containsKey(key);
    }

    void invalidate() {
        view.clearData(player);
        slotData().clear();
    }

    @Override
    public int getLastSlot() {
        return inventory.getSize() - 1;
    }

    @Override
    public String toString() {
        return "ViewContext{" +
                "view=" + view +
                ", player=" + player +
                ", inventory=" + inventory +
                ", cancelled=" + cancelled +
                ", data=" + data() +
                ", slotData=" + slotData() +
                "} " + super.toString();
    }

}