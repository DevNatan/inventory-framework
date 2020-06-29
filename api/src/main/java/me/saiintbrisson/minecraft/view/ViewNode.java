package me.saiintbrisson.minecraft.view;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

@Getter
@RequiredArgsConstructor
public class ViewNode<T> {

    private final View<T> owner;
    private final T object;

    private ViewItem<T>[] items;

    @NonNull
    @Setter
    private String title;
    private final int rows;

    public ViewItem<T> slot(int slot) {
        return items[slot] = new ViewItem<>();
    }

    public ViewItem<T> slot(int row, int column) {
        return slot((row * 9) + column);
    }

    public ViewItem<T> getItem(int slot) {
        if (slot < 0 || slot >= items.length) {
            return null;
        }

        return items[slot];
    }

    public void appendItem(ViewItem<T> item) {
        int slot = item.getSlot();
        if (slot < 0 || slot >= items.length) return;

        items[slot] = item;
    }

    private void render(Player player, Inventory inventory) {
        items = owner.getItems();

        inventory.clear();

        owner.apply(inventory);
        owner.render(this, player, object);

        for (int i = 0; i < items.length; i++) {
            ViewItem<T> item = items[i];
            if (item == null) continue;

            inventory.setItem(i, item.getItemStack());
        }
    }

    public void show(Player player) {
        ViewHolder holder = owner.createHolder(this, player.getUniqueId());
        Inventory inventory = Bukkit.createInventory(holder, rows * 9, title);

        render(player, inventory);

        player.openInventory(inventory);
    }

    public void update(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        InventoryHolder holder = inventory.getHolder();

        if (!(holder instanceof ViewHolder)) return;
        if (!((ViewHolder) holder).getId().equals(player.getUniqueId())) return;

        render(player, inventory);

        player.updateInventory();
    }

    public void handleClick(Plugin plugin, InventoryClickEvent event) {
        if (!plugin.equals(owner.getOwner())) return;

        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof ViewHolder)) return;

        ViewItem<T> item = getItem(event.getRawSlot());
        if (item == null || item.getClickActions().size() == 0) {
            event.setCancelled(true);
            return;
        }

        item.handleClick(this, event);
    }

    public void handleOpen(Plugin plugin, InventoryOpenEvent event) {
        if (!plugin.equals(owner.getOwner())) return;

        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof ViewHolder)) return;

        ViewAction<T, InventoryOpenEvent> action = owner.getOpenAction();
        if (action == null) return;

        action.interact(this, (Player) event.getPlayer(), event);
    }

    public void handleClose(Plugin plugin, InventoryCloseEvent event) {
        if (!plugin.equals(owner.getOwner())) return;

        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof ViewHolder)) return;

        ViewAction<T, InventoryCloseEvent> action = owner.getCloseAction();
        if (action == null) return;

        action.interact(this, (Player) event.getPlayer(), event);
    }

}
