package me.saiintbrisson.minecraft.pipeline.interceptors;

import static me.saiintbrisson.minecraft.PaginatedVirtualView.NAVIGATE_LEFT;
import static me.saiintbrisson.minecraft.PaginatedVirtualView.NAVIGATE_RIGHT;
import static me.saiintbrisson.minecraft.ViewItem.UNSET;
import static me.saiintbrisson.minecraft.VirtualView.LAYOUT_NEXT_PAGE;
import static me.saiintbrisson.minecraft.VirtualView.LAYOUT_PREVIOUS_PAGE;

import java.util.function.BiConsumer;
import java.util.function.Function;
import me.saiintbrisson.minecraft.AbstractPaginatedView;
import me.saiintbrisson.minecraft.PaginatedViewContext;
import me.saiintbrisson.minecraft.PaginatedVirtualView;
import me.saiintbrisson.minecraft.PlatformViewFrame;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.ViewItem;
import me.saiintbrisson.minecraft.pipeline.PipelineContext;
import me.saiintbrisson.minecraft.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NavigationControllerInterceptor implements PipelineInterceptor<ViewContext> {

    @Override
    public void intercept(@NotNull PipelineContext<ViewContext> pipeline, ViewContext context) {
        final PaginatedViewContext<?> paginatedContext = context.paginated();
        updateNavigationItem(paginatedContext, NAVIGATE_LEFT);
        updateNavigationItem(paginatedContext, NAVIGATE_RIGHT);
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
    private int getNavigationItemSlot(@NotNull PaginatedViewContext<?> context, int direction) {
        PaginatedVirtualView<?> target = context.isLayoutSignatureChecked() ? context : context.getRoot();

        return direction == NAVIGATE_LEFT ? target.getPreviousPageItemSlot() : target.getNextPageItemSlot();
    }

    @Nullable
    private <T> ViewItem createNavigationItemFromRoot(@NotNull PaginatedViewContext<T> context, int direction) {
        final AbstractPaginatedView<T> root = context.getRoot();
        final BiConsumer<PaginatedViewContext<T>, ViewItem> factory =
                direction == NAVIGATE_LEFT ? root.getPreviousPageItemFactory() : root.getNextPageItemFactory();

        // TODO just a compatibility guarantee must be removed in v2.5.5
        if (factory == null) {
            @SuppressWarnings("deprecation")
            final ViewItem result =
                    direction == NAVIGATE_LEFT ? root.getPreviousPageItem(context) : root.getNextPageItem(context);

            return result;
        }

        final ViewItem item = new ViewItem();
        item.setNavigationItem(true);
        factory.accept(context, item);
        item.setNavigationItem(false);
        return item;
    }

    private <T> ViewItem resolveNavigationItem(@NotNull PaginatedViewContext<T> context, int direction) {
        final ViewItem item = createNavigationItemFromRoot(context, direction);

        // TODO can be null due to compatibility guarantee
        if (item != null) return item;

        final PlatformViewFrame<?, ?, ?> vf = context.getRoot().getViewFrame();
        if (vf == null) return null;

        @SuppressWarnings("deprecation")
        final Function<PaginatedViewContext<?>, ViewItem> fallback =
                direction == NAVIGATE_LEFT ? vf.getDefaultPreviousPageItem() : vf.getDefaultNextPageItem();

        if (fallback == null) return null;

        return fallback.apply(context);
    }

    private <T> void updateNavigationItem(@NotNull PaginatedViewContext<T> context, int direction) {
        ViewItem item = resolveNavigationItem(context, direction);
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
                if (direction == NAVIGATE_LEFT) click.paginated().switchToPreviousPage();
                else click.paginated().switchToNextPage();
            });
        }

        renderItemAndApplyOnContext(context, item.withCancelOnClick(true), targetSlot);
    }

    private void removeAt(@NotNull ViewContext context, int slot) {
        context.clear(slot);
        context.getContainer().removeItem(slot);
    }

    private void renderItemAndApplyOnContext(@NotNull ViewContext context, ViewItem item, int slot) {
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

    private void checkUndefinedLayoutNavigationItem(ViewItem item, int layoutSlot, int direction) {
        if (layoutSlot != UNSET && item == null) {
            final String label = direction == NAVIGATE_LEFT
                    ? "left (" + LAYOUT_PREVIOUS_PAGE + ")"
                    : "right (" + LAYOUT_NEXT_PAGE + ")";

            throw new IllegalStateException(String.format(
                    "Navigation item for the direction \"%s\" was defined in the layout at slot %d but"
                            + " we could not find it. There must be a navigation item defined. See more: %s",
                    label, layoutSlot, "https://github.com/DevNatan/inventory-framework/wiki/Pagination#navigation"));
        }
    }
}
