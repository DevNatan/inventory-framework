package me.saiintbrisson.minecraft;

import me.saiintbrisson.minecraft.utils.Paginator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.function.Function;

public abstract class PaginatedView<T> extends View {

    private List<T> source;
    private Paginator<?> paginator;
    private final int offset;
    private final int limit;

    public PaginatedView(int rows, String title) {
        this(null, rows, title);
    }

    public PaginatedView(ViewFrame frame, int rows, String title) {
        super(frame, rows, title);
        this.offset = getFirstSlot() + 1;
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

    public List<T> getPaginationSource() {
        return source;
    }

    public void setPaginationSource(List<T> source) {
        this.source = source;
        this.paginator = new Paginator<>(getPageSize(), source);
    }

    public int getPageSize() {
        return limit - offset;
    }

    public Paginator<?> getPaginator() {
        return paginator;
    }

    public void setPaginator(Paginator<?> paginator) {
        this.paginator = paginator;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    @Override
    public ViewItem slot(int slot) {
        return super.slot(slot);
    }

    public ViewItem getPreviousPageItem(PaginatedViewContext context) {
        ViewFrame frame = getFrame();
        if (frame == null)
            throw new IllegalArgumentException("View frame is null and the previous page item has not been defined");

        Function<PaginatedViewContext, ViewItem> previous = getFrame().getDefaultPreviousPageItem();
        if (previous == null)
            return null;

        ViewItem item = previous.apply(context);
        if (item != null)
            context.setPreviousPageItemSlot(item.getSlot());

        return item;
    }

    public ViewItem getNextPageItem(PaginatedViewContext context) {
        ViewFrame frame = getFrame();
        if (frame == null)
            throw new IllegalArgumentException("View frame is null and the next page item has not been defined");

        Function<PaginatedViewContext, ViewItem> next = getFrame().getDefaultNextPageItem();
        if (next == null)
            return null;

        ViewItem item = next.apply(context);
        if (item != null)
            context.setNextPageItemSlot(item.getSlot());

        return item;
    }

    final void updateNavigation(PaginatedViewContext context) {
        ViewItem prev = getPreviousPageItem(context);
        if (prev != null) {
            renderSlot(context, prev.cancelOnClick().onClick($ -> context.switchToPreviousPage()));
        } else if (context.getPreviousPageItemSlot() != -1) {
            clearSlot(context, context.getPreviousPageItemSlot());
            context.setPreviousPageItemSlot(-1);
        }

        ViewItem next = getNextPageItem(context);
        if (next != null) {
            renderSlot(context, next.cancelOnClick().onClick($ -> context.switchToNextPage()));
        } else if (context.getNextPageItemSlot() != -1) {
            clearSlot(context, context.getNextPageItemSlot());
            context.setNextPageItemSlot(-1);
        }
    }

    @Override
    protected void renderSlot(ViewContext context, ViewItem item, int slot) {
        // ensure that the item is available in the virtual context
        context.getItems()[slot] = item;
        super.renderSlot(context, item, slot);
    }

    private void clearSlot(ViewContext context, int slot) {
        context.getItems()[slot] = null;
        context.getInventory().setItem(slot, null);
    }

    final void updateContext(PaginatedViewContext context, int page) {
        context.setPage(page);
        List<?> elements = context.getPaginator().getPage(page);
        for (int i = 0; i < elements.size(); i++) {
            final int slot = offset + i;
            Object value = elements.get(i);
            if (value == null)
                continue;

            ViewItem item = new ViewItem(slot);
            onPaginationItemRender(context, item, (T) value);
            renderSlot(context, item, slot);
        }

        for (int i = elements.size() + 1; i < limit; i++) {
            clearSlot(context, i);
        }

        updateNavigation(context);
    }

    @Override
    protected ViewContext createContext(View view, Player player, Inventory inventory) {
        return new PaginatedViewContext(this, player, inventory, 0, paginator);
    }

    @Override
    protected void render(ViewContext context) {
        // render all non-virtual items first
        super.render(context);

        PaginatedViewContext paginatedCtx = (PaginatedViewContext) context;
        updateContext(paginatedCtx, 0);
    }

    protected abstract void onPaginationItemRender(PaginatedViewContext context, ViewItem item, T value);

}
