package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.ToString;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

@Getter
@ToString(callSuper = true)
final class ViewSlotMoveContextImpl extends BukkitClickViewSlotContext
        implements ViewSlotMoveContext {

    @ToString.Exclude private final ViewContainer targetContainer;

    private final ItemWrapper targetItem, swappedItem;
    private final int targetSlot;
    private final boolean swap, stack;

    ViewSlotMoveContextImpl(
            ViewItem backingItem,
            @NotNull BaseViewContext parent,
            @NotNull InventoryClickEvent clickOrigin,
            ViewContainer targetContainer,
            Object targetItem,
            Object swappedItem,
            int targetSlot,
            boolean swap,
            boolean stack) {
        super(backingItem, parent, clickOrigin);
        this.targetContainer = targetContainer;
        this.targetItem = new ItemWrapper(targetItem);
        this.swappedItem = new ItemWrapper(swappedItem);
        this.targetSlot = targetSlot;
        this.swap = swap;
        this.stack = stack;
    }
}
