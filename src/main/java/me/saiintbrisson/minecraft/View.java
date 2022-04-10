package me.saiintbrisson.minecraft;

import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class View extends VirtualView implements InventoryHolder, Closeable {

	public static final int INVENTORY_ROW_SIZE = 9;
	public static final int UNSET_SLOT = -1;
	public static final int DEFAULT_INVENTORY_ROW_COUNT = 3;
	private static final Set<String> ENABLED_FEATURES = new HashSet<>();
	private final String title;
	private final int rows;
	private final Map<String, ViewContext> contexts;
	private final Map<Player, Map<String, Object>> data;
	private ViewFrame frame;
	private boolean cancelOnClick, cancelOnPickup, cancelOnDrop, cancelOnDrag, cancelOnClone;
	private boolean cancelOnMoveIn, cancelOnMoveOut, cancelOnShiftClick,
		clearCursorOnClose, closeOnOutsideClick;

	public View() {
		this(0);
	}

	public View(final int rows) {
		this(null, rows, "");
	}

	public View(final int rows, final String title) {
		this(null, rows, title);
	}

	public View(final ViewFrame frame, final int rows, final String title) {
		super(new ViewItem[rows * (rows == 0 ? DEFAULT_INVENTORY_ROW_COUNT : INVENTORY_ROW_SIZE)]);
		this.rows = rows;
		this.frame = frame;
		this.title = title;
		contexts = new WeakHashMap<>();
		data = new WeakHashMap<>();
		cancelOnPickup = true;
		cancelOnDrop = true;
		cancelOnDrag = true;
		cancelOnClone = true;
		cancelOnMoveIn = true;
		cancelOnMoveOut = true;
		cancelOnShiftClick = true;
		closeOnOutsideClick = true;
	}

	public static void enableFeaturePreview(@NotNull String feature) {
		ENABLED_FEATURES.add(feature);
	}

	static boolean isFeatureEnabled(@NotNull String feature) {
		return ENABLED_FEATURES.contains(feature);
	}

	public final Map<Player, ViewContext> getContexts() {
		return contexts.entrySet().stream().collect(Collectors.toMap(e -> Bukkit.getPlayerExact(e.getKey()), Map.Entry::getValue));
	}

	public final ViewContext getContext(final Player player) {
		return contexts.get(player.getName());
	}

	public final ViewFrame getFrame() {
		return frame;
	}

	final void setFrame(final ViewFrame frame) {
		this.frame = frame;
	}

	/**
	 * Use ViewContext#getRows() instead of this method if you're using dynamic rows.
	 *
	 * @return the number of rows in this view
	 */
	@Deprecated
	public final int getRows() {
		return rows;
	}

	/**
	 * The inventory title of this view.
	 * <p>
	 * Does not work with contexts that have had their title dynamically updated.
	 *
	 * @return the inventory title of this view.
	 */
	public final String getTitle() {
		return title;
	}

	protected ViewContext createContext(final View view, final Player player, final Inventory inventory) {
		return isScheduledToUpdate()
			? new ScheduledViewContext(view, player, inventory)
			: new ViewContext(view, player, inventory);
	}

	/**
	 * Opens this view to the given player.
	 *
	 * @param player The player this view will be shown to.
	 */
	public final void open(@NotNull Player player) {
		open(player, null);
	}

	/**
	 * Opens this view to the given player with predefined data.
	 *
	 * @param player The player this view will be shown to.
	 * @param data   The initial data of the player's context.
	 */
	public final void open(
		@NotNull Player player,
		Map<String, Object> data
	) {
		contexts.computeIfPresent(player.getName(), ($, context) -> {
			context.invalidate();
			return null;
		});

		final OpenViewContext preOpenContext = new OpenViewContext(this, player);
		if (data != null)
			setData(player, new HashMap<>(data));
		else {
			// ensure non-transitive data on view switch
			clearData(player);
		}

		runCatching(preOpenContext, () -> onOpen(preOpenContext));
		if (preOpenContext.isCancelled()) {
			clearData(player);
			return;
		}

		int inventorySize = preOpenContext.getInventorySize();
		if (inventorySize != items.length)
			this.expandItemsArray(inventorySize);

		final Inventory inventory = getInventory(preOpenContext.getInventoryTitle(), inventorySize);
		final ViewContext context = createContext(this, player, inventory);
		contexts.put(player.getName(), context);
		onRender(context);
		render(context);
		player.openInventory(inventory);
	}

	public void update() {
		for (final ViewContext ctx : contexts.values())
			ctx.update();
	}

	@Override
	public void update(final ViewContext context) {
		frame.debug("[context]: update");
		onUpdate(context);
		super.update(context);
	}

	@Override
	public void update(final ViewContext context, final int slot) {
		frame.debug("[slot " + slot + "]: update");
		super.update(context, slot);
	}

	@Override
	public void render(final ViewContext context) {
		frame.debug("[context]: render");
		super.render(context);
	}

	@Override
	public void render(final ViewContext context, final int slot) {
		frame.debug("[slot " + slot + "]: render");
		super.render(context, slot);
	}

	@Override
	public void render(final ViewContext context, final ViewItem item, final int slot) {
		frame.debug("[slot " + slot + "]: render with item");
		super.render(context, item, slot);
	}

	final ViewItem resolve(ViewContext context, int slot) {
		frame.debug("[slot " + slot + "]: resolve item");

		// fast path -- ArrayIndexOutOfBoundsException
		if (slot > items.length)
			return null;

		final ViewItem item = items[slot];
		if (item == null)
			return context.getItem(slot);

		return item;
	}

	ViewContext remove(final Player player) {
		frame.debug("[context]: remove");
		final ViewContext context = contexts.remove(player.getName());
		if (context != null) {
			context.invalidate();
			frame.debug("[context]: invalidate");
		}

		return context;
	}

	void remove(final ViewContext context) {
		context.invalidate();
		frame.debug("[context]: invalidate");

		synchronized (contexts) {
			contexts.remove(context.getPlayer().getName());
			frame.debug("[context]: remove");
		}
	}

	/**
	 * Closes the inventory for all players who have this view open.
	 */
	public final void close() {
		for (final Player player : Sets.newHashSet(getContexts().keySet())) {
			player.closeInventory();
		}
	}

	/**
	 * Should cancel the item click while the view is open.
	 *
	 * @return If item click should be cancelled.
	 */
	public final boolean isCancelOnClick() {
		return cancelOnClick;
	}

	/**
	 * Defines whether or not to click on items in the inventory.
	 *
	 * @param cancelOnClick <code>true</code> if click should be cancelled or
	 *                      <code>false</code> otherwise.
	 */
	public final void setCancelOnClick(final boolean cancelOnClick) {
		this.cancelOnClick = cancelOnClick;
	}

	/**
	 * Should cancel the item pickup while the view is open.
	 *
	 * @return If item pickup should be cancelled.
	 */
	public final boolean isCancelOnPickup() {
		return cancelOnPickup;
	}

	/**
	 * Defines whether the player is allowed to pick up items while the view is open.
	 *
	 * @param cancelOnPickup <code>true</code> to cancel item pickup while
	 *                       view is open or <code>false</code> otherwise.
	 */
	public final void setCancelOnPickup(final boolean cancelOnPickup) {
		this.cancelOnPickup = cancelOnPickup;
	}

	/**
	 * Should cancel the item drop while the view is open.
	 *
	 * @return If item drop should be cancelled.
	 */
	public final boolean isCancelOnDrop() {
		return cancelOnDrop;
	}

	public final void setCancelOnDrop(final boolean cancelOnDrop) {
		this.cancelOnDrop = cancelOnDrop;
	}

	public final boolean isCancelOnDrag() {
		return cancelOnDrag;
	}

	public final void setCancelOnDrag(final boolean cancelOnDrag) {
		this.cancelOnDrag = cancelOnDrag;
	}

	/**
	 * Should cancel the item clone (with mouse wheel) while the view is open.
	 *
	 * @return If item clone should be cancelled.
	 */
	public final boolean isCancelOnClone() {
		return cancelOnClone;
	}

	public final void setCancelOnClone(final boolean cancelOnClone) {
		this.cancelOnClone = cancelOnClone;
	}

	/**
	 * If moving items to the view's inventory is allowed.
	 *
	 * @return If moving items to the view's inventory is allowed.
	 */
	public final boolean isCancelOnMoveIn() {
		return cancelOnMoveIn;
	}

	/**
	 * Defines whether or not to move items to the view's inventory.
	 *
	 * @param cancelOnMoveIn <code>true</code> to cancel the move into
	 *                       view's inventory or <code>false</code> otherwise
	 */
	public final void setCancelOnMoveIn(boolean cancelOnMoveIn) {
		this.cancelOnMoveIn = cancelOnMoveIn;
	}

	/**
	 * If moving items out of the view's inventory is allowed.
	 *
	 * @return If moving items out of the view's inventory is allowed.
	 */
	public final boolean isCancelOnMoveOut() {
		return cancelOnMoveOut;
	}

	/**
	 * Defines whether or not to move items out of the view's inventory.
	 *
	 * @param cancelOnMoveOut <code>true</code> to cancel the move out of the
	 *                        view's inventory or <code>false</code> otherwise
	 */
	public final void setCancelOnMoveOut(boolean cancelOnMoveOut) {
		this.cancelOnMoveOut = cancelOnMoveOut;
	}

	public final boolean isCancelOnShiftClick() {
		return cancelOnShiftClick;
	}

	public final void setCancelOnShiftClick(boolean cancelOnShiftClick) {
		this.cancelOnShiftClick = cancelOnShiftClick;
	}

	public final boolean isClearCursorOnClose() {
		return clearCursorOnClose;
	}

	public final void setClearCursorOnClose(boolean clearCursorOnClose) {
		this.clearCursorOnClose = clearCursorOnClose;
	}

	/**
	 * Whether to close the view's inventory if the player clicks
	 * outside the inventory while it is open.
	 *
	 * @return If inventory should be closed when player clicks outside.
	 */
	public final boolean isCloseOnOutsideClick() {
		return closeOnOutsideClick;
	}

	/**
	 * Defines whether to close the view's inventory for the player when he
	 * clicks outside the view's inventory while the view is open.
	 *
	 * @param closeOnOutsideClick If inventory should be closed
	 *                            when player clicks outside.
	 */
	public final void setCloseOnOutsideClick(boolean closeOnOutsideClick) {
		this.closeOnOutsideClick = closeOnOutsideClick;
	}

	@Override
	public Inventory getInventory() {
		throw new UnsupportedOperationException(
			"View inventory is not accessible"
		);
	}

	private Inventory getInventory(final String title, final int size) {
		return Bukkit.createInventory(this, size, title == null ? this.title : title);
	}

	public final void clearData(@NotNull Player player) {
		data.remove(player);
	}

	public final void clearData(@NotNull Player player, @NotNull String key) {
		if (!data.containsKey(player))
			return;

		data.get(player).remove(key);
	}

	public final Map<String, Object> getData(@NotNull Player player) {
		return data.get(player);
	}

	@SuppressWarnings("unchecked")
	public final <T> T getData(@NotNull Player player, @NotNull String key) {
		if (!data.containsKey(player))
			return null;

		return (T) data.get(player).get(key);
	}

	@SuppressWarnings("unchecked")
	public final <T> T getData(@NotNull Player player, @NotNull String key, @NotNull Supplier<@Nullable T> defaultValue) {
		if (!data.containsKey(player) || !data.get(player).containsKey(key))
			return defaultValue.get();

		return (T) data.get(player).get(key);
	}

	/**
	 * Defines data for the player.
	 *
	 * @param player The player.
	 * @param data   The data.
	 */
	public final void setData(@NotNull Player player, @NotNull Map<String, Object> data) {
		this.data.put(player, data);
	}

	/**
	 * Defines data for the player linked with a key with the given value.
	 *
	 * @param player The player.
	 * @param key    The data key.
	 * @param value  The data value.
	 */
	public final void setData(@NotNull Player player, @NotNull String key, @Nullable Object value) {
		data.computeIfAbsent(player, $ -> new HashMap<>()).put(key, value);
	}

	/**
	 * Check if there is data linked with the specific key for a player.
	 *
	 * @param player The player.
	 * @param key    The data key.
	 * @return If data is set or not.
	 */
	public final boolean hasData(@NotNull Player player, @NotNull String key) {
		if (!data.containsKey(player))
			return false;

		return data.get(player).containsKey(key);
	}

	/**
	 * Called before the inventory is opened to the player.
	 * <p>
	 * This handler is often called "pre-rendering" because it is possible to set
	 * the title and size of the inventory and also cancel the opening of the View
	 * without even doing any handling related to the inventory.
	 * <p>
	 * It is not possible to manipulate the inventory in this handler, if it
	 * happens a exception will be thrown.
	 *
	 * @param context The player view context.
	 */
	protected void onOpen(@NotNull OpenViewContext context) {
	}

	/**
	 * Called when this view is rendered to the player.
	 * <p>
	 * This is where you will define items that will be contained
	 * non-persistently in the context.
	 * <p>
	 * Using {@link View#slot(int)} here will cause a leak of items in memory
	 * or that  the item that was previously defined will be overwritten as
	 * the slot item definition method is for use in the constructor only
	 * once. Instead, you should use the context item definition function
	 * {@link ViewContext#slot(int)}.
	 * <p>
	 * Handlers call order:
	 * <ul>
	 *     <li>{@link #onOpen(OpenViewContext)}</li>
	 *     <li>this rendering function</li>
	 *     <li>{@link #onUpdate(ViewContext)}</li>
	 *     <li>{@link #onClose(ViewContext)}</li>
	 * </ul>
	 * <p>
	 * This is a rendering function and can modify the view's inventory.
	 *
	 * @param context The player view context.
	 */
	protected void onRender(@NotNull ViewContext context) {
	}

	/**
	 * Called when the player closes the view's inventory.
	 * <p>
	 * It is possible to cancel this event and have the view's inventory open
	 * again for the player.
	 *
	 * @param context The player view context.
	 */
	protected void onClose(@NotNull ViewContext context) {
	}

	/**
	 * Called when a player clicks on the view inventory.
	 * <p>
	 * This function is called even if the click has been cancelled,
	 * you can check this using {@link ViewSlotContext#isCancelled()}.
	 * <p>
	 * Canceling the context will cancel the click.
	 * <p>
	 * Handling the inventory in the click handler is not allowed.
	 *
	 * @param context The player view context.
	 */
	protected void onClick(@NotNull ViewSlotContext context) {
	}

	/**
	 * Called when the view is updated for a player.
	 * <p>
	 * This is a rendering function and can modify the view's inventory.
	 *
	 * @param context The player view context.
	 * @see View#update()
	 * @see ViewContext#update()
	 */
	protected void onUpdate(@NotNull ViewContext context) {
	}

	/**
	 * Called when a player moves a view item out of the view's inventory.
	 * <p>
	 * Canceling the context will cancel the move.
	 * Don't confuse moving with dropping.
	 *
	 * @param context The player view context.
	 */
	protected void onMoveOut(@NotNull ViewSlotMoveContext context) {
	}

	protected void onMoveIn(@NotNull ViewSlotMoveContext context) {
	}

	/**
	 * Called when the player holds an item in the inventory.
	 * <p>
	 * This handler will only work if the player manages to successfully hold
	 * the item, for example it will not be called if the click has been
	 * canceled for whatever reasons.
	 * <p>
	 * This context is non-cancelable.
	 *
	 * @param context The player view context.
	 */
	protected void onItemHold(@NotNull ViewSlotContext context) {
	}

	/**
	 * Called when an item is dropped by the player in an inventory
	 * (not necessarily the View's inventory).
	 * <p>
	 * With this it is possible to detect if the player held and released an
	 * item:
	 * <ul>
	 *     <li>inside the view</li>
	 *     <li>outside the view (in the player inventory)</li>
	 *     <li>from inside to outside the view (to the player inventory)</li>
	 *     <li>from outside to inside the view (from the player inventory)</li>
	 * </ul>
	 * <p>
	 * This handler is the counterpart of {@link #onItemHold(ViewSlotContext)}.
	 *
	 * @param from The input context of the move.
	 * @param to   The output context of the move.
	 */
	protected void onItemRelease(
		@NotNull ViewSlotContext from,
		@NotNull ViewSlotContext to
	) {
	}

	/**
	 * Called when a player uses the hot bar key button.
	 * <p>
	 * This context is non-cancelable.
	 *
	 * @param context      The current view context.
	 * @param hotbarButton The interacted hot bar button.
	 */
	protected void onHotbarInteract(
		@NotNull ViewContext context,
		int hotbarButton
	) {

	}

	@Override
	final void throwViewException(
		@NotNull ViewContext context,
		@NotNull Exception exception
	) {
		super.throwViewException(context, exception);

		// propagate error to the global error handler
		if (frame.getErrorHandler() != null)
			frame.getErrorHandler().error(context, exception);
	}

	@Override
	public final ViewErrorHandler getErrorHandler() {
		return super.getErrorHandler();
	}

	@Override
	public final void setErrorHandler(@Nullable ViewErrorHandler errorHandler) {
		super.setErrorHandler(errorHandler);
	}

	private void expandItemsArray(int newLength) {
		ViewItem[] newItems = new ViewItem[newLength];
		System.arraycopy(items, 0, newItems, 0, items.length);

		items = newItems;
	}

	@Override
	public String toString() {
		return "View{" +
			"title='" + title + '\'' +
			", rows=" + rows +
			"} " + super.toString();
	}

}