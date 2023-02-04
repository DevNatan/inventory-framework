package me.devnatan.inventoryframework.pipeline;

import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.Stack;
import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.exception.ContainerException;
import me.devnatan.inventoryframework.exception.SlotFillExceededException;
import me.devnatan.inventoryframework.exception.UnresolvedLayoutException;
import me.saiintbrisson.minecraft.AbstractView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

/**
 * Renders items defined by {@link VirtualView#availableSlot()}.
 */
public class AvailableSlotRenderInterceptor implements PipelineInterceptor<IFContext> {

    @TestOnly
    boolean isSuppressContainerException() {
        return false;
    }

    @Override
    public void intercept(PipelineContext<IFContext> pipeline, IFContext context) {
        final RootView root = context.getRoot();

        // just render reserved items since root layout is always resolved on init
        if (root.isLayoutSignatureChecked())
			renderReservedItems(root, context, root.getLayoutItemsLayer(), true);

        final String[] contextLayout = context.getLayout();

        if (contextLayout != null) {
            if (!context.isLayoutSignatureChecked())
                throw new UnresolvedLayoutException("Context layout must be resolved before render", null);

            renderReservedItems(root, context, context.getLayoutItemsLayer(), false);
        } else {
            // inherits the layout items layer from root if the layout was inherited
            renderReservedItems(root, context, root.getLayoutItemsLayer(), false);
        }
    }

    private void renderReservedItems(
            @NotNull AbstractView root,
            @NotNull IFContext context,
            Stack<Integer> layoutItemsLayer,
            boolean targetingRoot) {
        final Deque<IFItem> reservedItems = !targetingRoot ? context.getReservedItems() : root.getReservedItems();

        // skip if reserved items defined by auto-slot-filling was already consumed
        if (reservedItems == null || reservedItems.isEmpty()) return;

        // we force the use of the items layer of the layout here if the context has a valid layout
        // because the context ALWAYS takes priority over the regular view, so in cases where there
        // is layout in both the regular view and the context, we use the context layout to render
        // the items defined in the regular view constructor or rendering function.
        if (targetingRoot && context.isLayoutSignatureChecked()) layoutItemsLayer = context.getLayoutItemsLayer();

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
        if (!targetingRoot) context.setReservedItemsCount(reservedItems.size());

        int offset = 0;

        // offset value is used when there is no layout in cases where there are reserved items to
        // be rendered in both regular view and context, when user set in render function and/or
        // constructor, do the same layout logic and push items remainders, so they don't overwrite.
        final boolean hasAnyAvailableLayout = root.isLayoutSignatureChecked() || context.isLayoutSignatureChecked();
        if (!targetingRoot && !hasAnyAvailableLayout) offset = root.getReservedItemsCount();

        for (int i = offset; i < reservedItemsCount; i++) {
            final int targetSlot;
            try {
                targetSlot = layoutItemsLayer.elementAt(i);
            } catch (final ArrayIndexOutOfBoundsException e) {
                final IFItem target = reservedItems.peekFirst();
                final String item = target == null ? "null" : String.valueOf(target.getItem());

                throw new SlotFillExceededException("No more slots available on layout to accommodate " + item, e);
            }

            final IFItem next;

            try {
                // remove first to preserve insertion order
                next = reservedItems.removeFirst();
            } catch (final NoSuchElementException ignored) {
                break;
            }

            // register item on root view since dynamically rendered items are not registered on init,
            // so we need to register then on the first time that it's get rendered
            if (targetingRoot) root.apply(next, targetSlot);
            else {
                context.apply(next, targetSlot);
            }

            try {
                root.render(context, next, targetSlot);
            } catch (ContainerException e) {
                if (!isSuppressContainerException()) throw e;
            }
        }
    }
}
