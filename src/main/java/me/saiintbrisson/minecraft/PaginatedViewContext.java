package me.saiintbrisson.minecraft;

import me.saiintbrisson.minecraft.utils.Paginator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PaginatedViewContext extends ViewContext {

    private int page;
    private final Paginator<?> paginator;
    private int previousPageItemSlot = -1;
    private int nextPageItemSlot = -1;

    public PaginatedViewContext(View view, Player player, Inventory inventory, int page, Paginator<?> paginator) {
        super(view, player, inventory);
        this.page = page;
        this.paginator = paginator;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Paginator<?> getPaginator() {
        return paginator;
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
