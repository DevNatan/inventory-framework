package me.saiintbrisson.minecraft;

import me.saiintbrisson.minecraft.pipeline.PipelineContext;
import me.saiintbrisson.minecraft.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;

public final class BukkitMoveOutInterceptor implements PipelineInterceptor<BukkitClickViewSlotContext> {

    @Override
    public void intercept(
            @NotNull final PipelineContext<BukkitClickViewSlotContext> pipeline,
            final BukkitClickViewSlotContext subject) {
        if (subject.isCancelled()) return;

        // TODO platform agnostic & testable impl
        //        final InventoryClickEvent event = subject.getClickOrigin();
        //        final InventoryAction action = event.getAction();
        //
        //        // fast path -- move out detects items being moved to entity container
        //        if (!subject.isOnEntityContainer()) return;
        //
        //        if (action != InventoryAction.PLACE_ALL
        //                && action != InventoryAction.PLACE_ONE
        //                && action != InventoryAction.PLACE_SOME
        //                && action != InventoryAction.SWAP_WITH_CURSOR) return;
        //
        //        final ItemStack swappedItem = action == InventoryAction.SWAP_WITH_CURSOR ? event.getCurrentItem() :
        // null;
        //
        //        ViewItem hold = null;
        //
        //        final ViewContainer container = subject.getContainer();
        //
        //        for (int i = container.getFirstSlot(); i <= container.getLastSlot(); i++) {
        //            final ViewItem item = subject.resolve(i, true);
        //            if (item == null) continue;
        //
        //            // fast path -- skip not yet hold items
        //            if (item.getState() != ViewItem.State.HOLDING) continue;
        //
        //            hold = item;
        //            break;
        //        }
        //
        //        if (hold == null) return;
        //
        //        final IFSlotMoveContext moveContext = new BukkitViewSlotMoveClickContextImpl(
        //                event,
        //                hold,
        //                subject,
        //                container,
        //                event.getCursor(),
        //                swappedItem,
        //                hold.getSlot(),
        //                event.getSlot(),
        //                swappedItem != null,
        //                false);
        //        moveContext.setCancelled(subject.isCancelled());
        //        if (hold.getMoveOutHandler() != null) hold.getMoveInHandler().accept(moveContext);
        //
        //        subject.getRoot().onMoveOut(moveContext);
        //        event.setCancelled(moveContext.isCancelled());
    }
}
