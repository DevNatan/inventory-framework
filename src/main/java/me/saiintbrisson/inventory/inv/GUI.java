package me.saiintbrisson.inventory.inv;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

@Getter
public class GUI<T> {

    private Plugin owner;

    private GUIItem<T>[] items;

    @Setter
    private GUIAction<T, InventoryOpenEvent> openAction;
    @Setter
    private GUIAction<T, InventoryCloseEvent> closeAction;

    private String title;
    private int rows;

    public GUI(@NonNull Plugin owner, String title, int rows) {
        this.owner = owner;
        this.title = title;
        this.rows = rows;

        this.items = new GUIItem[rows * 9];
    }

    public GUIItem<T> getItem(int slot) {
        if(slot < 0 || slot >= items.length) return null;

        return items[slot];
    }

    public void appendItem(GUIItem<T> item) {
        if(item == null) return;

        int slot = item.getSlot();
        if(slot < 0 || slot >= items.length) return;

        items[slot] = item;
    }

    protected void render(GUINode<T> node, T object) {}

    public GUINode<T> createNode(Player player, T object) {
        return new GUINode<>(this, object, player, title, rows);
    }

    public GUIHolder createHolder(GUINode<T> node, UUID id) {
        return new GUIHolder(node, id);
    }

}
