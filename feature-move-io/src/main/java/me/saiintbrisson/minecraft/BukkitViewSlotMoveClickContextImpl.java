package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.ToString;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

@Getter
@ToString(callSuper = true)
final class BukkitViewSlotMoveClickContextImpl extends BukkitClickViewSlotContext implements ViewSlotMoveContext {

    private final int targetSlot;
    private final ItemWrapper targetItem, swappedItem;
    private final boolean swap, stack;

    BukkitViewSlotMoveClickContextImpl(
            @NotNull InventoryClickEvent clickOrigin,
            ViewItem backingItem,
            ViewContext parent,
            ViewContainer container,
            Object targetItem,
            Object swappedItem,
            int slot,
            int targetSlot,
            boolean swap,
            boolean stack) {
        super(slot, clickOrigin, backingItem, parent, container);
        this.targetSlot = targetSlot;
        this.targetItem = new ItemWrapper(targetItem);
        this.swappedItem = new ItemWrapper(swappedItem);
        this.swap = swap;
        this.stack = stack;
    }

    @Override
    public @NotNull ViewContainer getTargetContainer() {
        // there's no way to determine target container for now :(
        return getContainer();
    }
}
