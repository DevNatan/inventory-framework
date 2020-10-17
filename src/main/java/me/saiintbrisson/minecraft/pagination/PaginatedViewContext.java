package me.saiintbrisson.minecraft.pagination;

import me.saiintbrisson.minecraft.View;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.utils.Paginator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PaginatedViewContext extends ViewContext {

    private int page;
    private final Paginator<?> paginator;

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

    public boolean isFirstPage() {
        System.out.println("#isFirstPage (page = " + page + ", count = " + paginator.count());
        return page == 0;
    }

    public boolean isLastPage() {
        System.out.println("#isLastPage (page = " + page + ", count = " + paginator.count());
        return page != paginator.count();
    }

    public void switchTo(int page) {
        ((PaginatedView) getView()).updateContext(this, page);
    }

}
