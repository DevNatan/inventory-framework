package me.saiintbrisson.inventory.inv;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;

public interface GUIAction<T, E extends InventoryEvent> {

    void interact(GUINode<T> node, Player player, E event);

}
