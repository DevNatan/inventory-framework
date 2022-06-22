package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Getter
@ToString(callSuper = true)
public abstract class AbstractPaginatedView<T> extends AbstractView
        implements PaginatedVirtualView<T> {

    static final byte NAVIGATE_LEFT = 0, NAVIGATE_RIGHT = 1;

    private final List<LayoutPattern> layoutPatterns = new ArrayList<>();
    private BiConsumer<PaginatedViewContext<T>, ViewItem> previousPageItemFactory,
            nextPageItemFactory;
    private int offset, limit;
    private String[] layout;
    private Paginator<T> paginator;

    AbstractPaginatedView(int rows, String title, @NotNull ViewType type) {
        super(rows, title, type);
        this.offset = 0;
        this.limit = getItems().length - 1;
    }

    /**
     * Called when a single pagination data is about to be rendered in the container.
     *
     * <p>The {@link ViewItem item parameter} is mutable and must be used to determine what will be
     * rendered in that context for that specific item.
     *
     * <pre><code>
     * &#64;Override
     * protected void onItemRender(
     *     PaginatedViewSlotContext&#60;T&#62; context,
     *     ViewItem viewItem,
     *     T value
     * ) {
     *     viewItem.withItem(platformStack).onClick(click -&#62; {
     *         // clicked on value
     *     });
     * }
     * </code></pre>
     *
     * This function is called extensively, every time a paginated item is rendered or updated.
     *
     * <p>It is not allowed to call methods that {@link #inventoryModificationTriggered() trigger
     * modifications in the container} of the context or in the view within this rendering function,
     * and it is also not possible to use the item {@link ViewItem#onRender(ViewItemHandler) render}
     * and {@link ViewItem#onUpdate(ViewItemHandler) update} functions within this function since it
     * is already a rendering function of item itself.
     *
     * @param context The pagination item rendering context.
     * @param viewItem A mutable instance of item that will be rendered.
     * @param value The paginated value.
     */
    protected abstract void onItemRender(
            @NotNull PaginatedViewSlotContext<T> context,
            @NotNull ViewItem viewItem,
            @NotNull T value);

    /**
     * Called when pagination is switched.
     *
     * <p>The context in the parameter is the new paging context, so trying to {@link
     * PaginatedViewContext#getPage() get the current page} will return the new page.
     *
     * @param context The page switch context.
     */
    protected void onPageSwitch(@NotNull PaginatedViewContext<T> context) {}

    @ApiStatus.Internal
    public final Paginator<T> getPaginator() {
        return paginator;
    }

    public BiConsumer<PaginatedViewContext<T>, ViewItem> getPreviousPageItemFactory() {
        return previousPageItemFactory;
    }

    public BiConsumer<PaginatedViewContext<T>, ViewItem> getNextPageItemFactory() {
        return nextPageItemFactory;
    }

    /** @deprecated Offset and limit will be replaced by layout. */
    @Deprecated
    public final int getOffset() {
        return offset;
    }

    /**
     * Defines the slot that pagination will start in the container.
     *
     * @deprecated Offset and limit will be replaced by layout. Use {@link #setLayout(String...)}
     *     instead.
     */
    @Deprecated
    public final void setOffset(int offset) {
        ensureNotInitialized();
        this.offset = offset;
    }

    /** @deprecated Offset and limit will be replaced by layout. */
    @Deprecated
    public final int getLimit() {
        return limit;
    }

    /**
     * Defines the last slot that pagination can reach in the container.
     *
     * @deprecated Offset and limit will be replaced by layout. Use {@link #setLayout(String...)}
     *     instead.
     */
    @Deprecated
    public final void setLimit(int limit) {
        ensureNotInitialized();
        this.limit = limit;
    }

    @ApiStatus.Internal
    public final String[] getLayout() {
        return layout;
    }

    @Override
    public final void setLayout(@Nullable String... layout) {
        ensureNotInitialized();
        this.layout = layout;
    }

    public final void setLayout(char character, Supplier<ViewItem> factory) {
        ensureNotInitialized();
        if (character == EMPTY_SLOT_CHAR
                || character == ITEM_SLOT_CHAR
                || character == NEXT_PAGE_CHAR
                || character == PREVIOUS_PAGE_CHAR)
            throw new IllegalArgumentException(
                    String.format(
                            "The \"%c\" character is reserved in layouts"
                                    + " and cannot be used due to backwards compatibility.",
                            character));

        layoutPatterns.add(new LayoutPattern(character, factory));
    }

    public final void setLayout(char identifier, @NotNull Consumer<ViewItem> layout) {
        setLayout(
                identifier,
                () -> {
                    final ViewItem item = new ViewItem();
                    layout.accept(item);
                    return item;
                });
    }

    /** @deprecated Use {@link #setPreviousPageItem(BiConsumer)} on constructor instead. */
    @Deprecated
    protected ViewItem getPreviousPageItem(@NotNull PaginatedViewContext<T> context) {
        return null;
    }

    public final void setPreviousPageItem(
            @NotNull BiConsumer<PaginatedViewContext<T>, ViewItem> previousPageItemFactory) {
        ensureNotInitialized();
        this.previousPageItemFactory = previousPageItemFactory;
    }

    /** @deprecated Use {@link #setNextPageItem(BiConsumer)} on constructor instead. */
    @Deprecated
    protected ViewItem getNextPageItem(@NotNull PaginatedViewContext<T> context) {
        return null;
    }

    public final void setNextPageItem(
            @NotNull BiConsumer<PaginatedViewContext<T>, ViewItem> nextPageItemFactory) {
        ensureNotInitialized();
        this.nextPageItemFactory = nextPageItemFactory;
    }

    public final void setSource(@NotNull List<T> source) {
        ensureNotInitialized();
        this.paginator = new Paginator<>(getPageSize(), source);
    }

    @Override
    @ApiStatus.Experimental
    public final void setSource(
            @NotNull Function<PaginatedViewContext<T>, List<T>> sourceProvider) {
        ensureNotInitialized();
        this.paginator = new Paginator<>(getPageSize(), sourceProvider);
    }

    @Override
    public final int getPageMaxItemsCount() {
        throw new IllegalStateException("Cannot retrieve page max items count without a container");
    }

    @Override
    public final int getPageSize() {
        return limit - offset;
    }

    private List<LayoutPattern> getLayoutPatterns() {
        return layoutPatterns;
    }

    @Override
    final void render(@NotNull ViewContext context) {
        if (context.getContainer().getType() != ViewType.CHEST)
            throw new IllegalStateException(
                    String.format(
                            "Pagination is not supported in \"%s\" view type: %s."
                                    + " Use chest type instead.",
                            getType().getIdentifier(), getClass().getName()));

        if (paginator == null && context.paginated().getPaginator() == null)
            throw new IllegalStateException(
                    "At least one pagination source must be set. "
                            + "Use #setSource in the PaginatedView constructor or set just to a context"
                            + " in the #onRender(...) function with \"render.paginated().setSource(...)\".");

        super.render(context);

        updateContext(context.paginated(), 0, true, true);
    }

    @Override
    void update(@NotNull ViewContext context) {
        super.update(context);

        final PaginatedViewContext<T> paginated = context.paginated();
        updateContext(
                paginated, paginated.getPage(), false /* avoid intensive page checking */, true);
    }

    final String[] useLayout(@NotNull PaginatedViewContext<T> context) {
        return context.getLayout() == null ? layout : context.getLayout();
    }

    private void renderPaginatedItemAt(
            @NotNull PaginatedViewContext<T> context,
            int index,
            int slot,
            @NotNull T value,
            @Nullable ViewItem override) {
        // TODO replace this with a more sophisticated overlay detection
        ViewItem overlay = context.resolve(slot, true);
        if (overlay != null && overlay.isPaginationItem()) overlay = null;

        // overlapping items are those that are already in the inventory but the IF is trying to
        // render
        // them,
        // if it is an overlapped item it means that during the layout cleanup it was detected that
        // they
        // should
        // not have been removed, so they are not removed and during layout rendering they are not
        // re-rendered.
        if (override == null) {
            final ViewItem item = new ViewItem(slot);
            item.setPaginationItem(true);

            @SuppressWarnings("unchecked")
            final PaginatedViewSlotContext<T> slotContext =
                    (PaginatedViewSlotContext<T>)
                            PlatformUtils.getFactory()
                                    .createSlotContext(
                                            item, (BaseViewContext) context, index, value);

            runCatching(
                    context,
                    () -> {
                        onItemRender(slotContext, item, value);
                    });
            render(slotContext, item, slot);
            item.setOverlay(overlay);
        } else {
            // we need to reset the initial rendering function of the overlaid item if not, when we
            // get to the rendering stage of the overlaid item, he overlaid item's rendering
            // function
            // will be called first and will render the wrong item
            override.setUpdateHandler(null);

            // only if there's a fallback item available, clearing it without checking will cause
            // "No item were provided and the rendering function was not defined at slot..."
            if (override.getItem() != null) override.setRenderHandler(null);

            override.setSlot(slot);
            override.setOverlay(overlay);
            ((BasePaginatedViewContext<T>) context).getItems()[slot] = override;
        }
    }

    private void renderLayout(
            @NotNull PaginatedViewContext<T> context, String[] layout, ViewItem[] preservedItems) {
        renderPatterns(context);

        final List<T> elements = context.getPaginator().getPageBlocking(context.getPage());
        final int elementsCount = elements.size();

        final Stack<Integer> itemsLayer = ((BasePaginatedViewContext<T>) context).getItemsLayer();
        final int lastSlot = layout == null ? limit : itemsLayer.peek();
        final int layerSize = getLayerSize(context, layout);

        for (int i = 0; i <= lastSlot; i++) {
            if (layout != null && i >= layerSize) break;

            final int targetSlot = layout == null ? offset + i : itemsLayer.elementAt(i);
            final ViewItem preserved =
                    preservedItems == null || preservedItems.length <= i ? null : preservedItems[i];
            if (i < elementsCount)
                renderPaginatedItemAt(context, i, targetSlot, elements.get(i), preserved);
            else {
                final ViewItem item = context.resolve(targetSlot, true);
                // check if a non-virtual item has been defined in that slot
                if (item != null) {
                    if (!item.isPaginationItem()) {
                        render(context, item, targetSlot);
                        continue;
                    }

                    final ViewItem overlay = item.getOverlay();
                    if (overlay != null) {
                        render(context, overlay, targetSlot);
                        continue;
                    }
                }

                removeAt(context, targetSlot);
            }
        }
    }

    private void renderPatterns(@NotNull PaginatedViewContext<T> context) {
        for (final LayoutPattern pattern : getLayoutPatterns()) {
            for (final int slot : pattern.getSlots()) {
                final ViewItem item = pattern.getFactory().get();

                // pattern slot must be unset
                if (item.getSlot() != -1)
                    throw new IllegalStateException(
                            String.format(
                                    "Items defined through the layout pattern's item factory cannot have a "
                                            + "pre-defined slot. Use `item()` instead of `slot(x)`. "
                                            + "Expected: *unset slot*, given: %s",
                                    item.getSlot()));

                item.setSlot(slot);
                render(context, item, slot);
            }
        }
    }

    void updateLayout(@NotNull PaginatedViewContext<T> context, String[] layout) {
        // what we will do: first, use the old defined layout to preserve the actual item slot state
        // and
        // then reorder
        // these items with the new slots of the new layout on different positions but with the same
        // preserved state
        final ViewItem[] items = clearLayout(context, useLayout(context));
        resolveLayout(context, layout, true);
        renderLayout(context, layout, items);
    }

    private ViewItem[] clearLayout(@NotNull PaginatedViewContext<T> context, String[] layout) {
        final int elementsCount = context.getPaginator().getPageBlocking(context.getPage()).size();
        final Stack<Integer> itemsLayer = ((BasePaginatedViewContext<T>) context).getItemsLayer();
        final int lastSlot = layout == null ? limit : itemsLayer.peek();
        final int layerSize = getLayerSize(context, layout);

        final ViewItem[] preservedItems = new ViewItem[Math.min(layerSize, elementsCount) + 1];
        for (int i = 0; i <= lastSlot; i++) {
            if (layout != null && i >= layerSize) break;

            final int targetSlot = layout == null ? offset + i : itemsLayer.elementAt(i);
            if (i < elementsCount) {
                final ViewItem preserved = context.getItem(targetSlot);
                preservedItems[i] = preserved;
            }

            removeAt(context, targetSlot);
        }

        return preservedItems;
    }

    void removeAt(@NotNull ViewContext context, int slot) {
        context.clear(slot);
        context.getContainer().removeItem(slot);
    }

    private int getLayerSize(@NotNull PaginatedViewContext<T> context, String[] layout) {
        return layout == null
                ? 0 /* ignored */
                : ((BasePaginatedViewContext<T>) context).getItemsLayer().size();
    }

    private ViewItem internalGetPreviousPageItem(@NotNull PaginatedViewContext<T> context) {
        final AbstractPaginatedView<T> root = context.getRoot().paginated();
        if (root.getPreviousPageItemFactory() == null) return root.getPreviousPageItem(context);

        final ViewItem item = new ViewItem();
        root.getPreviousPageItemFactory().accept(context, item);
        return item;
    }

    private ViewItem internalGetNextPageItem(@NotNull PaginatedViewContext<T> context) {
        final AbstractPaginatedView<T> root = context.getRoot().paginated();
        if (root.getNextPageItemFactory() == null) return root.getNextPageItem(context);

        final ViewItem item = new ViewItem();
        root.getNextPageItemFactory().accept(context, item);
        return item;
    }

    private ViewItem resolveNavigationItem(
            @NotNull PaginatedViewContext<T> context,
            @Range(from = NAVIGATE_LEFT, to = NAVIGATE_RIGHT) int direction) {
        final AbstractPaginatedView<T> root = context.getRoot();
        final ViewItem item =
                direction == NAVIGATE_LEFT
                        ? root.internalGetPreviousPageItem(context)
                        : root.internalGetNextPageItem(context);

        if (item != null) return item;

        final PlatformViewFrame<?, ?, ?> vf = context.getRoot().getViewFrame();
        if (vf == null) return null;

        final Function<PaginatedViewContext<?>, ViewItem> fallback =
                direction == NAVIGATE_LEFT
                        ? vf.getDefaultPreviousPageItem()
                        : vf.getDefaultNextPageItem();

        if (fallback == null) return null;

        return fallback.apply(context);
    }

    final void updateContext(
            @NotNull PaginatedViewContext<T> context,
            int page,
            boolean pageChecking,
            boolean setupForRender) {
        if (context instanceof ViewSlotContext)
            throw new IllegalStateException("Cannot update context using a slot context");

        final String[] layout = useLayout(context);
        if (pageChecking) {
            if (setupForRender && !context.getPaginator().hasPage(page)) return;

            if (layout != null && !context.isLayoutSignatureChecked())
                resolveLayout(context, layout, setupForRender);

            if (setupForRender) ((BasePaginatedViewContext<T>) context).setPage(page);
        }

        if (!setupForRender) return;

        renderLayout(context, layout, null);
        updateNavigationItem(context, NAVIGATE_LEFT);
        updateNavigationItem(context, NAVIGATE_RIGHT);
    }

    private int getNavigationItemSlot(
            @NotNull PaginatedViewContext<T> context,
            @Range(from = NAVIGATE_LEFT, to = NAVIGATE_RIGHT) int direction) {
        return direction == NAVIGATE_LEFT
                ? context.getPreviousPageItemSlot()
                : context.getNextPageItemSlot();
    }

    private void updateNavigationItem(
            @NotNull PaginatedViewContext<T> context,
            @Range(from = NAVIGATE_LEFT, to = NAVIGATE_RIGHT) int direction) {
        final AbstractPaginatedView<T> root = context.getRoot();
        int expectedSlot = getNavigationItemSlot(context, direction);
        ViewItem item = null;

        // it is recommended to use layout for pagination, so at this stage the layout may have
        // already defined the slot for the pagination items, so we check if it is not defined yet
        if (expectedSlot == -1) {
            // check if navigation item was manually set by the user
            item = root.resolveNavigationItem(context, direction);

            if (item == null || item.getSlot() == -1) return;

            expectedSlot = item.getSlot();
            if (direction == NAVIGATE_LEFT) context.setPreviousPageItemSlot(expectedSlot);
            else context.setNextPageItemSlot(expectedSlot);
        }

        if (item == null) item = root.resolveNavigationItem(context, direction);

        // ensure item is removed if it was resolved and set before and is not anymore
        if (item == null) {
            root.removeAt(context, expectedSlot);
            return;
        }

        // the click handler should be checked for cases where the user has defined the navigation
        // item manually, so we will not override his handler
        if (item.getClickHandler() == null) {
            item.onClick(
                    click -> {
                        click.getPlayer().sendMessage("clicked on navigation item to " + direction);

                        if (direction == NAVIGATE_LEFT) click.paginated().switchToPreviousPage();
                        else click.paginated().switchToNextPage();
                    });
        }

        render(context, item.withCancelOnClick(true), expectedSlot);
        ((BasePaginatedViewContext<T>) context).getItems()[expectedSlot] = item;
    }

    final void resolveLayout(
            @NotNull PaginatedViewContext<T> context, String[] layout, boolean setupForRender) {
        // since the layout is only defined once, we cache it
        // to avoid unnecessary processing every time we update the context.
        final int len = layout.length;
        final int containerRowsCount = context.getContainer().getRowsCount();

        if (len != containerRowsCount)
            throw new IllegalArgumentException(
                    String.format(
                            "Layout columns must respect the rows count of the container"
                                    + " (layout size: %d, container rows: %d)",
                            len, containerRowsCount));

        final int containerColumnsCount = context.getContainer().getColumnsCount();

        final Stack<Integer> itemsLayer = new Stack<>();
        ((BasePaginatedViewContext<T>) context).setItemsLayer(itemsLayer);

        for (int row = 0; row < len; row++) {
            final String layer = layout[row];

            final int layerLength = layer.length();
            if (layerLength != containerColumnsCount)
                throw new IllegalArgumentException(
                        String.format(
                                "Layout layer length located at %d must respect the columns count of the"
                                        + " container (layer length: %d, container columns: %d).",
                                row, layerLength, containerColumnsCount));

            for (int column = 0; column < containerColumnsCount; column++) {
                final int targetSlot = column + (row * containerColumnsCount);
                final char character = layer.charAt(column);
                switch (character) {
                    case EMPTY_SLOT_CHAR:
                        break;
                    case ITEM_SLOT_CHAR:
                        {
                            itemsLayer.push(targetSlot);
                            break;
                        }
                    case PREVIOUS_PAGE_CHAR:
                        {
                            if (setupForRender) {
                                resolveNavigationItem(context, NAVIGATE_LEFT);
                                context.setPreviousPageItemSlot(targetSlot);
                            }
                            break;
                        }
                    case NEXT_PAGE_CHAR:
                        {
                            if (setupForRender) {
                                resolveNavigationItem(context, NAVIGATE_RIGHT);
                                context.setNextPageItemSlot(targetSlot);
                            }
                            break;
                        }
                    default:
                        {
                            final LayoutPattern pattern = getLayoutOrNull(character);
                            if (pattern != null) pattern.getSlots().push(targetSlot);
                        }
                }
            }
        }

        if (!setupForRender) return;

        context.getPaginator().setPageSize(itemsLayer.size());
        context.setLayoutSignatureChecked(true);
    }

    private LayoutPattern getLayoutOrNull(char character) {
        return layoutPatterns.stream()
                .filter(pattern -> pattern.getCharacter() == character)
                .findFirst()
                .orElse(null);
    }
}
