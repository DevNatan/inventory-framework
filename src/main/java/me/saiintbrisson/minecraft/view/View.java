package me.saiintbrisson.minecraft.view;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

@Getter
public class View<T> {

    private final Plugin owner;

    private final ViewItem<T>[] items;

    @Setter
    private ViewAction<T, InventoryOpenEvent> openAction;
    @Setter
    private ViewAction<T, InventoryCloseEvent> closeAction;

    private final String title;
    private final int rows;

    public View(@NonNull Plugin owner, String title, int rows) {
        this.owner = owner;
        this.title = title;
        this.rows = rows;

        this.items = new ViewItem[rows * 9];
    }

    public ViewItem<T> slot(int slot) {
        return items[slot] = new ViewItem<>();
    }

    /**
     * @param row the row starting from one
     * @param column the column starting from one
     * @return the item created
     */
    public ViewItem<T> slot(int row, int column) {
        return slot(((row - 1) * 9) + (column - 1));
    }

    public ViewItem<T> getItem(int slot) {
        if(slot < 0 || slot >= items.length) {
            return null;
        }

        return items[slot];
    }

    public void appendItem(ViewItem<T> item) {
        if(item == null) return;

        int slot = item.getSlot();
        if(slot < 0 || slot >= items.length) return;

        items[slot] = item;
    }

    protected void render(ViewNode<T> node, Player player, T object) {}

    public ViewNode<T> createNode(T object) {
        return new ViewNode<>(this, object, title, rows);
    }

    public ViewHolder createHolder(ViewNode<T> node, UUID id) {
        return new ViewHolder(node, id);
    }

    public final void apply(@NonNull Inventory inventory) {
        for (int i = 0; i < items.length; i++) {
            ViewItem<T> item = items[i];
            if (item == null) continue;

            inventory.setItem(i, item.getItemStack());
        }
    }

}
