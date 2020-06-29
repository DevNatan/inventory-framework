package me.saiintbrisson.minecraft.view;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;

public interface ViewAction<T, E extends InventoryEvent> {

    void interact(ViewNode<T> node, Player player, E event);

}
