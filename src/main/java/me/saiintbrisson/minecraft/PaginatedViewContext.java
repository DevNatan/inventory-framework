package me.saiintbrisson.minecraft;

import me.saiintbrisson.minecraft.utils.Paginator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class PaginatedViewContext<T> extends ViewContext {

    private int page;
    private Paginator<T> paginator;
    private int previousPageItemSlot = -1;
    private int nextPageItemSlot = -1;

    public PaginatedViewContext(View view, Player player, Inventory inventory, int page) {
        super(view, player, inventory);
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Paginator<T> getPaginator() {
        return paginator;
    }

    public void setPaginationSource(List<T> source) {
        this.paginator = new Paginator<>(((PaginatedView<T>) getView()).getPageSize(), source);
    }

    public boolean hasPaginationSource() {
        return paginator != null;
    }

    public int getPreviousPage() {
        return Math.max(0, page - 1);
    }

    public int getNextPage() {
        return Math.min(paginator.count(), page + 1);
    }

    int getPreviousPageItemSlot() {
        return previousPageItemSlot;
    }

    void setPreviousPageItemSlot(int previousPageItemSlot) {
        this.previousPageItemSlot = previousPageItemSlot;
    }

    int getNextPageItemSlot() {
        return nextPageItemSlot;
    }

    void setNextPageItemSlot(int nextPageItemSlot) {
        this.nextPageItemSlot = nextPageItemSlot;
    }

    public boolean isFirstPage() {
        return page == 0;
    }

    public boolean hasNextPage() {
        return paginator.hasPage(page + 1);
    }

    public boolean isLastPage() {
        return !hasNextPage();
    }

    public void switchToPreviousPage() {
        switchTo(page - 1);
    }

    public void switchToNextPage() {
        switchTo(page + 1);
    }

    public void switchTo(int page) {
        ((PaginatedView) getView()).updateContext(this, page);
    }

}
