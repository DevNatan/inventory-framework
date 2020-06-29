package me.saiintbrisson.minecraft;

import lombok.AllArgsConstructor;
import me.saiintbrisson.minecraft.paginator.PaginatedViewHolder;
import me.saiintbrisson.minecraft.view.ViewHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

@AllArgsConstructor
public class InventoryListener implements Listener {

    private final Plugin plugin;

    @EventHandler
    public void handleClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            if (!viewHolder.getId().equals(event.getWhoClicked().getUniqueId())) {
                return;
            }

            viewHolder.getNode().handleClick(plugin, event);
        } else if (holder instanceof PaginatedViewHolder) {
            PaginatedViewHolder viewHolder = (PaginatedViewHolder) holder;
            if (!viewHolder.getId().equals(event.getWhoClicked().getUniqueId())) {
                return;
            }

            viewHolder.getOwner().handleClick(plugin, ((PaginatedViewHolder) holder), event);
        }
    }

    @EventHandler
    public void handleOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            if (!viewHolder.getId().equals(event.getPlayer().getUniqueId())) {
                return;
            }

            viewHolder.getNode().handleOpen(plugin, event);
        }
    }

    @EventHandler
    public void handleClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            if (!viewHolder.getId().equals(event.getPlayer().getUniqueId())) {
                return;
            }

            viewHolder.getNode().handleClose(plugin, event);
        }
    }

}
