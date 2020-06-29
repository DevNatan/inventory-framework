package me.saiintbrisson.minecraft.paginator;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface PaginatedItemConsumer<T extends PaginatedItem> {

    void process(Player player, T item, InventoryClickEvent event);

}
