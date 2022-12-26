package me.saiintbrisson.minecraft;

import lombok.Getter;
import me.devnatan.inventoryframework.IFContext;
import me.devnatan.inventoryframework.IFSlotMoveContext;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class ViewSlotMoveContext extends ViewSlotClickContext implements IFSlotMoveContext {

    private final int targetSlot;
    private final ItemStack targetItem, swappedItem;
    private final boolean swap, stack;

    ViewSlotMoveContext(
            int slot,
            @NotNull IFContext parent,
            @Nullable ViewContainer container,
            @NotNull InventoryClickEvent clickOrigin,
            ItemStack targetItem,
            ItemStack swappedItem,
            int targetSlot,
            boolean swap,
            boolean stack) {
        super(slot, parent, container, clickOrigin);
        this.targetItem = targetItem;
        this.swappedItem = swappedItem;
        this.targetSlot = targetSlot;
        this.swap = swap;
        this.stack = stack;
    }

    @Override
    public final @NotNull ViewContainer getTargetContainer() {
        // there's no way to determine target container for now :(
        return getContainer();
    }
}
