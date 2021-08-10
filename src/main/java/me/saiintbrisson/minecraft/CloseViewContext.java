package me.saiintbrisson.minecraft;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class CloseViewContext extends ViewContext {

    public CloseViewContext(View view, Player player, Inventory inventory) {
        super(view, player, inventory);
    }

}
