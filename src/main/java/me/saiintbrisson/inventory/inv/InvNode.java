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
public class InvNode<T> {

    private @NonNull Player player;
    private @NonNull Inv<T> owner;
    private @NonNull T object;

    private InvItem[] items;

    private @NonNull @Setter String title;
    private @NonNull int rows;

    private void render() {
        items = owner.getItems();
        owner.render(this, object);
    }

    private Inventory mount() {
        InvHolder holder = new InvHolder(this, player.getUniqueId());
        Inventory inventory = Bukkit.createInventory(holder, rows * 9, title);

        render();

        for (InvItem item : items) {
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
        if(!(holder instanceof InvHolder)) return;
        if(!((InvHolder) holder).getId().equals(player.getUniqueId())) return;

        inventory.clear();
        render();

        for (InvItem item : items) {
            if(item == null) continue;
            inventory.setItem(item.getSlot(), item.getItemStack());
        }

        player.updateInventory();
    }

    public void handleClick(Plugin plugin, InventoryClickEvent event) {
        if(plugin != owner.getOwner()) return;
        if(!(event.getWhoClicked() instanceof Player)) return;

        if(!event.getWhoClicked().getUniqueId().equals(player.getUniqueId())) return;

        InventoryHolder holder = event.getInventory().getHolder();
        if(!(holder instanceof InvHolder)) return;

        InvItem item = getItem(event.getRawSlot());
        if(item == null || item.getClickAction() == null) {
            event.setCancelled(true);
            return;
        }

        item.getClickAction().interact(this, event);
    }

    public void handleOpen(Plugin plugin, InventoryOpenEvent event) {
        if(plugin != owner.getOwner()) return;
        if(!(event.getPlayer() instanceof Player)) return;

        if(!event.getPlayer().getUniqueId().equals(player.getUniqueId())) return;

        InventoryHolder holder = event.getInventory().getHolder();
        if(!(holder instanceof InvHolder)) return;

        InvAction<InventoryOpenEvent> action = owner.getOpenAction();
        if(action == null) return;

        action.interact(this, event);
    }

    public void handleClose(Plugin plugin, InventoryCloseEvent event) {
        if(plugin != owner.getOwner()) return;
        if(!(event.getPlayer() instanceof Player)) return;

        if(!event.getPlayer().getUniqueId().equals(player.getUniqueId())) return;

        InventoryHolder holder = event.getInventory().getHolder();
        if(!(holder instanceof InvHolder)) return;

        InvAction<InventoryCloseEvent> action = owner.getCloseAction();
        if(action == null) return;

        action.interact(this, event);
    }

    public InvItem getItem(int slot) {
        if(slot < 0 || slot >= items.length) return null;

        return items[slot];
    }

    public void setItem(InvItem item) {
        int slot = item.getSlot();
        if(slot < 0 || slot >= items.length) return;

        items[slot] = item;
    }

}
