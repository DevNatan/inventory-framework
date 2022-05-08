package me.saiintbrisson.minecraft;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static me.saiintbrisson.minecraft.PaginatedView.*;
import static me.saiintbrisson.minecraft.View.INVENTORY_ROW_SIZE;
import static me.saiintbrisson.minecraft.View.UNSET_SLOT;

public class VirtualView {

	protected ViewItem[] items;
	protected String[] layout;
	protected final List<LayoutPattern> layoutPatterns = new ArrayList<>();

	ViewUpdateJob updateJob;
	ViewErrorHandler errorHandler;

	public VirtualView(ViewItem[] items) {
		this.items = items;
	}

	/**
	 * Returns the slot associated with the specified row and column.
	 *
	 * @param row    The rows count.
	 * @param column The columns count.
	 * @return The slot position based in specified row and column.
	 */
	public static int toSlot(int row, int column) {
		return toSlot(row, column, 6 /* inventory rows count */);
	}

	/**
	 * Returns the slot associated with the specified row and column.
	 *
	 * @param row          The rows count.
	 * @param column       The columns count.
	 * @param minRowsCount The minimum rows count.
	 * @return The slot position based in specified row and column.
	 */
	private static int toSlot(int row, int column, int minRowsCount) {
		Preconditions.checkArgument(row <= minRowsCount,
			"Row cannot be greater than " + minRowsCount + " (given: " + row + ")"
		);
		Preconditions.checkArgument(column <= INVENTORY_ROW_SIZE,
			"Column cannot be greater than " + INVENTORY_ROW_SIZE + " (given: " + column + ")"
		);

		return Math.max(row - 1, 0) * INVENTORY_ROW_SIZE + Math.max(column - 1, 0);
	}

	/**
	 * Returns the slot associated with the specified row and column.
	 *
	 * @param row    The rows count.
	 * @param column The columns count.
	 * @return The slot position based in specified row and column.
	 */
	int toSlot0(int row, int column) {
		return toSlot(row, column, getItems().length / INVENTORY_ROW_SIZE);
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
	 *
	 * @param layout the layout.
	 */
	public void setLayout(@Nullable String... layout) {
		this.layout = layout;
	}

	public void setLayout(char character, Supplier<ViewItem> factory) {
		final String name = this instanceof ViewContext ? "context" : "view";
		if (character == EMPTY_SLOT_CHAR
			|| character == ITEM_SLOT_CHAR
			|| character == NEXT_PAGE_CHAR
			|| character == PREVIOUS_PAGE_CHAR
		) throw new IllegalArgumentException(String.format(
			"The \"%c\" character is reserved in the %s layout" +
				" and cannot be used for backwards compatibility.",
			character,
			name
		));

//		if (layoutPatterns.stream().anyMatch(pattern -> pattern.getCharacter() == character))
//			throw new IllegalArgumentException(String.format(
//				"The \"%c\" has already been defined in the %s layout, " +
//					"it is not allowed to define the same pattern more than once.",
//				character,
//				name
//			));

		layoutPatterns.add(new LayoutPattern(character, factory));
	}

	LayoutPattern getLayoutOrNull(char character) {
		return layoutPatterns.stream()
			.filter(pattern -> pattern.getCharacter() == character)
			.findFirst()
			.orElse(null);
	}

	/**
	 * Returns all registered {@link ViewItem}s.
	 */
	ViewItem[] getItems() {
		return items;
	}

	/**
	 * Returns a {@link ViewItem} that is in the specified slot or {@code null} if not defined.
	 *
	 * @param slot the item slot.
	 */
	final ViewItem getItem(int slot) {
		return getItems()[slot];
	}

	/**
	 * Returns the number of the first slot of this view.
	 */
	public final int getFirstSlot() {
		return 0;
	}

	/**
	 * Returns the number of the last slot of this view.
	 */
	public final int getLastSlot() {
		return items.length - 1;
	}

	/**
	 * Returns a new {@link ViewItem}.
	 */
	public final ViewItem item() {
		return new ViewItem(UNSET_SLOT);
	}

	/**
	 * Returns a new {@link ViewItem} with a {@link ItemStack}.
	 *
	 * @param item the item.
	 */
	public final ViewItem item(@NotNull ItemStack item) {
		return new ViewItem(UNSET_SLOT).withItem(item);
	}

	/**
	 * Returns a new {@link ViewItem} with a {@link ItemStack}.
	 *
	 * @deprecated Use {@link #item(ItemStack)} instead.
	 */
	@Deprecated
	public final ViewItem item(@NotNull Material material) {
		return item(new ItemStack(material));
	}

	/**
	 * Returns a new {@link ViewItem} with a {@link ItemStack}.
	 *
	 * @deprecated Use {@link #item(ItemStack)} instead.
	 */
	@Deprecated
	public final ViewItem item(@NotNull Material material, short durability) {
		return item(new ItemStack(material, 1, durability));
	}

	/**
	 * Returns a new {@link ViewItem} with a {@link ItemStack}.
	 *
	 * @deprecated Use {@link #item(ItemStack)} instead.
	 */
	@Deprecated
	public final ViewItem item(@NotNull Material material, int amount) {
		return item(new ItemStack(material, amount));
	}

	/**
	 * Returns a new {@link ViewItem} with a {@link ItemStack}.
	 *
	 * @deprecated Use {@link #item(ItemStack)} instead.
	 */
	@Deprecated
	public final ViewItem item(@NotNull Material material, int amount, short durability) {
		return item(new ItemStack(material, amount, durability));
	}

	/**
	 * Registers a {@link ViewItem} in the specified slot.
	 *
	 * @param slot the item slot.
	 */
	public final ViewItem slot(int slot) {
		inventoryModificationTriggered();
		final int max = getLastSlot() + 1;
		if (slot > max)
			throw new IllegalArgumentException(
				"Slot exceeds the view limit (limit: " + max + ", given: " + slot + ")"
			);

		return items[slot] = new ViewItem(slot);
	}

	/**
	 * Registers a {@link ViewItem} with a {@link ItemStack} in the specified slot.
	 *
	 * @param slot the item slot.
	 * @param item the item to be set.
	 */
	public final ViewItem slot(int slot, ItemStack item) {
		return slot(slot).withItem(item);
	}

	/**
	 * Registers a {@link ViewItem} in the specified row and column.
	 *
	 * @param row    the item slot row.
	 * @param column the item slot column.
	 */
	public final ViewItem slot(int row, int column) {
		return slot(toSlot0(row, column));
	}

	/**
	 * Registers a {@link ViewItem} with a {@link ItemStack} in the specified row and column.
	 *
	 * @param row    the item slot row.
	 * @param column the item slot column.
	 * @param item   the item to be set.
	 */
	public final ViewItem slot(int row, int column, ItemStack item) {
		return slot(row, column).withItem(item);
	}

	/**
	 * Registers a {@link ViewItem} in the first slot.
	 *
	 * @see #getFirstSlot()
	 */
	public final ViewItem firstSlot() {
		return slot(getFirstSlot());
	}

	/**
	 * Registers a {@link ViewItem} with a {@link ItemStack} in the first slot.
	 *
	 * @param item the item to be set.
	 * @see #getFirstSlot()
	 */
	public final ViewItem firstSlot(ItemStack item) {
		return slot(getFirstSlot(), item);
	}

	/**
	 * Registers a {@link ViewItem} in the last slot.
	 *
	 * @see #getLastSlot()
	 */
	public final ViewItem lastSlot() {
		return slot(getLastSlot());
	}

	/**
	 * Registers a {@link ViewItem} with a {@link ItemStack} in the last slot.
	 *
	 * @param item the item to be set.
	 * @see #getLastSlot()
	 */
	public final ViewItem lastSlot(ItemStack item) {
		return slot(getLastSlot(), item);
	}

	/**
	 * Render all items in this view.
	 */
	public void render() {
		inventoryModificationTriggered();
	}

	/**
	 * Render all items in this view to the specified context.
	 *
	 * @param context the target context.
	 */
	public void render(ViewContext context) {
		Preconditions.checkNotNull(context, "Context cannot be null.");
		inventoryModificationTriggered();

		for (int i = 0; i < items.length; i++) {
			render(context, i);
		}
	}

	public void render(ViewContext context, int slot) {
		final ViewItem item = context.getView().resolve(context, slot);
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

			runCatching(context, () -> item.getRenderHandler().handle(render));
			item.setLinkedContext(render);
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
	 * Updates this view for all viewers who's viewing it.
	 */
	public void update() {
		inventoryModificationTriggered();
	}

	/**
	 * Updates the specified {@link ViewContext} according to this view.
	 *
	 * @param context the target context.
	 */
	public void update(ViewContext context) {
		inventoryModificationTriggered();
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

		final ViewItem item = context.getView().resolve(context, slot);
		if (item == null) {
			context.getInventory().setItem(slot, null);
			return;
		}

		if (item.getUpdateHandler() != null) {
			final ViewSlotContext update = context instanceof ViewSlotContext ?
				(ViewSlotContext) context :
				new DelegatedViewContext(context, slot, item.getItem());

			runCatching(context, () -> item.getUpdateHandler().handle(update));
			if (update.hasChanged()) {
				render(update, item, slot);
				update.setChanged(false);
			}
			return;
		}

		if (context instanceof ViewSlotContext) {
			final ViewSlotContext slotContext = (ViewSlotContext) context;

			// when using #updateSlot() inside a onClick
			if (item.getRenderHandler() != null) {
				runCatching(context, () -> item.getRenderHandler().handle(slotContext));
			}

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

		// fast path -- ArrayIndexOutOfBoundsException
		if (slot > items.length)
			return null;

		final ViewItem item = items[slot];
		if (item == null)
			return context.getItem(slot);

		return item;
	}

	/**
	 * Defines the automatic update interval time for this view.
	 *
	 * @param interval The ticks to wait between runs.
	 */
	protected final void scheduleUpdate(long interval) {
		scheduleUpdate(-1, interval);
	}

	/**
	 * Defines the automatic update interval time for this view.
	 *
	 * @param delay    The ticks to wait before running the task.
	 * @param interval The ticks to wait between runs.
	 */
	protected final void scheduleUpdate(long delay, long interval) {
		inventoryModificationTriggered();
		// initialize only when needed
		if (updateJob == null) {
			updateJob = new ViewUpdateJob(this, delay, interval);
			return;
		}

		// fast path -- do not schedule if delay and interval are the same
		if (updateJob.delay == delay && updateJob.interval == interval)
			return;

		// cancel the old update job to prevent leaks and
		// schedule again with the new delay and interval values
		updateJob.cancel();
		scheduleUpdate(delay, interval);
	}

	/**
	 * Checks if this view is set to update automatically.
	 *
	 * @return <code>true</code> if it will update automatically or <code>false</code> otherwise.
	 */
	public final boolean isScheduledToUpdate() {
		return updateJob != null;
	}

	/**
	 * Gets the error handler for this virtual view.
	 *
	 * @return The ViewErrorHandler for this view.
	 */
	public ViewErrorHandler getErrorHandler() {
		return errorHandler;
	}

	/**
	 * Defines the error handler for this virtual view.
	 * <p>
	 * Setting specific error handling for a {@link ViewContext} will cause the
	 * error to be propagated to the {@link View} as well if it has been set.
	 *
	 * @param errorHandler The View Error Handler for this view.
	 *                     Use <code>null</code> to remove it.
	 */
	public void setErrorHandler(@Nullable ViewErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	/**
	 * Throws an exception to the error handler if one has been defined.
	 *
	 * @param context   The current view context.
	 * @param exception The caught exception.
	 */
	void throwViewException(
		@NotNull ViewContext context,
		@NotNull Exception exception
	) {
		// re-throw exception if error handler is not defined
		if (getErrorHandler() == null)
			return;

		getErrorHandler().error(context, exception);
	}

	void runCatching(
		@NotNull ViewContext context,
		Runnable fn
	) {
		if (getErrorHandler() != null) {
			try {
				fn.run();
			} catch (final Exception e) {
				throwViewException(context, e);
			}
			return;
		}

		// unhandled exception
		fn.run();
	}

	/**
	 * Returns an item whose reference key is the same as the specified key.
	 *
	 * @param referenceKey The item reference key.
	 * @return The slot context linked to the referenced item, returning null if the item is not found.
	 * @throws IllegalStateException If the item was not yet rendered.
	 */
	public @Nullable ViewSlotContext ref(@NotNull String referenceKey) {
		final ViewItem ref = Arrays.stream(getItems())
			.filter(item -> item.getReference().equals(referenceKey))
			.findFirst()
			.orElse(null);

		if (ref == null) return null;
		return ref.getLinkedContext();
	}

	/**
	 * Called when a modification is triggered in this inventory view.
	 */
	protected void inventoryModificationTriggered() {
	}

	final List<LayoutPattern> getLayoutPatterns() {
		return layoutPatterns;
	}

	@Override
	public String toString() {
		return "VirtualView{" +
			"items=" + Arrays.stream(items).filter(Objects::nonNull)
			.map(ViewItem::toString)
			.collect(Collectors.joining()) + "}";
	}

}
