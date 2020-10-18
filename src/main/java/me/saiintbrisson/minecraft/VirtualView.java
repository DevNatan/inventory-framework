package me.saiintbrisson.minecraft;

import me.saiintbrisson.minecraft.pagination.PaginatedView;
import me.saiintbrisson.minecraft.pagination.PaginatedViewContext;
import me.saiintbrisson.minecraft.utils.Paginator;
import org.bukkit.inventory.ItemStack;

public abstract class VirtualView {

    private ViewItem[] items;

    public VirtualView(ViewItem[] items) {
        this.items = items;
    }

    public ViewItem[] getItems() {
        return items;
    }

    public ViewItem getItem(int slot) {
        return getItems()[slot];
    }

    public ViewItem slot(int slot) {
        int max = getLastSlot() + 1;
        if (slot > max)
            throw new IllegalArgumentException("Slot exceeds the inventory limit (expected: < " + max + ", given: " + slot + ")");

        return getItems()[slot] = new ViewItem(slot);
    }

    public ViewItem slot(int row, int column) {
        return slot((Math.max((row - 1), 0) * 9) + Math.max((column - 1), 0));
    }

    public int getFirstSlot() {
        return 0;
    }

    public ViewItem firstSlot() {
        return slot(getFirstSlot());
    }

    public ViewItem lastSlot() {
        return slot(getLastSlot());
    }

    protected void renderSlot(ViewContext context, ViewItem item, int slot) {
        ItemStack result = item.getItem();
        if (item.getRenderHandler() != null) {
            ViewSlotContext slotContext = new SynchronizedViewContext(context, slot, result);
            item.getRenderHandler().handle(slotContext, null);
            if (!slotContext.hasChanged())
                return;

            result = slotContext.getItem();
        } else if (result != null)
            result = result.clone();

        context.getInventory().setItem(slot, result);
    }


    protected void renderSlot(ViewContext context, int slot) {
        ViewItem item = getItem(slot);
        if (item == null)
            return;

        renderSlot(context, item, slot);
    }

    protected void render(ViewContext context) {
        for (int i = 0; i < getItems().length; i++) {
            renderSlot(context, i);
        }

        if (this instanceof PaginatedView) {
            PaginatedView<?> paginated = (PaginatedView<?>) this;
            if (paginated.getPaginationSource().isEmpty())
                return;

            Paginator<?> paginator = new Paginator<>(paginated.getLimit() - paginated.getOffset(), paginated.getPaginationSource());
            paginated.setPaginator(paginator);

            PaginatedViewContext viewContext = new PaginatedViewContext(paginated, context.getPlayer(), context.getInventory(), 0, paginator);
            paginated.updateNavigation(viewContext);
            viewContext.switchTo(0);
        }

    }

    public abstract int getLastSlot();

}
