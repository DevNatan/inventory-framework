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

	private final String title;
	private final int rows;

	private final Map<String, ViewContext> contexts;
	private final Map<Player, Map<String, Object>> data;
	private ViewFrame frame;
	private boolean cancelOnClick, cancelOnPickup, cancelOnDrop, cancelOnDrag, cancelOnClone;
	private boolean cancelOnMoveOut, cancelOnShiftClick, clearCursorOnClose, closeOnOutsideClick;
	private static final Set<String> ENABLED_FEATURES = new HashSet<>();

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
		cancelOnMoveOut = true;
		cancelOnShiftClick = true;
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

	public final String getTitle() {
		return title;
	}

	protected ViewContext createContext(final View view, final Player player, final Inventory inventory) {
		return isScheduledToUpdate()
			? new ScheduledViewContext(view, player, inventory)
			: new ViewContext(view, player, inventory);
	}

	public final void open(final Player player) {
		open(player, null);
	}

	public final void open(final Player player, final Map<String, Object> data) {
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

		onOpen(preOpenContext);
		if (preOpenContext.isCancelled()) {
			clearData(player);
			return;
		}

		int inventorySize = preOpenContext.getInventorySize();
		if (inventorySize != items.length)
			this.expandItemsArray(inventorySize);

		// it is possible to set static items to the context through onOpen.
		if (preOpenContext.getItems().length > 0) {
			// TODO
		}


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

	public final void close() {
		for (final Player player : Sets.newHashSet(getContexts().keySet())) {
			player.closeInventory();
		}
	}

	public final boolean isCancelOnClick() {
		return cancelOnClick;
	}

	public final void setCancelOnClick(final boolean cancelOnClick) {
		this.cancelOnClick = cancelOnClick;
	}

	public final boolean isCancelOnPickup() {
		return cancelOnPickup;
	}

	public final void setCancelOnPickup(final boolean cancelOnPickup) {
		this.cancelOnPickup = cancelOnPickup;
	}

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

	public final boolean isCancelOnClone() {
		return cancelOnClone;
	}

	public final void setCancelOnClone(final boolean cancelOnClone) {
		this.cancelOnClone = cancelOnClone;
	}

	public final boolean isCancelOnMoveOut() {
		return cancelOnMoveOut;
	}

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

	public final boolean isCloseOnOutsideClick() {
		return closeOnOutsideClick;
	}

	public final void setCloseOnOutsideClick(boolean closeOnOutsideClick) {
		this.closeOnOutsideClick = closeOnOutsideClick;
	}

	@Override
	public Inventory getInventory() {
		throw new UnsupportedOperationException();
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

	public final void setData(@NotNull Player player, @NotNull Map<String, Object> data) {
		this.data.put(player, data);
	}

	public final void setData(@NotNull Player player, @NotNull String key, @Nullable Object value) {
		data.computeIfAbsent(player, $ -> new HashMap<>()).put(key, value);
	}

	public final boolean hasData(@NotNull Player player, @NotNull String key) {
		if (!data.containsKey(player))
			return false;

		return data.get(player).containsKey(key);
	}

	/**
	 *
	 *
	 *
	 * @param context The player opening view context.
	 */
	protected void onOpen(@NotNull OpenViewContext context) {
	}

	protected void onRender(@NotNull ViewContext context) {
	}

	protected void onClose(@NotNull ViewContext context) {
	}

	protected void onClick(@NotNull ViewSlotContext context) {
	}

	protected void onUpdate(@NotNull ViewContext context) {
	}

	protected void onMoveOut(@NotNull ViewSlotMoveContext context) {
	}

	protected void onMoveIn(@NotNull ViewSlotMoveContext context) {
	}

	protected void onItemHold(@NotNull ViewSlotContext context) {
	}

	protected void onItemRelease(@NotNull ViewSlotContext from, @NotNull ViewSlotContext to) {
	}

	private void expandItemsArray(int newLength) {
		ViewItem[] newItems = new ViewItem[newLength];
		System.arraycopy(items, 0, newItems, 0, items.length);

		items = newItems;
	}

	public static void enableFeaturePreview(@NotNull String feature) {
		ENABLED_FEATURES.add(feature);
	}

	static boolean isFeatureEnabled(@NotNull String feature) {
		return ENABLED_FEATURES.contains(feature);
	}

	@Override
	public String toString() {
		return "View{" +
			"title='" + title + '\'' +
			", rows=" + rows +
			"} " + super.toString();
	}

}