package me.saiintbrisson.inventory.paginator;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface PaginatedItemConsumer<T extends PaginatedItem> {

    void process(Player player, T item, InventoryClickEvent event);

}
