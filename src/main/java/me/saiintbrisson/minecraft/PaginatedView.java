package me.saiintbrisson.minecraft;

import me.saiintbrisson.minecraft.utils.Paginator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.function.Function;

public abstract class PaginatedView<T> extends View {

    private Paginator<T> paginator;
    private int offset;
    private int limit;
    private boolean layered;

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

    public void setPaginationSource(List<T> source) {
        this.paginator = new Paginator<>(getPageSize(), source);
    }

    public int getPageSize() {
        return limit - offset;
    }

    public Paginator<?> getPaginator() {
        return paginator;
    }

    public void setPaginator(Paginator<T> paginator) {
        this.paginator = paginator;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean isLayered() {
        return layered;
    }

    public void setLayered(boolean layered) {
        this.layered = layered;
    }

    public ViewItem getPreviousPageItem(PaginatedViewContext<T> context) {
        ViewFrame frame = getFrame();
        if (frame == null)
            throw new IllegalArgumentException("View frame is null and the previous page item has not been defined");

        Function<PaginatedViewContext<?>, ViewItem> previous = getFrame().getDefaultPreviousPageItem();
        if (previous == null)
            return null;

        ViewItem item = previous.apply(context);
        if (item != null)
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
        if (item != null)
            context.setNextPageItemSlot(item.getSlot());

        return item;
    }

    final void updateNavigation(PaginatedViewContext<T> context) {
        ViewItem prev = getPreviousPageItem(context);
        if (prev != null) {
            render(context, prev.withCancelOnClick(true).onClick($ -> context.switchToPreviousPage()), prev.getSlot());
        } else if (context.getPreviousPageItemSlot() != -1) {
            clearSlot(context, context.getPreviousPageItemSlot());
            context.setPreviousPageItemSlot(-1);
        }

        ViewItem next = getNextPageItem(context);
        if (next != null) {
            render(context, next.withCancelOnClick(true).onClick($ -> context.switchToNextPage()), next.getSlot());
        } else if (context.getNextPageItemSlot() != -1) {
            clearSlot(context, context.getNextPageItemSlot());
            context.setNextPageItemSlot(-1);
        }
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
        if (context.getPaginator() == null) {
            if (this.paginator == null)
                throw new IllegalArgumentException("No pagination source provided.");

            context.setPaginator(this.paginator);
        }

        context.setPage(page);
        final List<T> elements = context.getPaginator().getPage(page);
        final int size = elements.size();
        for (int i = 0; i < size; i++) {
            T value = elements.get(i);
            if (value == null)
                continue;

            final int slot = offset + i;
            final ViewItem item = new ViewItem(slot);
            item.setCancelOnClick(context.getView().isCancelOnClick());
            onPaginationItemRender(context, item, value);
            render(context, item, slot);
        }

        for (int i = size + 1; i < limit; i++) {
            ViewItem item = getItem(i);

            // check if a non-virtual item has been defined in that slot
            if (item != null)
                continue;

            clearSlot(context, i);
        }

        updateNavigation(context);
    }

    @Override
    protected ViewContext createContext(View view, Player player, Inventory inventory) {
        return new PaginatedViewContext<>(this, player, inventory, 0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void render(ViewContext context) {
        // render all non-virtual items first
        super.render(context);
        updateContext((PaginatedViewContext<T>) context, 0);
    }

    protected abstract void onPaginationItemRender(PaginatedViewContext<T> context, ViewItem item, T value);

}
