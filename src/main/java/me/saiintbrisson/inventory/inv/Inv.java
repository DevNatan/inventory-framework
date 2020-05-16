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
public class Inv<T> {

    private Plugin owner;

    private InvItem<T>[] items;

    @Setter
    private InvAction<T, InventoryOpenEvent> openAction;
    @Setter
    private InvAction<T, InventoryCloseEvent> closeAction;

    private String title;
    private int rows;

    public Inv(@NonNull Plugin owner, String title, int rows) {
        this.owner = owner;
        this.title = title;
        this.rows = rows;

        this.items = new InvItem[rows * 9];
    }

    public InvItem<T> getItem(int slot) {
        if(slot < 0 || slot >= items.length) return null;

        return items[slot];
    }

    public void setItem(InvItem<T> item) {
        if(item == null) return;

        int slot = item.getSlot();
        if(slot < 0 || slot >= items.length) return;

        items[slot] = item;
    }

    protected void render(InvNode<T> node, T object) {}

    public InvNode<T> createNode(Player player, T object) {
        return new InvNode<>(
          player,
          this,
          object,
          title,
          rows
        );
    }

    public InvHolder createHolder(InvNode<T> node, UUID id) {
        return new InvHolder(node, id);
    }

}
