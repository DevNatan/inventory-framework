package me.saiintbrisson.minecraft.pipeline.interceptors;

import static me.saiintbrisson.minecraft.PaginatedVirtualView.NAVIGATE_LEFT;
import static me.saiintbrisson.minecraft.PaginatedVirtualView.NAVIGATE_RIGHT;
import static me.saiintbrisson.minecraft.VirtualView.LAYOUT_EMPTY_SLOT;
import static me.saiintbrisson.minecraft.VirtualView.LAYOUT_FILLED_SLOT;
import static me.saiintbrisson.minecraft.VirtualView.LAYOUT_NEXT_PAGE;
import static me.saiintbrisson.minecraft.VirtualView.LAYOUT_PREVIOUS_PAGE;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Function;
import me.saiintbrisson.minecraft.AbstractPaginatedView;
import me.saiintbrisson.minecraft.AbstractView;
import me.saiintbrisson.minecraft.LayoutPattern;
import me.saiintbrisson.minecraft.PaginatedViewContext;
import me.saiintbrisson.minecraft.PaginatedVirtualView;
import me.saiintbrisson.minecraft.Paginator;
import me.saiintbrisson.minecraft.PlatformViewFrame;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.ViewItem;
import me.saiintbrisson.minecraft.VirtualView;
import me.saiintbrisson.minecraft.pipeline.PipelineContext;
import me.saiintbrisson.minecraft.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called during the initialization phase of a view to pre-resolve its layout if defined, or called
 * during the render phase to resolve the layout of a context.
 * <p>
 * Initial resolution is handled by {@link #handleInitialResolution(AbstractView)}.
 * Context-scope resolution is handled by {@link #resolveLayout(VirtualView, ViewContext, String[])}.
 */
public final class LayoutResolutionInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, VirtualView subject) {
        if (subject.isLayoutSignatureChecked()) return;

        // layout resolution interceptor is only called to regular views on init
        if (subject instanceof AbstractView) {
            handleInitialResolution((AbstractView) subject);
            return;
        }

        final ViewContext context = (ViewContext) subject;
        final String[] layout = context.getLayout();
        if (layout == null) return;

        resolveLayout(context, context, layout);
    }

    /**
     * Resolves and "render" layout on view init to optimize auto-slot-filling items render.
     * <p>
     * This function sets the initial count of reserved items from the root view, defined by the user
     * by slot autofill, to be used by the context later as the starting position for rendering.
     *
     * @param view The view.
     */
    private void handleInitialResolution(@NotNull AbstractView view) {
        view.ensureNotInitialized();

        final String[] layout = view.getLayout();
        if (layout == null) return;

        // preserve reserved items count to be used later on context render
        view.setReservedItemsCount(
                view.getReservedItems() == null ? 0 : view.getReservedItems().size());

        resolveLayout(view, null, layout);
    }

    /**
     * Determines the number of rows for the specified view.
     * <p>
     * If the view is a context it uses the number of rows of the {@link ViewContext#getContainer() context's container},
     * if it is a regular view it uses the number of rows of the {@link AbstractView#getType() view's type}.
     *
     * @param view The view.
     * @return The columns count for the given view.
     * @throws IllegalStateException If it is not possible to determine the number of rows for
     *                               the specified view implementation.
     */
    private static int determineRowsCount(@NotNull VirtualView view) {
        if (view instanceof ViewContext)
            return ((ViewContext) view).getContainer().getRowsCount();
        if (view instanceof AbstractView) return view.getRows();

        throw new IllegalStateException(String.format(
                "Unsupported view implementation, cannot determine rows count: %s",
                view.getClass().getName()));
    }

    /**
     * Determines the number of columns for the specified view.
     * <p>
     * If the view is a context it uses the number of columns of the {@link ViewContext#getContainer() context's container},
     * if it is a regular view it uses the number of columns of the {@link AbstractView#getType() view's type}.
     *
     * @param view The view.
     * @return The columns count for the given view.
     * @throws IllegalStateException If it is not possible to determine the number of columns for
     *                               the specified view implementation.
     */
    private static int determineColumnsCount(@NotNull VirtualView view) {
        if (view instanceof ViewContext)
            return ((ViewContext) view).getContainer().getColumnsCount();
        if (view instanceof AbstractView) return view.getColumns();

        throw new IllegalStateException(String.format(
                "Unsupported view implementation, cannot determine columns count: %s",
                view.getClass().getName()));
    }

    /**
     * Resolves the given layout to the given view.
     * <p>
     * Reads the specified layout, checks if it is within the view size constraints, it defines the
     * layout items layer and determines the page size if the specified view is paginated.
     * <p>
     * This function can only be called once per view or in case of dynamic layout update.
     *
     * @param view    The target view.
     * @param context The resolution context.
     * @param layout  The layout to be resolved.
     * @throws IllegalStateException     If during resolution a page navigation item is found for a
     *                                   view that is not paginated.
     * @throws IllegalArgumentException  If an invalid character is found in the layout.
     * @throws IndexOutOfBoundsException If the layout doesn't fit the view's container constraints.
     */
    private void resolveLayout(@NotNull VirtualView view, @Nullable ViewContext context, String[] layout) {
        final int rows = layout.length;
        final int containerRowsCount = determineRowsCount(view);

        if (rows != containerRowsCount)
            throw new IndexOutOfBoundsException(String.format(
                    "Layout columns must respect the rows count of the container" + " (given: %d, expect: %d)",
                    rows, containerRowsCount));

        final int containerColumnsCount = determineColumnsCount(view);
        final Stack<Integer> itemsLayer = new Stack<>();

        for (int row = 0; row < rows; row++) {
            final String layer = layout[row];

            final int layerLength = layer.length();
            if (layerLength != containerColumnsCount)
                throw new IndexOutOfBoundsException(String.format(
                        "Layout layer length located at %d must respect the columns count of the"
                                + " container (given: %d, expect: %d).",
                        row, layerLength, containerColumnsCount));

            for (int column = 0; column < containerColumnsCount; column++) {
                final int targetSlot = column + (row * containerColumnsCount);
                final char character = layer.charAt(column);
                switch (character) {
                    case LAYOUT_EMPTY_SLOT:
                        break;
                    case LAYOUT_FILLED_SLOT: {
                        itemsLayer.push(targetSlot);
                        break;
                    }
                    case LAYOUT_PREVIOUS_PAGE: {
                        resolveAndApplyNavigationItem(view, context, NAVIGATE_LEFT, targetSlot);
                        break;
                    }
                    case LAYOUT_NEXT_PAGE: {
                        resolveAndApplyNavigationItem(view, context, NAVIGATE_RIGHT, targetSlot);
                        break;
                    }
                    default: {
                        final LayoutPattern pattern = getLayoutOrNull(view, context, character);

                        // TODO keep this message as warning but remove this exception since
                        //      user can provide context-specific layout pattern with root layout
                        if (pattern == null) {
                            throw new IllegalArgumentException(String.format(
                                    "An unknown character \"" + character
                                            + "\" was found in layout on line %d column %d. Only %s characters are valid. "
                                            + "Any other character is considered a custom user pattern and must be registered with #setLayout(Character, Supplier<ViewItem>)",
                                    row,
                                    column,
                                    Arrays.asList(
                                            LAYOUT_EMPTY_SLOT,
                                            LAYOUT_FILLED_SLOT,
                                            LAYOUT_PREVIOUS_PAGE,
                                            LAYOUT_NEXT_PAGE)));
                        }

                        pattern.getSlots().push(targetSlot);
                    }
                }
            }
        }

        view.setLayoutItemsLayer(itemsLayer);
        view.setLayoutSignatureChecked(true);

        // this part down is usually when rendering a context
        if (!(view instanceof PaginatedVirtualView)) return;

        final Paginator<?> paginator = view.paginated().getPaginator();
        if (paginator == null) return;

        paginator.setPageSize(itemsLayer.size());
    }

    /**
     * Resolves and registers (add in context items) the navigation item for the view and sets the
     * specified slot as the navigation item.
     *
     * @param view      The target view.
     * @param context   The resolution context.
     * @param direction The navigation direction.
     * @param slot      The target slot.
     * @throws IllegalStateException If view or context is not paginated.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void resolveAndApplyNavigationItem(
            @NotNull VirtualView view, @Nullable ViewContext context, int direction, int slot) {
        // can be null on regular view layout pre render (on initialization)
        if (context != null) {
			if (!view.isPaginated() || !context.isPaginated())
				throw new IllegalStateException(String.format(
					"Navigation characters (%s) on layout are reserved to paginated views and cannot be used on regular views.",
					LAYOUT_PREVIOUS_PAGE + ", " + LAYOUT_NEXT_PAGE));

			final AbstractPaginatedView root = view instanceof ViewContext
				? ((PaginatedViewContext<?>) view).getRoot()
				: (AbstractPaginatedView<?>) view;

			getInternalNavigationItemWithFallback(root, (PaginatedViewContext) context, direction);
		}

        final PaginatedVirtualView paginatedView = view.paginated();
        if (direction == NAVIGATE_LEFT) paginatedView.setPreviousPageItemSlot(slot);
        else paginatedView.setNextPageItemSlot(slot);
    }

    /**
     * Gets the navigation item based on the functions of the specified view's navigation item
     * factories, from the context root if the view is a context, or from the ViewFrame's default
     * navigation item factory if accessible.
     *
     * @param view      The target view.
     * @param context   The resolution context.
     * @param direction The navigation direction.
     * @param <T>       The pagination data type.
     * @return The item that will be used for navigation.
     */
    private <T> ViewItem getInternalNavigationItemWithFallback(
            @NotNull AbstractPaginatedView<T> view, @NotNull PaginatedViewContext<T> context, int direction) {
        final ViewItem item = getInternalNavigationItem(view, context, direction);
        if (item != null) return item;

        final PlatformViewFrame<?, ?, ?> vf = view.getViewFrame();
        if (vf == null) return null;

        final Function<PaginatedViewContext<?>, ViewItem> fallback =
                direction == NAVIGATE_LEFT ? vf.getDefaultPreviousPageItem() : vf.getDefaultNextPageItem();

        if (fallback == null) return null;

        return fallback.apply(context);
    }

    /**
     * Gets the navigation item based on the navigation item factory of the view or the view root
     * if it is a context.
     *
     * @param view      The target view.
     * @param context   The resolution context.
     * @param direction The navigation direction.
     * @param <T>       The pagination data type.
     * @return The item that will be used for navigation.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> ViewItem getInternalNavigationItem(
            @NotNull PaginatedVirtualView<T> view, @NotNull PaginatedViewContext<T> context, int direction) {
        final boolean isBackwards = direction == NAVIGATE_LEFT;
        final BiConsumer<PaginatedViewContext<T>, ViewItem> factory =
                isBackwards ? view.getPreviousPageItemFactory() : view.getNextPageItemFactory();

        if (factory == null) {
            if (view instanceof AbstractPaginatedView)
                return isBackwards
                        ? ((AbstractPaginatedView) view).getPreviousPageItem(context)
                        : ((AbstractPaginatedView) view).getNextPageItem(context);

            return isBackwards
                    ? ((PaginatedViewContext) view).getRoot().getPreviousPageItem(context)
                    : ((PaginatedViewContext) view).getRoot().getNextPageItem(context);
        }

        final ViewItem item = new ViewItem();
        factory.accept(context, item);
        return item;
    }

    /**
     * Finds the layout pattern to the given character.
     *
     * @param view      The target view.
     * @param context   The resolution context.
     * @param character The layout pattern character.
     * @return The layout pattern to the given character or <code>null</code>.
     */
    private LayoutPattern getLayoutOrNull(VirtualView view, ViewContext context, char character) {
        List<LayoutPattern> layoutPatternList = null;
        if (context != null) layoutPatternList = context.getLayoutPatterns();

        if (layoutPatternList == null || layoutPatternList.isEmpty())
            layoutPatternList = (context == null ? view : context.getRoot()).getLayoutPatterns();

        return layoutPatternList.stream()
                .filter(pattern -> pattern.getCharacter() == character)
                .findFirst()
                .orElse(null);
    }
}
