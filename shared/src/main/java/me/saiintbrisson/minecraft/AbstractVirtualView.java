package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class AbstractVirtualView implements VirtualView {

	static final char LAYOUT_PREVIOUS_PAGE = '<';
	static final char LAYOUT_NEXT_PAGE = '>';
	static final char LAYOUT_EMPTY_SLOT = 'X';
	static final char LAYOUT_FILLED_SLOT = 'O';

	@ToString.Exclude
	private ViewItem[] items;

	private ViewErrorHandler errorHandler;
	private ViewUpdateJob updateJob;
	private final List<LayoutPattern> layoutPatterns = new ArrayList<>();
	private String[] layout;

	protected ViewItem[] getItems() {
		return items;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ViewItem getItem(int index) {
		return items[index];
	}

	final void setItems(ViewItem[] items) {
		this.items = items;
	}

	/**
	 * {@inheritDoc}
	 */
	public final ViewErrorHandler getErrorHandler() {
		return errorHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void setErrorHandler(ViewErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getFirstSlot() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getLastSlot() {
		return items.length - 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
	public final ViewItem item() {
		return new ViewItem();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
	public final ViewItem item(@NotNull ItemStack item) {
		return new ViewItem().withItem(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
	public final ViewItem item(@NotNull Material material) {
		return new ViewItem().withItem(new ItemStack(material));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
	public final ViewItem item(@NotNull Material material, int amount) {
		return new ViewItem().withItem(new ItemStack(material, amount));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
	public final ViewItem item(@NotNull Material material, short durability) {
		return new ViewItem().withItem(new ItemStack(material, 1, durability));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
	public final ViewItem item(@NotNull Material material, int amount, short durability) {
		return new ViewItem().withItem(new ItemStack(material, amount, durability));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@NotNull
	public final ViewItem slot(int slot) {
		inventoryModificationTriggered();
		if (getItems() == null)
			throw new IllegalStateException("VirtualView was not initialized yet");

		final ViewItem item = new ViewItem(slot);
		getItems()[slot] = item;
		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@NotNull
	public final ViewItem slot(int slot, Object item) {
		return slot(slot).withItem(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@NotNull
	public final ViewItem slot(int row, int column) {
		return slot(convertSlot(row, column), null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@NotNull
	public final ViewItem slot(int row, int column, Object item) {
		return slot(convertSlot(row, column), item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final @NotNull ViewItem firstSlot() {
		return slot(getFirstSlot());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final @NotNull ViewItem firstSlot(Object item) {
		return slot(getFirstSlot(), item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final @NotNull ViewItem lastSlot() {
		return slot(getLastSlot());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final @NotNull ViewItem lastSlot(Object item) {
		return slot(getLastSlot(), item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public @NotNull ViewItem availableSlot() {
		return availableSlot(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public @NotNull ViewItem availableSlot(Object item) {
		return slot(getNextAvailableSlot(), item);
	}

	/**
	 * Determines the next available slot.
	 *
	 * @return The next available slot.
	 * @throws IllegalStateException If there's no slot available.
	 */
	int getNextAvailableSlot() {
		for (int i = 0; i < getItems().length; i++) {
			final ViewItem item = items[i];
			if (item == null) return i;
		}

		return throwNoSlotAvailable();
	}

	protected final int throwNoSlotAvailable() {
		throw new IllegalStateException("No slot available");
	}

	void render(@NotNull ViewContext context) {
		for (int i = 0; i < getItems().length; i++) {
			render(context, i);
		}
	}

	protected final void render(@NotNull ViewContext context, int slot) {
		final ViewItem item = context.resolve(slot, true);
		if (item == null) return;

		render(context, item, slot);
	}

	protected final void render(@NotNull ViewContext context, @NotNull ViewItem item, int slot) {
		inventoryModificationTriggered();

		final Object fallbackItem = item.getItem();

		if (item.getRenderHandler() != null) {
			final ViewSlotContext renderContext =
				PlatformUtils.getFactory().createSlotContext(item, (BaseViewContext) context, 0, null);

			runCatching(context, () -> item.getRenderHandler().handle(renderContext));
			if (renderContext.hasChanged()) {
				context.getContainer().renderItem(slot, unwrap(renderContext.getItemWrapper()));
				renderContext.setChanged(false);
				return;
			}
		}

		if (fallbackItem == null)
			throw new IllegalArgumentException(String.format(
				"No item were provided and the rendering function was not defined at slot %d."
					+ "You must use a rendering function #slot(...).onRender(...)"
					+ " or a fallback item #slot(fallbackItem)",
				slot));

		context.getContainer().renderItem(slot, unwrap(fallbackItem));
	}

	private Object unwrap(Object item) {
		if (item instanceof ItemWrapper) return unwrap(((ItemWrapper) item).getValue());

		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update() {
		throw new UnsupportedOperationException("Update aren't supported in this view");
	}

	void update(@NotNull ViewContext context) {
		for (int i = 0; i < getItems().length; i++) update(context, i);
	}

	final void update(@NotNull ViewContext context, int slot) {
		inventoryModificationTriggered();

		final ViewItem item = context.resolve(slot, true);
		if (item == null) {
			context.getContainer().removeItem(slot);
			return;
		}

		update(context, item, slot);
	}

	final void update(@NotNull ViewContext context, ViewItem item, int slot) {
		inventoryModificationTriggered();

		if (item.getUpdateHandler() != null) {
			final ViewSlotContext updateContext =
				PlatformUtils.getFactory().createSlotContext(item, (BaseViewContext) context, 0, null);

			runCatching(context, () -> item.getUpdateHandler().handle(updateContext));
			if (updateContext.hasChanged()) {
				context.getContainer().renderItem(slot, unwrap(updateContext.getItemWrapper()));
				updateContext.setChanged(false);
				return;
			}
		}

		// update handler can be used as a empty function, so we fall back to the render handler to
		// update the fallback item properly
		render(context, item, slot);
	}

	/**
	 * {@inheritDoc}
	 */
	@ApiStatus.Internal
	ViewItem resolve(int index) {
		// fast path -- skip -999 index on some platforms
		if (index < 0) return null;

		final int len = getItems().length;
		if (index >= len) return null;

		return getItems()[index];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void clear(int slot) {
		getItems()[slot] = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ViewUpdateJob getUpdateJob() {
		return updateJob;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setUpdateJob(ViewUpdateJob updateJob) {
		this.updateJob = updateJob;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void scheduleUpdate(long intervalInTicks) {
		scheduleUpdate(-1, intervalInTicks);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scheduleUpdate(long delayInTicks, long intervalInTicks) {
		inventoryModificationTriggered();
		PlatformUtils.getFactory().scheduleUpdate(this, delayInTicks, intervalInTicks);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void scheduleUpdate(@NotNull Duration duration) {
		scheduleUpdate(-1, Math.floorDiv(duration.getSeconds(), 20));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isScheduledToUpdate() {
		return updateJob != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@ApiStatus.Internal
	@Override
	public void inventoryModificationTriggered() {
	}

	final void runCatching(final ViewContext context, @NotNull final Runnable runnable) {
		if (context != null && context.getErrorHandler() != null) {
			tryRunOrFail(context, runnable);
			return;
		}

		if (getErrorHandler() == null) {
			runnable.run();
			return;
		}

		tryRunOrFail(context, runnable);
	}

	boolean throwException(final ViewContext context, @NotNull final Exception exception) {
		if (context != null && context.getErrorHandler() != null) {
			context.getErrorHandler().error(context, exception);
			if (!context.isPropagateErrors()) return false;
		}

		launchError(getErrorHandler(), context, exception);
		return true;
	}

	protected final void launchError(
		final ViewErrorHandler errorHandler, final ViewContext context, @NotNull final Exception exception) {
		if (errorHandler == null) return;

		errorHandler.error(context, exception);
	}

	private void tryRunOrFail(final ViewContext context, @NotNull final Runnable runnable) {
		try {
			runnable.run();
		} catch (final Exception e) {
			throwException(context, e);
		}
	}

	/**
	 * Returns the slot associated with the specified row and column.
	 *
	 * @param row    The rows count.
	 * @param column The columns count.
	 * @return The slot position based in specified row and column.
	 */
	int convertSlot(int row, int column) {
		throw new IllegalArgumentException("Slot conversion not supported");
	}

	protected final int convertSlot(int row, int column, int maxRowsCount, int maxColumnsCount) {
		if (row > maxRowsCount)
			throw new IllegalArgumentException(
				String.format("Row cannot be greater than %d (given %d)", maxRowsCount, row));

		if (column > maxColumnsCount)
			throw new IllegalArgumentException(
				String.format("Column cannot be greater than %d (given %d)", maxColumnsCount, column));

		return Math.max(row - 1, 0) * maxColumnsCount + Math.max(column - 1, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@ApiStatus.Internal
	public final List<LayoutPattern> getLayoutPatterns() {
		return layoutPatterns;
	}

	@Override
	@ApiStatus.Internal
	public final String[] getLayout() {
		return layout;
	}

	/**
	 * {@inheritDoc}
	 **/
	@Override
	public void setLayout(@Nullable String... layout) {
		this.layout = layout;
	}

	/**
	 * {@inheritDoc}
	 **/
	@Override
	public void setLayout(char character, @Nullable Supplier<ViewItem> factory) {
		checkReservedLayoutCharacter(character);
		if (factory == null) {
			layoutPatterns.removeIf(pattern -> pattern.getCharacter() == character);
			return;
		}

		layoutPatterns.add(new LayoutPattern(character, factory));
	}

	/**
	 * {@inheritDoc}
	 **/
	@Override
	public void setLayout(char identifier, @Nullable Consumer<ViewItem> layout) {
		setLayout(identifier, () -> {
			final ViewItem item = new ViewItem();
			Objects.requireNonNull(
				layout,
				"Layout pattern consumer cannot be null"
			).accept(item);
			return item;
		});
	}

	void checkReservedLayoutCharacter(char character) {
		if (character == LAYOUT_EMPTY_SLOT
			|| character == LAYOUT_FILLED_SLOT
			|| character == LAYOUT_PREVIOUS_PAGE
			|| character == LAYOUT_NEXT_PAGE)
			throw new IllegalArgumentException(String.format(
				"The \"%c\" character is reserved in layouts and cannot be used due to backwards compatibility.",
				character));
	}

}
