package me.saiintbrisson.minecraft;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ViewContext {

    private final View view;
    private final Player player;
    private final Inventory inventory;
    private boolean cancelled;

    public ViewContext(View view, Player player, Inventory inventory) {
        this.view = view;
        this.player = player;
        this.inventory = inventory;
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

    public void close() {
        view.close(player);
    }

    public void update() {
        view.update(player);
    }

    public void open(Class<? extends View> view) {
        this.view.getFrame().open(view, player);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) view.getData(player).get(key);
    }

    public void set(String key, Object value) {
        view.setData(player, key, value);
    }

    public boolean has(String key) {
        return view.hasData(player, key);
    }

}