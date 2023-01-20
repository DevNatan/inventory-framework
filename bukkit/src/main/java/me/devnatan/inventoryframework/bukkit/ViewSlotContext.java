package me.devnatan.inventoryframework.bukkit;

import me.devnatan.inventoryframework.ViewItem;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ViewSlotContext extends AbstractViewSlotContext implements IFContext, IFSlotContext {

    private final Player player;

    ViewSlotContext(
            int slot,
            ViewItem backingItem,
            @NotNull IFContext parent,
            @Nullable ViewContainer container,
            @NotNull Player player) {
        super(slot, backingItem, parent, container);
        this.player = player;
    }

    public ItemStack getItem() {
        // TODO remove this :)
        return ((BukkitViewContainer) getContainer()).getInventory().getItem(getSlot());
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }
}
