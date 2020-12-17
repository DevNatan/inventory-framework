package me.saiintbrisson.minecraft;

import me.saiintbrisson.minecraft.utils.Paginator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

import static me.saiintbrisson.minecraft.View.UNSET_SLOT;

public class PaginatedViewContext<T> extends ViewContext {

    public static final int FIRST_PAGE = 0;

    private int page;
    private Paginator<T> paginator;
    private int previousPageItemSlot = UNSET_SLOT;
    private int nextPageItemSlot = UNSET_SLOT;

    public PaginatedViewContext(View view, Player player, Inventory inventory, int page) {
        super(view, player, inventory);
        this.page = page;
    }

    /**
     * Returns the current page for that context.
     */
    public int getPage() {
        return page;
    }

    /**
     * Sets the current page in that context.
     * @param page the new page numbering.
     */
    void setPage(int page) {
        this.page = page;
    }

    /**
     * Returns the number of the previous page with a minimum value of {@link #FIRST_PAGE}.
     */
    public int getPreviousPage() {
        return Math.max(FIRST_PAGE, page - 1);
    }

    /**
     * Returns the number of the next page with the maximum value of the number of available pages.
     */
    public int getNextPage() {
        return Math.min(paginator.count(), page + 1);
    }

    /**
     * Returns `true` if there are more pages than the current one available or `false` otherwise.
     */
    public boolean hasNextPage() {
        return paginator.hasPage(page + 1);
    }

    /**
     * Returns `true` if the current page is the first page of the available pages or `false` otherwise.
     */
    public boolean isFirstPage() {
        return page == FIRST_PAGE;
    }

    /**
     * Returns `true` if the current page is the last page of the available pages or `false` otherwise.
     */
    public boolean isLastPage() {
        return !hasNextPage();
    }

    /**
     * Updates the current context by switching to the previous page if available.
     */
    public void switchToPreviousPage() {
        switchTo(page - 1);
    }

    /**
     * Updates the current context by switching to the next page if available.
     */
    public void switchToNextPage() {
        switchTo(page + 1);
    }

    /**
     * Returns the {@link Paginator} of that context.
     */
    Paginator<T> getPaginator() {
        return paginator;
    }

    /**
     * Defines the {@link Paginator} of that context.
     * @param paginator the new paginator.
     */
    void setPaginator(Paginator<T> paginator) {
        this.paginator = paginator;
    }

    /**
     * Defines the source of the {@link Paginator} for this context.
     * @param source the pagination source.
     * @deprecated Use {@link ViewContext#setSource(List)} instead.
     */
    @Deprecated
    public void setPaginationSource(List<?> source) {
        this.paginator = new Paginator(((PaginatedView<T>) getView()).getPageSize(), source);
    }

    /**
     * Returns `true` if a source has been set for pagination or `false`, otherwise.
     */
    public boolean hasPaginationSource() {
        return paginator != null;
    }

    public int getPreviousPageItemSlot() {
        return previousPageItemSlot;
    }

    public void setPreviousPageItemSlot(int previousPageItemSlot) {
        this.previousPageItemSlot = previousPageItemSlot;
    }

    public int getNextPageItemSlot() {
        return nextPageItemSlot;
    }

    public void setNextPageItemSlot(int nextPageItemSlot) {
        this.nextPageItemSlot = nextPageItemSlot;
    }

}
