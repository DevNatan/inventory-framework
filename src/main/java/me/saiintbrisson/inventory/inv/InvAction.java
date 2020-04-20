package me.saiintbrisson.inventory.inv;

import org.bukkit.event.inventory.InventoryEvent;

public interface InvAction<T extends InventoryEvent> {

    void interact(InvNode node, T event);

}
