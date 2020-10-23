package me.saiintbrisson.minecraft;

import com.google.common.base.Preconditions;
import org.bukkit.inventory.Inventory;
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

    public ViewItem slot(int slot, ItemStack item) {
        return slot(slot).withItem(item);
    }

    public ViewItem slot(int row, int column) {
        return slot((Math.max((row - 1), 0) * 9) + Math.max((column - 1), 0));
    }

    public ViewItem slot(int row, int column, ItemStack item) {
        return slot(row, column).withItem(item);
    }

    public int getFirstSlot() {
        return 0;
    }

    public ViewItem firstSlot() {
        return slot(getFirstSlot());
    }

    public ViewItem firstSlot(ItemStack item) {
        return slot(getFirstSlot(), item);
    }

    public ViewItem lastSlot() {
        return slot(getLastSlot());
    }

    public ViewItem lastSlot(ItemStack item) {
        return slot(getLastSlot(), item);
    }

    protected void renderSlot(ViewContext context, ViewItem item) {
        renderSlot(context, item, item.getSlot());
    }

    protected void renderSlot(ViewContext context, ViewItem item, int slot) {
        ItemStack result = item.getItem();
        if (item.getRenderHandler() != null) {
            ViewSlotContext slotContext = new SynchronizedViewContext(context, slot, result);
            item.getRenderHandler().handle(slotContext);
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
    }

    public void updateSlot(ViewContext context, int slot) {
        Preconditions.checkNotNull(context, "Context cannot be null");

        Inventory inventory = context.getInventory();
        Preconditions.checkNotNull(inventory, "Player inventory cannot be null");

        ViewItem item = getItem(slot);
        if (item == null)
            return;

        ViewSlotContext slotContext = new SynchronizedViewContext(context, slot, inventory.getItem(slot));
        if (item.getUpdateHandler() != null) {
            item.getUpdateHandler().handle(slotContext);
            inventory.setItem(slot, slotContext.getItem());
        } else
            renderSlot(slotContext, item, slot);
    }

    public abstract int getLastSlot();

}
