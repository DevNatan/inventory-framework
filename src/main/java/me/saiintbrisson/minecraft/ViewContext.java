package me.saiintbrisson.minecraft;

import me.saiintbrisson.minecraft.utils.Paginator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;
import java.util.function.Supplier;

public class ViewContext extends VirtualView {

    protected final View view;
    protected final Player player;
    protected final Inventory inventory;
    protected boolean cancelled;
    private final Map<Integer, Map<String, Object>> slotData = new HashMap<>();
    boolean checkedLayerSignature;
    Stack<Integer> itemsLayer;

    public ViewContext(View view, Player player, Inventory inventory) {
        super(inventory == null ? null : new ViewItem[View.INVENTORY_ROW_SIZE * (inventory.getSize() / 9)]);
        this.view = view;
        this.player = player;
        this.inventory = inventory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewItem slot(int slot) {
        final ViewItem item = super.slot(slot);
        item.setCancelOnClick(view.isCancelOnClick());
        return item;
    }

    @Override
    public void setLayout(String... layout) {
        super.setLayout(layout);

        // force layout re-order
        checkedLayerSignature = false;
    }

    /**
     * Returns the {@link View} of that context.
     */
    public View getView() {
        return view;
    }

    /**
     * Returns the {@link Player} of that context.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the {@link Inventory} of that context.
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Returns `true` if the action in that context was canceled or `false` otherwise.
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Defines whether the action that is taking place in that context should be canceled.
     * @param cancelled should be canceled.
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Cancels the action that is taking place in that context.
     * @deprecated Use {@link #setCancelled(boolean)} instead.
     */
    @Deprecated
    public void cancel() {
        this.cancelled = true;
    }

    /**
     * Returns the current data of the player tied to that context.
     */
    public Map<String, Object> getData() {
        return view.getData(player);
    }

    /**
     * Updates the current view to that context.
     */
    public void update() {
        view.update(this);
    }

    /**
     * Closes the current view for that context.
     */
    public void close() {
        player.closeInventory();
    }

    /**
     * Clear a slot from that view for that context.
     * It is necessary to use `update` to apply the action.
     * @param slot the slot to be cleared.
     */
    public void clear(int slot) {
        getItems()[slot] = null;
    }

    /**
     * Clears all slots from that view for that context.
     * It is necessary to use `update` to apply the action.
     */
    public void clear() {
        for (int i = 0; i < getItems().length; i++) {
            clear(i);
        }
    }

    /**
     * Opens the specified view for users in that context.
     * @param view the view to be open.
     */
    public void open(Class<? extends View> view) {
        this.view.getFrame().open(view, player);
    }

    /**
     * Opens the specified view for users in that context with the specified data.
     * @param view the view to be open.
     * @param data custom data.
     */
    public void open(Class<? extends View> view, Map<String, Object> data) {
        this.view.getFrame().open(view, player, data);
    }

    public <T> T get(String key) {
        return view.getData(player, key);
    }

    public <T> T get(String key, Supplier<T> defaultValue) {
        return view.getData(player, key, defaultValue);
    }

    public void set(String key, Object value) {
        view.setData(player, key, value);
    }

    public boolean has(String key) {
        return view.hasData(player, key);
    }

    public Map<Integer, Map<String, Object>> slotData() {
        return slotData;
    }

    public Map<String, Object> getSlotData(int slot) {
        return slotData.computeIfAbsent(slot, $ -> new HashMap<>());
    }

    @SuppressWarnings("unchecked")
    public <T> T getSlotData(int slot, String key) {
        if (!getSlotData(slot).containsKey(key))
            return null;

        return (T) getSlotData(slot).get(key);
    }

    public <T> T getSlotData(int slot, String key, Supplier<T> defaultValue) {
        T value = getSlotData(slot, key);
        if (value == null)
            return defaultValue.get();

        return value;
    }

    public void setSlotData(int slot, String key, Object value) {
        getSlotData(slot).put(key, value);
    }

    public boolean hasSlotData(int slot, String key) {
        return getSlotData(slot).containsKey(key);
    }

    void invalidate() {
        view.clearData(player);
    }

    @Override
    public int getLastSlot() {
        return inventory.getSize() - 1;
    }

    /**
     * @deprecated Use {@link #paginated()} and {@link PaginatedViewContext#setSource(List)} instead.
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public void setSource(List<?> source) {
        if (!(this instanceof PaginatedViewContext))
            throw new IllegalArgumentException("Only paginated views can have a source.");

        ((PaginatedViewContext<?>) this).setSource(source);
    }

    /**
     * Updates the current context by jumping to the specified page.
     * @param page the new page.
     * @deprecated Use {@link #paginated()} and {@link PaginatedViewContext#switchTo(int)} instead.
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public void switchTo(int page) {
        if (!(this instanceof PaginatedViewContext))
            throw new IllegalArgumentException("Only paginated views can switch between pages.");

        ((PaginatedView<?>) view).updateContext((PaginatedViewContext) this, page);
    }

    /**
     * Returns the current context as the context of a paged view.
     * @param <T> view type parameter.
     * @return this
     */
    @SuppressWarnings("unchecked")
    public <T> PaginatedViewContext<T> paginated() {
        if (!(view instanceof PaginatedView))
            throw new IllegalArgumentException("Only paginated views can enforce paginated view context.");

        return (PaginatedViewContext<T>) this;
    }

    @Override
    public String toString() {
        return "ViewContext{" +
                "view=" + view +
                ", player=" + player +
                ", inventory=" + inventory +
                ", cancelled=" + cancelled +
                ", data=" + getData() +
                "} " + super.toString();
    }

}