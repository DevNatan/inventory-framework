package me.saiintbrisson.minecraft;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ViewContext extends VirtualView {

    protected final View view;
    protected final Player player;
    protected final Inventory inventory;
    protected boolean cancelled;
    private final List<View> history;
    private int historyIndex;

    public ViewContext(View view, Player player, Inventory inventory) {
        super(inventory == null ? null : new ViewItem[View.INVENTORY_ROW_SIZE * (inventory.getSize() / 9)]);
        this.view = view;
        this.player = player;
        this.inventory = inventory;
        history = new LinkedList<>();
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

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Deprecated
    public void cancel() {
        this.cancelled = true;
    }

    public Map<String, Object> getData() {
        return view.getData(player);
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

    public <T> T get(String key) {
        return view.getData(player, key);
    }

    public <T> T get(String key, Supplier<T> defaultValue) {
        return view.getData(player, key, defaultValue);
    }

    public void set(String key, Object value) {
        view.setData(player, key, value);
    }

    public boolean has(String key) {
        return view.hasData(player, key);
    }

    public List<View> getHistory() {
        return history;
    }

    void invalidate() {
        view.clearData(player);
        getHistory().clear();
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
                ", data=" + getData() +
                "} " + super.toString();
    }

}