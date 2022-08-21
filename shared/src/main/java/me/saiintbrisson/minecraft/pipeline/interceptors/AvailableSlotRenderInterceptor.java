package me.saiintbrisson.minecraft.pipeline.interceptors;

import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.Stack;
import me.saiintbrisson.minecraft.AbstractView;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.ViewItem;
import me.saiintbrisson.minecraft.VirtualView;
import me.saiintbrisson.minecraft.exception.SlotFillExceededException;
import me.saiintbrisson.minecraft.exception.UnresolvedLayoutException;
import me.saiintbrisson.minecraft.pipeline.PipelineContext;
import me.saiintbrisson.minecraft.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;

/**
 * Renders items defined by {@link VirtualView#availableSlot()}.
 */
public final class AvailableSlotRenderInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, VirtualView view) {
        // layout cannot be rendered without a context (no container available)
        if (!(view instanceof ViewContext)) return;

        final ViewContext context = (ViewContext) view;
        final AbstractView root = context.getRoot();

        // just render reserved items since root layout is always resolved on init
        if (root.isLayoutSignatureChecked()) renderReservedItems(root, context, root.getLayoutItemsLayer(), true);

        final String[] contextLayout = context.getLayout();
        if (contextLayout != null) {
            if (!context.isLayoutSignatureChecked())
                throw new UnresolvedLayoutException("Context layout must be resolved before render", null);

            // inherits the layout items layer from root if the layout was inherited
            renderReservedItems(root, context, context.getLayoutItemsLayer(), false);
        } else {
            renderReservedItems(root, context, root.getLayoutItemsLayer(), false);
        }
    }

    private void renderReservedItems(
            @NotNull AbstractView root,
            @NotNull ViewContext context,
            Stack<Integer> layoutItemsLayer,
            boolean targetingRoot) {
        final Deque<ViewItem> reservedItems = !targetingRoot
			? context.getReservedItems()
			: root.getReservedItems();

        // skip if reserved items defined by auto-slot-filling was already consumed
        if (reservedItems == null || reservedItems.isEmpty()) return;

        System.out.println("render reserved items");

        // we force the use of the items layer of the layout here if the context has a valid layout
        // because the context ALWAYS takes priority over the regular view, so in cases where there
        // is layout in both the regular view and the context, we use the context layout to render
        // the items defined in the regular view constructor or rendering function.
        if (targetingRoot && context.isLayoutSignatureChecked())
			layoutItemsLayer = context.getLayoutItemsLayer();

        final boolean wasRootReservedItemsRendered =
                !targetingRoot && root.isLayoutSignatureChecked() && root.getReservedItems() != null;

        // what we are going to do here is, if the root has already been rendered, that means that
        // the initial slots of the layout have already been filled, so the items defined for
        // automatic slot definition from the context cannot overlap these items, we will use the
        // root layout layer to push the next items out of context to the next available slots
        if (wasRootReservedItemsRendered) {
            // inherits the layout items layer too from root if the layout was inherited
            layoutItemsLayer = new Stack<>();
            layoutItemsLayer.addAll(
                    !context.isLayoutSignatureChecked() ? root.getLayoutItemsLayer() : context.getLayoutItemsLayer());

            // drops all items that have been rendered before as these slots have already been filled,
            // the next slots will be used to render the items in context
            int dropCount = root.getReservedItemsCount();
            while (dropCount > 0) {
                layoutItemsLayer.removeElementAt(0);
                dropCount--;
            }
        }

        final int reservedItemsCount = reservedItems.size();

		// allow context reserved items count be accessed by other interceptors since
		// reserved items queue will be cleared after get rendered
		if (!targetingRoot)
			context.setReservedItemsCount(reservedItems.size());

        int offset = 0;

        // offset value is used when there is no layout in cases where there are reserved items to
        // be rendered in both regular view and context, when user set in render function and/or
        // constructor, do the same layout logic and push items remainders, so they don't overwrite.
        final boolean hasAnyAvailableLayout = root.isLayoutSignatureChecked() || context.isLayoutSignatureChecked();
        if (!targetingRoot && !hasAnyAvailableLayout)
			offset = root.getReservedItemsCount();

        for (int i = offset; i < reservedItemsCount; i++) {
            final int targetSlot;
            try {
                targetSlot = layoutItemsLayer.elementAt(i);
            } catch (final ArrayIndexOutOfBoundsException e) {
                final ViewItem target = reservedItems.peekFirst();
                final String item = target == null ? "null" : String.valueOf(target.getItem());

                throw new SlotFillExceededException("No more slots available on layout to accommodate " + item, e);
            }

            final ViewItem next;

            try {
                // remove first to preserve insertion order
                next = reservedItems.removeFirst();
            } catch (final NoSuchElementException ignored) {
                break;
            }

            // register item on root view since dynamically rendered items are not registered on init,
            // so we need to register then on the first time that it's get rendered
            if (targetingRoot) root.apply(next, targetSlot);
            else context.apply(next, targetSlot);

            root.render(context, next, targetSlot);
        }
    }
}
