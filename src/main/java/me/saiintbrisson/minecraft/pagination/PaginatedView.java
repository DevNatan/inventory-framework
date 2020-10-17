package me.saiintbrisson.minecraft.pagination;

import me.saiintbrisson.minecraft.View;
import me.saiintbrisson.minecraft.ViewFrame;
import me.saiintbrisson.minecraft.ViewItem;
import me.saiintbrisson.minecraft.utils.Paginator;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiFunction;

public abstract class PaginatedView<T> extends View {

    private static final int UNSET = -1;

    private List<T> source;
    private Paginator<?> paginator;
    private final int offset;
    private final int limit;
    private final Map<Player, ViewItem[]> playerItems = new WeakHashMap<>();

    public PaginatedView(int rows, String title) {
        this(null, rows, title);
    }

    public PaginatedView(ViewFrame frame, int rows, String title) {
        super(frame, rows, title);
        this.offset = 1;
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

    @SuppressWarnings("unchecked")
    public void setPaginationSource(List<T> source) {
        this.source = source;
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

        BiFunction<PaginatedView<?>, PaginatedViewContext, ViewItem> previous = getFrame().getDefaultPreviousPageItem();
        if (previous == null)
            return null;  // unset

        return previous.apply(this, context);
    }

    public ViewItem getNextPageItem(PaginatedViewContext context) {
        ViewFrame frame = getFrame();
        if (frame == null)
            throw new IllegalArgumentException("View frame is null and the next page item has not been defined");

        BiFunction<PaginatedView<?>, PaginatedViewContext, ViewItem> next = getFrame().getDefaultNextPageItem();
        if (next == null)
            return null; // unset

        return next.apply(this, context);
    }

    private ViewItem[] getItems(Player player) {
        return playerItems.computeIfAbsent(player, $ -> new ViewItem[limit - offset]);
    }

    public final void updateNavigation(PaginatedViewContext context) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    final void updateContext(PaginatedViewContext context, int page) {
        context.setPage(page);
        List<?> elements = context.getPaginator().getPage(page);
        int size = elements.size();
        for (int i = 0; i < limit; i++) {
            if (i < size) {
                context.getInventory().clear(i);
                continue;
            }

            Object value = elements.get(i);
            if (value == null)
                continue;

            final int slot = offset + i;
            ViewItem item = new ViewItem(slot);
            onPaginationItemRender(context, item, (T) value);
            renderSlot(context, item, slot);
        }

        updateNavigation(context);
        context.getPlayer().sendMessage("update to " + page + " (offset: " + offset + ", limit: " + limit);
    }

    protected abstract List<T> onPaginationRender(PaginatedViewContext context);

    protected abstract void onPaginationItemRender(PaginatedViewContext context, ViewItem item, T value);

}
