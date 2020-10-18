package me.saiintbrisson.minecraft;

import org.bukkit.entity.Player;

public class PreRenderViewContext {

    private final View view;
    private final Player player;
    private String inventoryTitle;
    private boolean cancelled;

    public PreRenderViewContext(View view, Player player) {
        this.view = view;
        this.player = player;
    }

    public View getView() {
        return view;
    }

    public Player getPlayer() {
        return player;
    }

    public String getInventoryTitle() {
        return inventoryTitle;
    }

    public void setInventoryTitle(String inventoryTitle) {
        this.inventoryTitle = inventoryTitle;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
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
