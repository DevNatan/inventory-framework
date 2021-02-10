package me.saiintbrisson.minecraft;

import me.saiintbrisson.minecraft.utils.Paginator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Stack;
import java.util.function.Function;

import static me.saiintbrisson.minecraft.PaginatedViewContext.FIRST_PAGE;

public abstract class PaginatedView<T> extends View {

    private static final char PREVIOUS_PAGE_CHAR = '<';
    private static final char NEXT_PAGE_CHAR = '>';
    private static final char EMPTY_SLOT_CHAR = 'X';
    private static final char ITEM_SLOT_CHAR = 'O';

    private Paginator<T> paginator;
    private int offset;
    private int limit;

    public PaginatedView(int rows, String title) {
        this(null, rows, title);
    }

    public PaginatedView(ViewFrame frame, int rows, String title) {
        super(frame, rows, title);
        this.offset = getFirstSlot();
        this.limit = getLastSlot();
    }

    public PaginatedView(int rows, String title, int offset, int limit) {
        this(null, rows, title, offset, limit);
    }

    public PaginatedView(ViewFrame frame, int rows, String title, int offset, int limit) {
        super(frame, rows, title);
        this.offset = offset;
        this.limit = limit;
    }

    /**
     * @deprecated Use {@link #setSource(List)} instead.
     */
    @Deprecated
    public void setPaginationSource(List<T> source) {
        setSource(source);
    }

    public void setSource(List<T> source) {
        this.paginator = new Paginator<>(getPageSize(), source);
    }

    public int getPageSize() {
        return limit - offset;
    }

    public Paginator<?> getPaginator() {
        return paginator;
    }

    /**
     * This method will not be available in the next versions.
     * @param paginator
     */
    @Deprecated
    public void setPaginator(Paginator<T> paginator) {
        this.paginator = paginator;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        if (layout != null)
            throw new IllegalArgumentException("Layered views cannot set the offset slot.");

        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (layout != null)
            throw new IllegalArgumentException("Layered views cannot set the limit slot.");

        this.limit = limit;
    }

    public ViewItem getPreviousPageItem(PaginatedViewContext<T> context) {
        ViewFrame frame = getFrame();
        if (frame == null)
            throw new IllegalArgumentException("View frame is null and the previous page item has not been defined");

        Function<PaginatedViewContext<?>, ViewItem> previous = getFrame().getDefaultPreviousPageItem();
        if (previous == null)
            return null;

        ViewItem item = previous.apply(context);
        if (item != null && context.getPreviousPageItemSlot() == UNSET_SLOT)
            context.setPreviousPageItemSlot(item.getSlot());

        return item;
    }

    public ViewItem getNextPageItem(PaginatedViewContext<T> context) {
        ViewFrame frame = getFrame();
        if (frame == null)
            throw new IllegalArgumentException("View frame is null and the next page item has not been defined");

        Function<PaginatedViewContext<?>, ViewItem> next = getFrame().getDefaultNextPageItem();
        if (next == null)
            return null;

        ViewItem item = next.apply(context);
        if (item != null && context.getNextPageItemSlot() == UNSET_SLOT)
            context.setNextPageItemSlot(item.getSlot());

        return item;
    }

    private void updateNavigationPreviousItem(PaginatedViewContext<T> context) {
        final ViewItem item = getPreviousPageItem(context);

        // check it for layered views
        final int defaultSlot = context.getPreviousPageItemSlot();
        if (item == null) {
            if (defaultSlot != UNSET_SLOT)
                clearSlot(context, defaultSlot);

            context.setPreviousPageItemSlot(UNSET_SLOT);
        } else {
            if (defaultSlot == UNSET_SLOT) {
                if (item.getSlot() == UNSET_SLOT)
                    throw new IllegalArgumentException("No slot has been provided for previous page item.");

                context.setPreviousPageItemSlot(item.getSlot());
            }

            // checking if the item already has a native click handler
            // will make it possible to have custom handlers.
            if (item.getClickHandler() == null)
                item.onClick(ctx -> ctx.paginated().switchToNextPage());

            render(context, item.withCancelOnClick(true), context.getPreviousPageItemSlot());
        }
    }

    private void updateNavigationNextItem(PaginatedViewContext<T> context) {
        final ViewItem item = getNextPageItem(context);

        // check it for layered views
        final int defaultSlot = context.getNextPageItemSlot();
        if (item == null) {
            if (defaultSlot != UNSET_SLOT)
                clearSlot(context, defaultSlot);

            context.setNextPageItemSlot(UNSET_SLOT);
        } else {
            if (defaultSlot == UNSET_SLOT) {
                if (item.getSlot() == UNSET_SLOT)
                    throw new IllegalArgumentException("No slot has been provided for next page item.");

                context.setNextPageItemSlot(item.getSlot());
            }

            // checking if the item already has a native click handler
            // will make it possible to have custom handlers.
            if (item.getClickHandler() == null)
                item.onClick(ctx -> ctx.paginated().switchToNextPage());

            render(context, item.withCancelOnClick(true), context.getNextPageItemSlot());
        }
    }

    final void updateNavigation(PaginatedViewContext<T> context) {
        updateNavigationPreviousItem(context);
        updateNavigationNextItem(context);
    }

    public void render(ViewContext context, ViewItem item, int slot) {
        // ensure that the item is available in the virtual context
        context.getItems()[slot] = item;
        super.render(context, item, slot);
    }

    private void clearSlot(ViewContext context, int slot) {
        context.getItems()[slot] = null;
        context.getInventory().setItem(slot, null);
    }

    final void updateContext(PaginatedViewContext<T> context, int page) {
        // AIOOBE
        if (!context.getPaginator().hasPage(page))
            return;

        if (layout != null) {
            if (!context.checkedLayerSignature) {
                // since the layout is only defined once, we cache it
                // to avoid unnecessary processing every time we update the context.
                final int len = layout.length;
                final int columnsLimit = context.getInventory().getSize() / INVENTORY_ROW_SIZE;
                if (len != columnsLimit)
                    throw new IllegalArgumentException("Layout columns must respect the size of the inventory (" + len + " != " + columnsLimit + ")");

                context.itemsLayer = new Stack<>();
                for (int row = 0; row < len; row++) {
                    final String layer = layout[row];
                    if (layer.length() != INVENTORY_ROW_SIZE)
                        throw new IllegalArgumentException("The layer located at " + row + " must contain " + INVENTORY_ROW_SIZE + " characters.");

                    for (int col = 0; col < INVENTORY_ROW_SIZE; col++) {
                        final int targetSlot = col + (row * INVENTORY_ROW_SIZE);
                        final char c = layer.charAt(col);
                        switch (c) {
                            case EMPTY_SLOT_CHAR:
                                break;
                            case ITEM_SLOT_CHAR: {
                                context.itemsLayer.push(targetSlot);
                                break;
                            }
                            case PREVIOUS_PAGE_CHAR: {
                                final int slot = context.getPreviousPageItemSlot();
                                if (getFrame().getDefaultPreviousPageItem() == null && slot == UNSET_SLOT && getPreviousPageItem(context) == null)
                                    throw new IllegalArgumentException("Found previous page item character (" + PREVIOUS_PAGE_CHAR + ") but no item was defined.");

                                context.setPreviousPageItemSlot(targetSlot);
                                break;
                            }
                            case NEXT_PAGE_CHAR: {
                                final int slot = context.getNextPageItemSlot();
                                if (getFrame().getDefaultNextPageItem() == null && slot == UNSET_SLOT && getNextPageItem(context) == null)
                                    throw new IllegalArgumentException("Found next page item character (" + NEXT_PAGE_CHAR + ") but no item was defined.");

                                context.setNextPageItemSlot(targetSlot);
                                break;
                            }
                            default:
                                throw new IllegalArgumentException("Invalid layer character: " + c);
                        }
                    }
                }

                context.getPaginator().setPageSize(context.itemsLayer.size());
            }

            context.checkedLayerSignature = true;
        }

        context.setPage(page);
        final List<T> elements = context.getPaginator().getPage(page);
        final int size = elements.size();
        final int lastSlot = layout == null ? limit : context.itemsLayer.peek();
        for (int i = 0; i < lastSlot; i++) {
            if (layout != null && i < context.itemsLayer.size())
                break;

            final int targetSlot = layout == null ? offset + i : context.itemsLayer.elementAt(i);
            if (i < size)
                renderPaginatedItemAt(context, targetSlot, elements.get(i));
            else {
                final ViewItem item = getItem(targetSlot);
                // check if a non-virtual item has been defined in that slot
                if (item != null)
                    continue;

                clearSlot(context, targetSlot);
            }
        }

        updateNavigation(context);
    }

    private void renderPaginatedItemAt(PaginatedViewContext<T> context, int slot, T value) {
        final ViewItem item = new ViewItem(slot);
        item.setCancelOnClick(context.getView().isCancelOnClick());
        onPaginationItemRender(context, item, value);
        render(context, item, slot);
    }

    @Override
    protected ViewContext createContext(View view, Player player, Inventory inventory) {
        return new PaginatedViewContext<>(this, player, inventory, FIRST_PAGE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void render(ViewContext context) {
        // render all non-virtual items first
        super.render(context);
        updateContext((PaginatedViewContext<T>) context, FIRST_PAGE);
    }

    protected abstract void onPaginationItemRender(PaginatedViewContext<T> context, ViewItem item, T value);

}
