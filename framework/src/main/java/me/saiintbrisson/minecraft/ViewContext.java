package me.saiintbrisson.minecraft;

import me.matsubara.roulette.util.InventoryUpdate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

import static me.saiintbrisson.minecraft.View.INVENTORY_ROW_SIZE;

public class ViewContext extends VirtualView {

	protected final View view;
	protected final Player player;
	protected final Inventory inventory;
	private final Map<Integer, Map<String, Object>> slotData = new HashMap<>();
	protected boolean cancelled;
	protected boolean markedToClose;
	boolean checkedLayerSignature;

	Stack<Integer> itemsLayer;
	private boolean invalidated;
	private boolean propagateErrors = true;

	public ViewContext(@NotNull View view, @NotNull Player player, @NotNull Inventory inventory) {
		super(new ViewItem[INVENTORY_ROW_SIZE * (inventory.getSize() / INVENTORY_ROW_SIZE)]);
		this.view = view;
		this.player = player;
		this.inventory = inventory;
	}

	// allow static item placement on OpenViewContext
	ViewContext(@NotNull View view, @NotNull Player player, int inventorySize) {
		super(new ViewItem[inventorySize]);
		this.view = view;
		this.player = player;
		this.inventory = null;
	}

	Stack<Integer> getItemsLayer() {
		return itemsLayer;
	}

	public boolean isMarkedToClose() {
		return markedToClose;
	}

	boolean isCheckedLayerSignature() {
		return checkedLayerSignature;
	}

	void setCheckedLayerSignature(boolean checkedLayerSignature) {
		this.checkedLayerSignature = checkedLayerSignature;
	}

	@Override
	public void setLayout(String... layout) {
		super.setLayout(layout);

		// force layout re-order
		checkedLayerSignature = false;
	}

	/**
	 * Returns the {@link View} of that context.
	 *
	 * @return The View who owns this context.
	 */
	public View getView() {
		return view;
	}

	/**
	 * Returns the {@link Player} of that context.
	 *
	 * @return The player who owns this context.
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Returns the rows count of that context.
	 *
	 * @return The inventory rows count of this context.
	 */
	public int getRows() {
		return inventory.getSize() / 9;
	}

	/**
	 * Returns the {@link Inventory} of that context.
	 *
	 * @return The inventory of this context.
	 */
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * Returns if the action was canceled.
	 *
	 * @return <code>true</code> if the action in that context was canceled or <code>false</code> otherwise.
	 */
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Defines whether the action that is taking place in that context should be canceled.
	 *
	 * @param cancelled should be canceled.
	 */
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
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
		inventoryModificationTriggered();
		view.update(this);
	}

	public void update(int slot) {
		inventoryModificationTriggered();
		view.update(this, slot);
	}

	/**
	 * Close this context in the next tick.
	 */
	public void close() {
		this.markedToClose = true;
	}

	/**
	 * Immediately closes the current view for that context.
	 */
	public void closeNow() {
		player.closeInventory();
	}

	/**
	 * Cancel the current context event and marks the context
	 * to be closed in the next event.
	 *
	 * @see #setCancelled(boolean)
	 * @see #close()
	 */
	public void cancelAndClose() {
		setCancelled(true);
		close();
	}

	/**
	 * Clear a slot from that view for that context.
	 * It is necessary to use `update` to apply the action.
	 *
	 * @param slot the slot to be cleared.
	 */
	public void clear(int slot) {
		inventoryModificationTriggered();
		getItems()[slot] = null;
	}

	/**
	 * Clears all slots from that view for that context.
	 * It is necessary to use `update` to apply the action.
	 */
	public void clear() {
		inventoryModificationTriggered();
		for (int i = 0; i < getItems().length; i++) {
			clear(i);
		}
	}

	/**
	 * Opens the specified view for users in that context.
	 *
	 * @param view The view to be opened.
	 */
	public void open(Class<? extends View> view) {
		open(view, null, false);
	}

	/**
	 * Opens the specified view for users in that context.
	 *
	 * @param view The view to be opened.
	 * @param transitiveData Whether data from this context should be passed to the next one,
	 *                       to the view that will be opened.
	 */
	public void open(Class<? extends View> view, boolean transitiveData) {
		open(view, null, transitiveData);
	}

	/**
	 * Opens the specified view for users in that context with the specified data.
	 *
	 * @param view The view to be opened.
	 * @param data Custom context data.
	 */
	public void open(Class<? extends View> view, Map<String, Object> data) {
		open(view, data, false);
	}

	/**
	 * Opens the specified view for users in that context with the specified data and/or transitive data.
	 *
	 * @param view           The view to be opened.
	 * @param data           Custom context data.
	 * @param transitiveData Whether data from this context should be passed to the next one,
	 *                       to the view that will be opened.
	 */
	public void open(Class<? extends View> view, Map<String, Object> data, boolean transitiveData) {
		Map<String, Object> contextData = data;
		if (transitiveData) {
			contextData = new HashMap<>(getData());
			contextData.putAll(data);
		}

		this.view.getFrame().open(view, player, contextData);
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

	public void clear(String key) {
		view.clearData(player, key);
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
		if (invalidated)
			throw new IllegalStateException("This context has been invalidated and cannot be reused");

		view.clearData(player);
		slotData.clear();
		layoutPatterns.clear();
		checkedLayerSignature = false;
		invalidated = true;
	}

	/**
	 * Returns the current context as the context of a paged view.
	 *
	 * @param <T> view type parameter.
	 * @return this
	 */
	@SuppressWarnings("unchecked")
	public <T> PaginatedViewContext<T> paginated() {
		if (!(view instanceof PaginatedView))
			throw new IllegalArgumentException("Only paginated views can enforce paginated view context.");

		if (this instanceof DelegatedViewContext)
			return (PaginatedViewContext<T>) ((DelegatedViewContext) this).getDelegate();

		return (PaginatedViewContext<T>) this;
	}

	/**
	 * The title of the player's currently open inventory.
	 *
	 * @return The title of the player's inventory.
	 */
	public @NotNull String getTitle() {
		return player.getOpenInventory().getTitle();
	}

	/**
	 * Updates the title of the inventory for the client of the player who owns this context.
	 * <p>
	 * Notes:
	 * <ul>
	 *     <li>
	 * 	 This is not to be used before the inventory is opened, if you need to set the **initial title** use
	 *     {@link OpenViewContext#setInventoryTitle(String)} on {@link View#onOpen(OpenViewContext)}.
	 * 	 </li>
	 * 	 <li>
	 * 	 This function is not agnostic, so it may be that your server version is not yet supported,
	 * 	 if you try to use this function and fail, report it to the IF developers to add support to your version.
	 * 	 </li>
	 * </ul>
	 *
	 * @param title The new inventory title.
	 */
	public void updateTitle(@NotNull String title) {
		inventoryModificationTriggered();
		final Plugin plugin = getView().getFrame().getOwner();
		Bukkit.getScheduler().runTaskLater(plugin,
			() -> InventoryUpdate.updateInventory((JavaPlugin) plugin, getPlayer(), title), 2L);
	}

	/**
	 * Updates the inventory title of the customer that owns this context to the initially defined title.
	 * Must be used after {@link #updateTitle(String)} to take effect.
	 */
	public void resetTitle() {
		inventoryModificationTriggered();
		updateTitle(player.getOpenInventory().getTitle());
	}

	/**
	 * If errors should be propagated to the View's error handler for
	 * that context.
	 *
	 * @return If errors will be propagated to the View.
	 */
	public boolean isPropagateErrors() {
		return propagateErrors;
	}

	/**
	 * Defines whether errors should be propagated to the View's error handler.
	 *
	 * @param propagateErrors If errors should be propagated to the View.
	 */
	public void setPropagateErrors(boolean propagateErrors) {
		this.propagateErrors = propagateErrors;
	}

	public boolean isValid() {
		return !invalidated;
	}

	SlotFindResult findNextAvailableSlot(@NotNull ItemStack currentItem) {
		int moveTo = -1;
		boolean stacked = false;

		int idx = 0;
		do {
			// first we try to get a static item from the view
			final ViewItem item = getItem(idx);
			if (item != null) {
				final ItemStack staticItem = item.getItem();

				// we can determine if slot is available only with fallback item items rendered
				// through the rendering function cannot be accessed
				if (staticItem != null) {
					// TODO stack detection
					if (staticItem.isSimilar(currentItem)) {
						stacked = true;
						break;
					}
				}

				continue;
			}

			// checks if there is an item in that slot that was "manually" to the inventory, like
			// another move in
			final ItemStack actualItem = getInventory().getItem(idx);
			if (actualItem != null) {
				if (actualItem.isSimilar(currentItem)) {
					// TODO stack detection
					stacked = true;
					break;
				}

				continue;
			}
		} while (idx++ <= getLastSlot());

		return new SlotFindResult(idx, moveTo, stacked);
	}

	protected void inventoryModificationTriggered() {
	}

	@Override
	final void throwViewException(
		@NotNull ViewContext context,
		@NotNull Exception exception
	) {
		super.throwViewException(context, exception);

		// propagate errors to the View then to the global view error handler
		if (context.isPropagateErrors()) {
			getView().throwViewException(context, exception);
			return;
		}

		final ViewErrorHandler globalErrorHandler =
			getView().getFrame().getErrorHandler();

		if (globalErrorHandler != null)
			globalErrorHandler.error(context, exception);
	}

	@Override
	public String toString() {
		return "ViewContext{" +
			"view=" + view +
			", player=" + player +
			", inventory=" + inventory +
			", cancelled=" + cancelled +
			", data=" + getData() +
			", propagateErrors=" + propagateErrors +
			", invalidated=" + invalidated +
			", markedToClose=" + markedToClose +
			"} " + super.toString();
	}

}