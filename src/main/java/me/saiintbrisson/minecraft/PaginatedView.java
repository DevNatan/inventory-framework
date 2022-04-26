package me.saiintbrisson.minecraft;

import me.saiintbrisson.minecraft.utils.Paginator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;

import static me.saiintbrisson.minecraft.PaginatedViewContext.FIRST_PAGE;

public abstract class PaginatedView<T> extends View {

	static final char PREVIOUS_PAGE_CHAR = '<';
	static final char NEXT_PAGE_CHAR = '>';
	static final char EMPTY_SLOT_CHAR = 'X';
	static final char ITEM_SLOT_CHAR = 'O';

	private static final int NAVIGATE_LEFT = -1;
	private static final int NAVIGATE_RIGHT = 1;

	private Paginator<T> paginator;
	private int offset, limit;

	public PaginatedView() {
		this(null, 0, "");
	}

	public PaginatedView(int rows) {
		this(null, rows, "");
	}

	public PaginatedView(int rows, String title) {
		this(null, rows, title);
	}

	public PaginatedView(ViewFrame frame, int rows, String title) {
		super(frame, rows, title);
		this.offset = getFirstSlot();
		this.limit = getLastSlot();
	}

	public PaginatedView(int rows, String title, int offset, int limit) {
		this(null, rows, title, offset, limit);
	}

	public PaginatedView(ViewFrame frame, int rows, String title, int offset, int limit) {
		super(frame, rows, title);
		this.offset = offset;
		this.limit = limit;
	}


	/**
	 * @deprecated Use {@link #setSource(List)} instead.
	 */
	@Deprecated
	public void setPaginationSource(List<T> source) {
		setSource(source);
	}

	public void setSource(List<T> source) {
		this.paginator = new Paginator<>(getPageSize(), source);
	}

	public int getPageSize() {
		return limit - offset;
	}

	public Paginator<?> getPaginator() {
		return paginator;
	}

	void setPaginator(Paginator<T> paginator) {
		this.paginator = paginator;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		if (layout != null)
			throw new IllegalArgumentException("Layered views cannot set the offset slot.");

		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		if (layout != null)
			throw new IllegalArgumentException("Layered views cannot set the limit slot.");

		this.limit = limit;
	}

	private ViewItem resolveNavigationItem(
		@NotNull PaginatedViewContext<T> context,
		int direction
	) {
		final ViewFrame frame = getFrame();
		if (frame == null)
			throw new IllegalArgumentException("View frame cannot be null");

		ViewItem item = direction == NAVIGATE_LEFT
			? getPreviousPageItem(context)
			: getNextPageItem(context);
		if (item == null) {
			final Function<PaginatedViewContext<?>, ViewItem> fallback =
				direction == NAVIGATE_LEFT
					? getFrame().getDefaultPreviousPageItem()
					: getFrame().getDefaultNextPageItem();
			if (fallback == null)
				return null;

			item = fallback.apply(context);
		}

		return item;
	}

	private void updateNavigationItem(
		@NotNull PaginatedViewContext<T> context,
		int direction
	) {
		int expectedSlot = getExpectedNavigationItemSlot(context,
			direction);

		ViewItem item = null;
		if (expectedSlot == UNSET_SLOT) {
			// check if item was manually set
			// https://github.com/DevNatan/inventory-framework/issues/43
			item = resolveNavigationItem(context, direction);

			if (item != null && item.getSlot() != UNSET_SLOT) {
				if (direction == NAVIGATE_LEFT)
					context.setPreviousPageItemSlot((expectedSlot = item.getSlot()));
				else
					context.setNextPageItemSlot((expectedSlot = item.getSlot()));
			} else return;
		}

		// try to resolve again if not resolved
		if (item == null)
			item = resolveNavigationItem(context, direction);

		if (item == null) {
			clearSlot(context, expectedSlot);
			return;
		}

		// checking if the item already has a native click handler
		// will make it possible to have custom handlers.
		if (item.getClickHandler() == null)
			item.onClick(ctx -> {
				if (direction == NAVIGATE_LEFT) ctx.paginated().switchToPreviousPage();
				else ctx.paginated().switchToNextPage();
			});

		render(context, item.withCancelOnClick(true), expectedSlot);
	}

	private int getExpectedNavigationItemSlot(
		@NotNull PaginatedViewContext<T> context,
		int direction
	) {
		return direction == NAVIGATE_LEFT ?
			context.getPreviousPageItemSlot() : context.getNextPageItemSlot();
	}

	private void updateNavigation(PaginatedViewContext<T> context) {
		updateNavigationItem(context, NAVIGATE_LEFT);
		updateNavigationItem(context, NAVIGATE_RIGHT);
	}

	public void render(ViewContext context, ViewItem item, int slot) {
		// ensure that the item is available in the virtual context
		context.getItems()[slot] = item;
		super.render(context, item, slot);
	}

	private void clearSlot(ViewContext context, int slot) {
		context.getItems()[slot] = null;
		context.getInventory().setItem(slot, null);
		getFrame().debug("[slot " + slot + " (paginated)]: cleared");
	}

	String[] useLayout(PaginatedViewContext<T> context) {
		return context.getLayout() == null ? this.layout : context.getLayout();
	}

	final void updateContext(PaginatedViewContext<T> context, int page, boolean pageChecking, boolean render) {
		getFrame().debug("[context] paginated update");

		final String[] layout = useLayout(context);
		if (pageChecking) {
			// index check
			if (render && !context.getPaginator().hasPage(page)) {
				getFrame().debug("[context] paginated update - no page " + page + " available");
				getFrame().debug("[context] paginated update - items: " + context.getPaginator().getSource().stream().map(Object::toString).collect(Collectors.joining(", ")));
				getFrame().debug("[context] paginated update - count: " + context.getPaginator().count());
				getFrame().debug("[context] paginated update - page size: " + context.getPaginator().getPageSize());
				return;
			}

			if (layout != null && !context.isCheckedLayerSignature())
				resolveLayout(context, layout, render);

			if (render)
				context.setPage(page);
		}

		if (!render)
			return;

		renderLayout(context, layout, null);
		updateNavigation(context);
	}

	final void updateContext(PaginatedViewContext<T> context, int page, boolean pageChecking) {
		updateContext(context, page, pageChecking, true);
	}

	final void updateContext(PaginatedViewContext<T> context, int page) {
		updateContext(context, page, true);
	}

	private void renderPaginatedItemAt(
		PaginatedViewContext<T> context,
		int index,
		int slot,
		T value,
		ViewItem override
	) {
		ViewItem lastInternalState = resolve(context, slot);
		if (lastInternalState != null && lastInternalState.isPaginationItem())
			lastInternalState = null;

		// overlapping items are those that are already in the inventory but the IF is trying to render them,
		// if it is an overlapped item it means that during the layout cleanup it was detected that they should
		// not have been removed, so they are not removed and during layout rendering they are not re-rendered.
		if (override == null) {
			final ViewItem item = new ViewItem(slot);
			item.setPaginationItem(true);

			final PaginatedViewSlotContext<T> slotContext = new PaginatedViewSlotContext<>(context, index, slot);
			runCatching(context, () -> {
				onPaginationItemRender(slotContext, item, value);
				onItemRender(slotContext, item, value);
			});
			render(slotContext, item, slot);

			item.setLastInternalState(lastInternalState);
			getFrame().debug("--- Set last internal state @ " + slot + " --- ");
			getFrame().debug(Objects.toString(lastInternalState));
			getFrame().debug("rendered paginated item at " + slot + ": " + item);
		} else {
			getFrame().debug("[debug] optimized overriden from layout clear at " + slot + " (index: " + index + ")");

			// we need to reset the initial rendering function of the overlaid item if not, when we get to the rendering
			// stage of the overlaid item, he overlaid item's rendering function will be called first and will render
			// the wrong item.
			override.setUpdateHandler(null);

			// only if there's a fallback item available, clearing it without checking will cause
			// "No item were provided and the rendering function was not defined at slot..."
			if (override.getItem() != null)
				override.setRenderHandler(null);

			override.setSlot(slot);
			context.getItems()[slot] = override;
			override.setLastInternalState(lastInternalState);
		}
	}

	@Override
	protected ViewContext createContext(View view, Player player, Inventory inventory) {
		final PaginatedViewContext<T> context = new PaginatedViewContext<>(this, player, inventory, FIRST_PAGE);
		updateContext(context, FIRST_PAGE, true, false);

		return context;
	}

	private void renderLayoutPattern(PaginatedViewContext<T> context) {
		for (final LayoutPattern pattern : getLayoutPatterns()) {
			for (final int slot : pattern.getSlots()) {
				final ViewItem item = pattern.getFactory().get();
				if (item.getSlot() != UNSET_SLOT)
					throw new IllegalStateException(
						"Items defined through the layout pattern's item factory cannot have a pre-defined slot." +
							"Use `item()` instead of `slot(x)`. " +
							"Expected: " + UNSET_SLOT + ", given: " + item.getSlot()
					);

				item.setSlot(slot);
				render(context, item, slot);
			}
		}
	}

	private void renderLayout(PaginatedViewContext<T> context, String[] layout, ViewItem[] preservedItems) {
		renderLayoutPattern(context);
		final List<T> elements = context.getPaginator().getPage(context.getPage());
		final int size = elements.size();
		getFrame().debug("[context] rendering layout (" + size + " items)");

		final int lastSlot = layout == null ? limit : context.getItemsLayer().peek();
		final int layerSize = getLayerSize(context, layout);

		getFrame().debug("[context]   - last slot: " + lastSlot);
		getFrame().debug("[context]   - layer size: " + layerSize);
		for (int i = 0; i <= lastSlot; i++) {
			if (layout != null && i >= layerSize)
				break;

			final int targetSlot = layout == null ? offset + i : context.getItemsLayer().elementAt(i);
			final ViewItem preserved = preservedItems == null || preservedItems.length <= i ? null : preservedItems[i];
			if (i < size) {
				renderPaginatedItemAt(context, i, targetSlot, elements.get(i), preserved);
			} else {
				final ViewItem item = resolve(context, targetSlot);

				// check if a non-virtual item has been defined in that slot
				if (item != null) {
					if (!item.isPaginationItem()) {
						render(context, item, targetSlot);
						getFrame().debug("cannot override backing non-paginated item while rendering at " + targetSlot);
						continue;
					}

					final ViewItem lastState = item.getLastInternalState();
					if (lastState != null) {
						getFrame().debug("rendering backing @ " + targetSlot + " of " + lastState);
						render(context, lastState, targetSlot);
						continue;
					}
				}

				getFrame().debug("clearing on `renderLayout` @ " + targetSlot);
				clearSlot(context, targetSlot);
			}
		}
	}

	final ViewItem[] clearLayout(PaginatedViewContext<T> context, String[] layout) {
		getFrame().debug("[context] clearing layout");
		final int size = context.getPaginator().getPage(context.getPage()).size();
		final int lastSlot = layout == null ? limit : context.getItemsLayer().peek();
		final int layerSize = getLayerSize(context, layout);

		final ViewItem[] preservedItems = new ViewItem[Math.min(layerSize, size) + 1];
		for (int i = 0; i <= lastSlot; i++) {
			if (layout != null && i >= layerSize)
				break;

			final int targetSlot = layout == null ? offset + i : context.getItemsLayer().elementAt(i);
			if (i < size) {
				final ViewItem preserved = context.getItem(targetSlot);
				getFrame().debug("[clear layout] preserved @ " + targetSlot + ": " + preserved);
				preservedItems[i] = preserved;
			} else {
				final ViewItem item = getItem(targetSlot);
				getFrame().debug("[clear layout] bef clear @ " + targetSlot + ": " + item);
			}

			getFrame().debug("clearing on `clearLayout`");
			clearSlot(context, targetSlot);
		}

		return preservedItems;
	}

	private int getLayerSize(PaginatedViewContext<T> context, String[] layout) {
		return layout == null ? 0 /* ignored */ : context.getItemsLayer().size();
	}

	private void resolveLayout(PaginatedViewContext<T> context, String[] layout) {
		resolveLayout(context, layout, true);
	}

	void resolveLayout(PaginatedViewContext<T> context, String[] layout, boolean render) {
		getFrame().debug("[context] resolving layout (render=" + render + ")");

		// since the layout is only defined once, we cache it
		// to avoid unnecessary processing every time we update the context.
		final int len = layout.length;
		final int columnsLimit = context.getInventory().getSize() / INVENTORY_ROW_SIZE;
		if (len != columnsLimit)
			throw new IllegalArgumentException("Layout columns must respect the size of the inventory (" + len + " != " + columnsLimit + ")");

		context.itemsLayer = new Stack<>();
		for (int row = 0; row < len; row++) {
			final String layer = layout[row];
			if (layer.length() != INVENTORY_ROW_SIZE)
				throw new IllegalArgumentException("The layer located at " + row + " must contain " + INVENTORY_ROW_SIZE + " characters.");

			for (int col = 0; col < INVENTORY_ROW_SIZE; col++) {
				final int targetSlot = col + (row * INVENTORY_ROW_SIZE);
				final char character = layer.charAt(col);
				switch (character) {
					case EMPTY_SLOT_CHAR:
						break;
					case ITEM_SLOT_CHAR: {
						context.itemsLayer.push(targetSlot);
						break;
					}
					case PREVIOUS_PAGE_CHAR: {
						if (render) {
							resolveNavigationItem(context, NAVIGATE_LEFT);
							context.setPreviousPageItemSlot(targetSlot);
						}
						break;
					}
					case NEXT_PAGE_CHAR: {
						if (render) {
							resolveNavigationItem(context, NAVIGATE_RIGHT);
							context.setNextPageItemSlot(targetSlot);
						}
						break;
					}
					default: {
						final LayoutPattern pattern = getLayoutOrNull(character);
						if (pattern != null) {
							pattern.getSlots().push(targetSlot);
						}
					}
				}
			}
		}

		if (!render)
			return;

		context.getPaginator().setPageSize(context.itemsLayer.size());
		context.setCheckedLayerSignature(true);
	}

	void updateLayout(PaginatedViewContext<T> context, String[] layout) {
		getFrame().debug("[context] updating layout");

		// what we will do: first, use the old defined layout to preserve the actual item slot state and then reorder
		// these items with the new slots of the new layout on different positions but with the same preserved state
		final ViewItem[] items = clearLayout(context, useLayout(context));
		resolveLayout(context, layout);
		renderLayout(context, layout, items);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void render(ViewContext context) {
		// render all non-virtual items first
		super.render(context);
		updateContext((PaginatedViewContext<T>) context, FIRST_PAGE);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void update(ViewContext context) {
		// calls global onUpdate
		super.update(context);

		PaginatedViewContext<T> paginated = (PaginatedViewContext<T>) context;
		updateContext(paginated, paginated.getPage(), false /* avoid intensive page checking */);
	}

	public ViewItem getPreviousPageItem(PaginatedViewContext<T> context) {
		return null;
	}

	public ViewItem getNextPageItem(PaginatedViewContext<T> context) {
		return null;
	}

	/**
	 * @deprecated Use {@link #onItemRender(PaginatedViewSlotContext, ViewItem, Object)} instead.
	 */
	@Deprecated
	protected void onPaginationItemRender(
		final PaginatedViewContext<T> context,
		final ViewItem item,
		final T value
	) {
	}


	/**
	 * Called when a paginated item is rendered.
	 *
	 * @param render - the pagination context.
	 * @param item   - the rendered item.
	 * @param value  - the paginated value.
	 */
	protected void onItemRender(
		final PaginatedViewSlotContext<T> render,
		final ViewItem item,
		final T value
	) {
	}

	protected void onPageSwitch(final PaginatedViewContext<T> context) {
	}

}
