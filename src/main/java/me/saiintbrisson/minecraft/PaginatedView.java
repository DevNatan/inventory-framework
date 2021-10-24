package me.saiintbrisson.minecraft;

import me.saiintbrisson.minecraft.utils.Paginator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;

import static me.saiintbrisson.minecraft.PaginatedViewContext.FIRST_PAGE;

public abstract class PaginatedView<T> extends View {

	private static final char PREVIOUS_PAGE_CHAR = '<';
	private static final char NEXT_PAGE_CHAR = '>';
	private static final char EMPTY_SLOT_CHAR = 'X';
	private static final char ITEM_SLOT_CHAR = 'O';
	private static final char FILL_PAGE_CHAR = 'F';

	private Paginator<T> paginator;
	private int offset, limit;
	private Function<PaginatedViewContext<T>, ViewItem> _fillItemFn;

	public PaginatedView() {
		this(null, 3, "");
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

	public void setFillItem(final Function<PaginatedViewContext<T>, ViewItem> fillItemFunction) {
		this._fillItemFn = fillItemFunction;
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

	protected final ViewItem resolvePreviousPageItem(PaginatedViewContext<T> context) {
		final ViewFrame frame = getFrame();
		if (frame == null)
			throw new IllegalArgumentException("View frame cannot be null");

		ViewItem item = getPreviousPageItem(context);
		if (item == null) {
			final Function<PaginatedViewContext<?>, ViewItem> fallback = getFrame().getDefaultPreviousPageItem();
			if (fallback == null)
				return null;

			item = fallback.apply(context);
		}

		return item;
	}

	protected final ViewItem resolveNextPageItem(PaginatedViewContext<T> context) {
		final ViewFrame frame = getFrame();
		if (frame == null)
			throw new IllegalArgumentException("View frame cannot be null");

		ViewItem item = getNextPageItem(context);
		if (item == null) {
			final Function<PaginatedViewContext<?>, ViewItem> fallback = getFrame().getDefaultNextPageItem();
			if (fallback == null)
				return null;

			item = fallback.apply(context);
		}

		return item;
	}

	private void updateNavigationPreviousItem(PaginatedViewContext<T> context) {
		if (context.getPreviousPageItemSlot() == UNSET_SLOT)
			return;

		final ViewItem item = resolvePreviousPageItem(context);
		if (item == null) {
			clearSlot(context, context.getPreviousPageItemSlot());
			return;
		}

		// checking if the item already has a native click handler
		// will make it possible to have custom handlers.
		if (item.getClickHandler() == null)
			item.onClick(ctx -> ctx.paginated().switchToPreviousPage());

		render(context, item.withCancelOnClick(true), context.getPreviousPageItemSlot());
	}

	private void updateNavigationNextItem(PaginatedViewContext<T> context) {
		if (context.getNextPageItemSlot() == UNSET_SLOT)
			return;

		final ViewItem item = resolveNextPageItem(context);
		if (item == null) {
			clearSlot(context, context.getNextPageItemSlot());
			return;
		}

		// checking if the item already has a native click handler
		// will make it possible to have custom handlers.
		if (item.getClickHandler() == null)
			item.onClick(ctx -> ctx.paginated().switchToNextPage());

		render(context, item.withCancelOnClick(true), context.getNextPageItemSlot());
	}

	private void updateNavigation(PaginatedViewContext<T> context) {
		updateNavigationPreviousItem(context);
		updateNavigationNextItem(context);

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

	private String[] useLayout(PaginatedViewContext<T> context) {
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

	private void renderPaginatedItemAt(PaginatedViewContext<T> context, int index, int slot, T value,
									   ViewItem override) {
		final ViewItem item = override == null ? new ViewItem(slot) : override;

		if (override == null) {
			getFrame().debug("[context] render paginated item at " + slot);
			final PaginatedViewSlotContext<T> slotContext = new PaginatedViewSlotContext<>(context, index, slot);
			item.setPaginationItem(true);
			onPaginationItemRender(slotContext, item, value);
			onItemRender(slotContext, item, value);
			render(context, item, slot);
		} else {
			getFrame().debug("[context] render overriden paginated item at " + slot);
			override.setSlot(slot);
			context.getItems()[slot] = override;
		}
	}

	@Override
	protected ViewContext createContext(View view, Player player, Inventory inventory) {
		final PaginatedViewContext<T> context = new PaginatedViewContext<>(this, player, inventory, FIRST_PAGE);
		updateContext(context, FIRST_PAGE, true, false);

		return context;
	}

	private void renderLayout(PaginatedViewContext<T> context, String[] layout, ViewItem[] preservedItems) {
		getFrame().debug("[context] rendering layout");
		final List<T> elements = context.getPaginator().getPage(context.getPage());
		final int size = elements.size();
		getFrame().debug("[context] rendering elements (" + size + "): " + elements);

		final int lastSlot = layout == null ? limit : context.getItemsLayer().peek();
		final int layerSize = layout == null ? 0 /* ignored */ : context.getItemsLayer().size();
		for (int i = 0; i < lastSlot; i++) {
			if (layout != null && i >= layerSize)
				break;

			final int targetSlot = layout == null ? offset + i : context.getItemsLayer().elementAt(i);
			if (i < size) {
				final ViewItem preserved = preservedItems == null || preservedItems.length <= i ? null : preservedItems[i];
				renderPaginatedItemAt(context, i, targetSlot, elements.get(i), preserved);
			} else {
				final ViewItem item = getItem(targetSlot);

				// check if a non-virtual item has been defined in that slot
				if (item != null)
					continue;

				clearSlot(context, targetSlot);
			}
		}

		if (layout == null || context.getFillLayer() == null || _fillItemFn == null)
			return;

		for (final int slot : context.getFillLayer()) {
			final PaginatedViewSlotContext<T> slotFillContext = new PaginatedViewSlotContext<>(context, -1, slot);
			final ViewItem item = _fillItemFn.apply(slotFillContext);
			if (item == null)
				continue;

			item.setSlot(slot);
			render(slotFillContext, item, slot);
		}
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

		for (int row = 0; row < len; row++) {
			final String layer = layout[row];
			if (layer.length() != INVENTORY_ROW_SIZE)
				throw new IllegalArgumentException("The layer located at " + row + " must contain " + INVENTORY_ROW_SIZE + " characters.");

			for (int col = 0; col < INVENTORY_ROW_SIZE; col++) {
				final int targetSlot = col + (row * INVENTORY_ROW_SIZE);
				final char c = layer.charAt(col);
				switch (c) {
					case EMPTY_SLOT_CHAR:
						break;
					case ITEM_SLOT_CHAR: {
						if (context.itemsLayer == null)
							context.itemsLayer = new Stack<>();

						context.itemsLayer.push(targetSlot);
						break;
					}
					case FILL_PAGE_CHAR: {
						if (context.fillLayer == null)
							context.fillLayer = new Stack<>();

						context.fillLayer.push(targetSlot);
						break;
					}
					case PREVIOUS_PAGE_CHAR: {
						if (render) {
							resolvePreviousPageItem(context);
							context.setPreviousPageItemSlot(targetSlot);
						}
						break;
					}
					case NEXT_PAGE_CHAR: {
						if (render) {
							resolveNextPageItem(context);
							context.setNextPageItemSlot(targetSlot);
						}
						break;
					}
					default:
						throw new IllegalArgumentException("Invalid layer character: " + c);
				}
			}
		}

		getFrame().debug("[context] layout resolved with " + context.itemsLayer.size() + " items");
		if (!render)
			return;

		context.getPaginator().setPageSize(context.itemsLayer.size());
		context.setCheckedLayerSignature(true);
	}

	final ViewItem[] clearLayout(PaginatedViewContext<T> context, String[] layout) {
		getFrame().debug("[context] clearing layout");
		final int size = context.getPaginator().getPage(context.getPage()).size();
		final int layerSize = layout == null ? 0 /* ignored */ : context.getItemsLayer().size();
		final ViewItem[] preservedItems = new ViewItem[layerSize + 1];
		for (int i = 0; i < (layout == null ? limit : context.getItemsLayer().peek()); i++) {
			if (layout != null && i >= layerSize)
				break;

			final int targetSlot = layout == null ? offset + i : context.getItemsLayer().elementAt(i);
			final ViewItem item;
			if (i < size)
				preservedItems[i] = context.getItem(targetSlot);
			else {
				item = getItem(targetSlot);
				if (item != null)
					continue;
			}

			clearSlot(context, targetSlot);
		}

		return preservedItems;
	}

	void updateLayout(PaginatedViewContext<T> context, String[] layout) {
		getFrame().debug("[context] updating layout");

        /*
            what we will do: first, use the old defined
            layout to preserve the actual item slot state and then
            reorder this items with the new slots of the new layout
            on different positions but with the same preserved state
         */
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
		// call global onUpdate
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
