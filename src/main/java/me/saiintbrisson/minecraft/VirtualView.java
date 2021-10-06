package me.saiintbrisson.minecraft;

import com.google.common.base.Preconditions;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static me.saiintbrisson.minecraft.View.UNSET_SLOT;

public class VirtualView {

    protected ViewItem[] items;
    protected String[] layout;

    public VirtualView(ViewItem[] items) {
        this.items = items;
    }

    /**
     * Returns the layout of items for this view.
     */
    public String[] getLayout() {
        return layout;
    }

    /**
     * Sets the layout of items for this view.
     * The layout must respect the size and length of the inventory.
     * @param layout the layout.
     */
    public void setLayout(String... layout) {
        Preconditions.checkNotNull(layout, "Layout cannot be null.");
        this.layout = layout;
    }

    /**
     * Returns all registered {@link ViewItem}s.
     */
    public ViewItem[] getItems() {
        return items;
    }

    /**
     * Returns a {@link ViewItem} that is in the specified slot or {@code null} if not defined.
     *
     * @param slot the item slot.
     */
    public ViewItem getItem(int slot) {
        return items[slot];
    }

    /**
     * Returns the number of the first slot available or not.
     */
    public int getFirstSlot() {
        return 0;
    }

    /**
     * Returns the number of the last slot available or not.
     */
    public int getLastSlot() {
        return items.length - 1;
    }

    /**
     * Returns a new {@link ViewItem}.
     */
    public ViewItem item() {
        return new ViewItem(UNSET_SLOT);
    }

    /**
     * Returns a new {@link ViewItem} with a {@link ItemStack}.
     * @param item the item.
     */
    public ViewItem item(final ItemStack item) {
        return new ViewItem(UNSET_SLOT).withItem(item);
    }

    /**
     * Registers a {@link ViewItem} in the specified slot.
     *
     * @param slot the item slot.
     */
    public ViewItem slot(int slot) {
        final int max = getLastSlot() + 1;
        if (slot > max)
            throw new IllegalArgumentException("Slot exceeds the inventory limit (expected: < " + max + ", given: " + slot + ").");

        return items[slot] = new ViewItem(slot);
    }

    /**
     * Registers a {@link ViewItem} with a {@link ItemStack} in the specified slot.
     *
     * @param slot the item slot.
     * @param item the item to be set.
     */
    public ViewItem slot(int slot, ItemStack item) {
        return slot(slot).withItem(item);
    }

    /**
     * Registers a {@link ViewItem} in the specified row and column.
     *
     * @param row    the item slot row.
     * @param column the item slot column.
     */
    public ViewItem slot(int row, int column) {
        return slot((Math.max((row - 1), 0) * 9) + Math.max((column - 1), 0));
    }

    /**
     * Registers a {@link ViewItem} with a {@link ItemStack} in the specified row and column.
     *
     * @param row    the item slot row.
     * @param column the item slot column.
     * @param item   the item to be set.
     */
    public ViewItem slot(int row, int column, ItemStack item) {
        return slot(row, column).withItem(item);
    }

    /**
     * Registers a {@link ViewItem} in the first slot.
     *
     * @see #getFirstSlot()
     */
    public ViewItem firstSlot() {
        return slot(getFirstSlot());
    }

    /**
     * Registers a {@link ViewItem} with a {@link ItemStack} in the first slot.
     *
     * @param item the item to be set.
     * @see #getFirstSlot()
     */
    public ViewItem firstSlot(ItemStack item) {
        return slot(getFirstSlot(), item);
    }

    /**
     * Registers a {@link ViewItem} in the last slot.
     *
     * @see #getLastSlot()
     */
    public ViewItem lastSlot() {
        return slot(getLastSlot());
    }

    /**
     * Registers a {@link ViewItem} with a {@link ItemStack} in the last slot.
     *
     * @param item the item to be set.
     * @see #getLastSlot()
     */
    public ViewItem lastSlot(ItemStack item) {
        return slot(getLastSlot(), item);
    }

    /**
     * Render all items in this view to the specified context.
     *
     * @param context the target context.
     */
    public void render(ViewContext context) {
        Preconditions.checkNotNull(context, "Context cannot be null.");

        for (int i = 0; i < items.length; i++) {
            render(context, i);
        }
    }

    public void render(ViewContext context, int slot) {
        final ViewItem item = resolve(context, slot);
        if (item == null)
            return;

        render(context, item, slot);
    }

    /**
     * Renders a {@link ViewItem} for the specified context.
     *
     * @param context the target context.
     * @param slot    the slot that the item will be rendered.
     */
    public void render(ViewContext context, ViewItem item, int slot) {
        Preconditions.checkNotNull(item, "Render item cannot be null");

        final ItemStack fallback = item.getItem();
        if (item.getRenderHandler() != null) {
            final ViewSlotContext render = context instanceof ViewSlotContext ?
                    (ViewSlotContext) context :
                    new DelegatedViewContext(context, slot, fallback);
            item.getRenderHandler().handle(render);
            if (render.hasChanged()) {
                render.getInventory().setItem(slot, render.getItem());
                render.setChanged(false);
                return;
            }
        }

        if (fallback == null)
            throw new IllegalArgumentException("No item were provided and the rendering function was not defined at slot " + slot + ".");

        if (!(context instanceof ViewSlotContext))
            context.getInventory().setItem(slot, fallback);
    }

    /**
     * Updates the specified {@link ViewContext} according to this view.
     *
     * @param context the target context.
     */
    public void update(ViewContext context) {
        Preconditions.checkNotNull(context, "Context cannot be null");
        for (int i = 0; i < items.length; i++) {
            update(context, i);
        }
    }

    /**
     * Updates only one {@link ViewItem} in that view to the specified {@link ViewContext}.
     *
     * @param context the target context.
     * @param slot    the slot that the item will be updated.
     */
    public void update(ViewContext context, int slot) {
        Preconditions.checkNotNull(context, "Context cannot be null");

        final ViewItem item = resolve(context, slot);
        if (item == null) {
            context.getInventory().setItem(slot, null);
            return;
        }

        if (item.getUpdateHandler() != null) {
            final ViewSlotContext update = context instanceof ViewSlotContext ?
                    (ViewSlotContext) context :
                    new DelegatedViewContext(context, slot, item.getItem());

            item.getUpdateHandler().handle(update);
            if (update.hasChanged()) {
                render(update, item, slot);
                update.setChanged(false);
            }
            return;
        }

        if (context instanceof ViewSlotContext) {
            final ViewSlotContext slotContext = (ViewSlotContext) context;

            // when using #updateSlot() inside a onClick
            if (item.getRenderHandler() != null)
                item.getRenderHandler().handle(slotContext);

            // can be global click/move (in/out) handler
            if (slotContext.hasChanged()) {
                slotContext.getInventory().setItem(slot, slotContext.getItem());
                slotContext.setChanged(false);
                return;
            }
        }

        // update handler can be used as a void function, so
        // we must fall back to the render handler to update the item
        render(context, item, slot);
    }

    ViewItem resolve(ViewContext context, int slot) {
        if (this instanceof ViewContext)
            throw new IllegalArgumentException("Context can't resolve items itself");

        final ViewItem item = items[slot];
        if (item == null)
            return context.getItem(slot);

        return item;
    }

    @Override
    public String toString() {
        return Arrays.stream(items).filter(Objects::nonNull).map(ViewItem::toString).collect(Collectors.joining());
    }

}
