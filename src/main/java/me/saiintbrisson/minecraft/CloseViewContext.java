package me.saiintbrisson.minecraft;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import static me.saiintbrisson.minecraft.View.INVENTORY_ROW_SIZE;

public final class CloseViewContext extends ViewContext {

    public CloseViewContext(View view, Player player, Inventory inventory) {
        super(view, player, inventory);
    }

}
