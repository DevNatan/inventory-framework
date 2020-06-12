package me.saiintbrisson.inventory.inv;

import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

@Getter
@RequiredArgsConstructor
public class GUINode<T> {

    private final GUI<T> owner;
    private final T object;

    private final Player player;

    private GUIItem<T>[] items;

    @NonNull @Setter
    private String title;
    private final int rows;

    private void render() {
        items = owner.getItems();
        owner.render(this, object);
    }

    private Inventory mount() {
        GUIHolder holder = owner.createHolder(this, player.getUniqueId());
        Inventory inventory = Bukkit.createInventory(holder, rows * 9, title);

        render();

        for (GUIItem item : items) {
            if(item == null) continue;
            inventory.setItem(item.getSlot(), item.getItemStack());
        }

        return inventory;
    }

    public void show() {
        player.openInventory(mount());
    }

    public void reload() {
        player.closeInventory();
        show();
    }

    public void update() {
        Inventory inventory = player.getOpenInventory().getTopInventory();

        InventoryHolder holder = inventory.getHolder();
        if(!(holder instanceof GUIHolder)) return;
        if(!((GUIHolder) holder).getId().equals(player.getUniqueId())) return;

        inventory.clear();
        render();

        for (GUIItem<T> item : items) {
            if(item == null) continue;
            inventory.setItem(item.getSlot(), item.getItemStack());
        }

        player.updateInventory();
    }

    public void handleClick(Plugin plugin, InventoryClickEvent event) {
        if(!plugin.equals(owner.getOwner())) return;
        if(!(event.getWhoClicked() instanceof Player)) return;

        if(!event.getWhoClicked().getUniqueId().equals(player.getUniqueId())) return;

        InventoryHolder holder = event.getInventory().getHolder();
        if(!(holder instanceof GUIHolder)) return;

        GUIItem<T> item = getItem(event.getRawSlot());
        if(item == null || item.getClickActions().size() == 0) {
            event.setCancelled(true);
            return;
        }

        item.handleClick(this, event);
    }

    public void handleOpen(Plugin plugin, InventoryOpenEvent event) {
        if(!plugin.equals(owner.getOwner())) return;
        if(!(event.getPlayer() instanceof Player)) return;

        if(!event.getPlayer().equals(player)) return;

        InventoryHolder holder = event.getInventory().getHolder();
        if(!(holder instanceof GUIHolder)) return;

        GUIAction<T, InventoryOpenEvent> action = owner.getOpenAction();
        if(action == null) return;

        action.interact(this, player, event);
    }

    public void handleClose(Plugin plugin, InventoryCloseEvent event) {
        if(!plugin.equals(owner.getOwner())) return;
        if(!(event.getPlayer() instanceof Player)) return;

        if(!event.getPlayer().equals(player)) return;

        InventoryHolder holder = event.getInventory().getHolder();
        if(!(holder instanceof GUIHolder)) return;

        GUIAction<T, InventoryCloseEvent> action = owner.getCloseAction();
        if(action == null) return;

        action.interact(this, player, event);
    }

    public GUIItem<T> getItem(int slot) {
        if(slot < 0 || slot >= items.length) return null;

        return items[slot];
    }

    public void appendItem(GUIItem<T> item) {
        int slot = item.getSlot();
        if(slot < 0 || slot >= items.length) return;

        items[slot] = item;
    }

}
