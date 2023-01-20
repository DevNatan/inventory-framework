package me.devnatan.inventoryframework.pipeline;

import static me.devnatan.inventoryframework.VirtualView.LAYOUT_NEXT_PAGE;
import static me.devnatan.inventoryframework.VirtualView.LAYOUT_PREVIOUS_PAGE;
import static me.saiintbrisson.minecraft.PaginatedVirtualView.NAVIGATE_LEFT;
import static me.saiintbrisson.minecraft.PaginatedVirtualView.NAVIGATE_RIGHT;
import static me.devnatan.inventoryframework.IFItem.UNSET;

import java.util.function.BiConsumer;
import java.util.function.Function;
import me.devnatan.inventoryframework.context.IFContext;
import me.saiintbrisson.minecraft.AbstractPaginatedView;
import me.saiintbrisson.minecraft.PaginatedVirtualView;
import me.saiintbrisson.minecraft.PlatformViewFrame;
import me.devnatan.inventoryframework.IFItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NavigationControllerInterceptor implements PipelineInterceptor<IFContext> {

    @Override
    public void intercept(@NotNull PipelineContext<IFContext> pipeline, IFContext context) {
        final IFPaginatedContext<?> paginatedContext = context.paginated();
        updateNavigationItem(paginatedContext, PaginatedVirtualView.NAVIGATE_LEFT);
        updateNavigationItem(paginatedContext, PaginatedVirtualView.NAVIGATE_RIGHT);
    }

    /**
     * Returns the position of the navigation item for the specified direction.
     * <p>
     * The item slot is defined during {@link LayoutResolutionInterceptor layout resolution}.
     *
     * @param context   The pagination context.
     * @param direction The navigation direction.
     * @return Current navigation (of the specified direction) item slot.
     */
    private int getNavigationItemSlot(@NotNull IFPaginatedContext<?> context, int direction) {
        PaginatedVirtualView<?> target = context.isLayoutSignatureChecked() ? context : context.getRoot();

        return direction == PaginatedVirtualView.NAVIGATE_LEFT ? target.getPreviousPageItemSlot() : target.getNextPageItemSlot();
    }

    @Nullable
    private <T> IFItem createNavigationItemFromRoot(@NotNull IFPaginatedContext<T> context, int direction) {
        final AbstractPaginatedView<T> root = context.getRoot();
        final BiConsumer<IFPaginatedContext<T>, IFItem> factory =
                direction == PaginatedVirtualView.NAVIGATE_LEFT ? root.getPreviousPageItemFactory() : root.getNextPageItemFactory();

        // TODO just a compatibility guarantee must be removed in v2.5.5
        if (factory == null) {
            @SuppressWarnings("deprecation")
            final IFItem result =
                    direction == PaginatedVirtualView.NAVIGATE_LEFT ? root.getPreviousPageItem(context) : root.getNextPageItem(context);

            return result;
        }

        final IFItem item = new IFItem();
        item.setNavigationItem(true);
        factory.accept(context, item);
        item.setNavigationItem(false);
        return item;
    }

    private <T> IFItem resolveNavigationItem(@NotNull IFPaginatedContext<T> context, int direction) {
        final IFItem item = createNavigationItemFromRoot(context, direction);

        // TODO can be null due to compatibility guarantee
        if (item != null) return item;

        final PlatformViewFrame<?, ?, ?> vf = context.getRoot().getViewFrame();
        if (vf == null) return null;

        @SuppressWarnings("deprecation")
        final Function<IFPaginatedContext<?>, IFItem> fallback =
                direction == PaginatedVirtualView.NAVIGATE_LEFT ? vf.getDefaultPreviousPageItem() : vf.getDefaultNextPageItem();

        if (fallback == null) return null;

        return fallback.apply(context);
    }

    private <T> void updateNavigationItem(@NotNull IFPaginatedContext<T> context, int direction) {
        IFItem item = resolveNavigationItem(context, direction);
        final int layoutSlot = getNavigationItemSlot(context, direction);
        checkUndefinedLayoutNavigationItem(item, layoutSlot, direction);

        int targetSlot = layoutSlot;

        if (item != null) {
            checkIndeterministicNavigationSlot(layoutSlot, item.getSlot());
            checkAmbiguousNavigationSlot(layoutSlot, item.getSlot());

            // prioritize layout
            targetSlot = layoutSlot == UNSET ? item.getSlot() : layoutSlot;
        }

        if (targetSlot == UNSET) return;

        // GH-187 this check allow users to omit navigation item factory to remove it
        final boolean notAvailable = item == null
                || (item.getItem() == null && item.getRenderHandler() == null && item.getUpdateHandler() == null);

        // ensure item is removed if it was resolved and set before and is not anymore
        if (notAvailable) {
            removeAt(context, targetSlot);
            return;
        }

        // the click handler should be checked for cases where the user has defined the navigation
        // item manually, so we will not override his handler
        if (item.getClickHandler() == null) {
            item.onClick(click -> {
                if (direction == PaginatedVirtualView.NAVIGATE_LEFT) click.paginated().switchToPreviousPage();
                else click.paginated().switchToNextPage();
            });
        }

        renderItemAndApplyOnContext(context, item.withCancelOnClick(true), targetSlot);
    }

    private void removeAt(@NotNull IFContext context, int slot) {
        context.clear(slot);
        context.getContainer().removeItem(slot);
    }

    private void renderItemAndApplyOnContext(@NotNull IFContext context, IFItem item, int slot) {
        context.apply(item, slot);
        context.getRoot().render(context, item, slot);
    }

    private void checkIndeterministicNavigationSlot(int layoutSlot, int itemSlot) {
        if (layoutSlot != UNSET || itemSlot != UNSET) return;

        throw new IllegalStateException(String.format(
                "Navigation controller items have been defined but"
                        + " their slots are indeterministic (unset everywhere), it is necessary"
                        + " that the position of these items be defined in "
                        + "the layout using the navigation reserved "
                        + "characters (%s and %s) or in the items themselves with #withSlot.",
                LAYOUT_PREVIOUS_PAGE, LAYOUT_NEXT_PAGE));
    }

    private void checkAmbiguousNavigationSlot(int layoutSlot, int itemSlot) {
        if (layoutSlot == UNSET || itemSlot == UNSET) return;

        throw new IllegalStateException(String.format(
                "More than one navigation item position has been defined, "
                        + "it is not allowed to use both definition in layout (at %d) and "
                        + "manual definition of navigation item slot (at %d) together, choose only one.",
                layoutSlot, itemSlot));
    }

    private void checkUndefinedLayoutNavigationItem(IFItem item, int layoutSlot, int direction) {
        if (layoutSlot != UNSET && item == null) {
            final String label = direction == PaginatedVirtualView.NAVIGATE_LEFT
                    ? "left (" + LAYOUT_PREVIOUS_PAGE + ")"
                    : "right (" + LAYOUT_NEXT_PAGE + ")";

            throw new IllegalStateException(String.format(
                    "Navigation item for the direction \"%s\" was defined in the layout at slot %d but"
                            + " we could not find it. There must be a navigation item defined. See more: %s",
                    label, layoutSlot, "https://github.com/DevNatan/inventory-framework/wiki/Pagination#navigation"));
        }
    }
}
