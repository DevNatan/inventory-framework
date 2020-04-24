package me.saiintbrisson.inventory.inv;

import org.bukkit.event.inventory.InventoryEvent;

public interface InvAction<T, E extends InventoryEvent> {

    void interact(InvNode<T> node, E event);

}
