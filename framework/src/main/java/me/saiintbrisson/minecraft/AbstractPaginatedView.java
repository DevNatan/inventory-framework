package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Getter
@ToString(callSuper = true)
public abstract class AbstractPaginatedView<T> extends AbstractView
	implements PaginatedVirtualView<T> {

	static final byte NAVIGATE_LEFT = 0, NAVIGATE_RIGHT = 1;

	private BiConsumer<PaginatedViewContext<T>, ViewItem> previousPageItemFactory, nextPageItemFactory;

	@ToString.Exclude
	private final SharedPaginationProperties<T> properties = new SharedPaginationProperties<>(this);

	AbstractPaginatedView(int rows, String title, @NotNull ViewType type) {
		super(rows, title, type);
	}

	/**
	 * TODO document it properly
	 * <p>
	 * You can use: item.withItem(...);
	 *
	 * @param renderContext The pagination item rendering context.
	 * @param viewItem      A mutable instance of item that will be rendered.
	 * @param value         The paginated value.
	 */
	protected abstract void onItemRender(
		@NotNull PaginatedViewSlotContext<T> renderContext,
		@NotNull ViewItem viewItem,
		@NotNull T value
	);

	/**
	 * Called when pagination is switched.
	 * <p>
	 * The context in the parameter is the new paging context, so trying to
	 * {@link PaginatedViewContext#getPage() get the page} will return the
	 * new page and not the current one.
	 *
	 * @param context The pagination context.
	 */
	protected void onPageSwitch(@NotNull PaginatedViewContext<T> context) {
	}

	@Override
	public final void setLayout(String... layout) {
		ensureNotInitialized();
		if (getOffset() != -1 || getLimit() != -1)
			throw new IllegalStateException(
				"Layout, offset and limit slot cannot be used together."
			);

		getProperties().setLayout(layout);
	}

	@Override
	public final void setLayout(char identifier, @NotNull Supplier<ViewItem> layout) {
		ensureNotInitialized();
		if (getOffset() != -1 || getLimit() != -1)
			throw new IllegalStateException(
				"Layout, offset and limit slot cannot be used together."
			);

		getProperties().setLayout(identifier, layout);
	}

	@Override
	public final void setLayout(char identifier, @NotNull Consumer<ViewItem> layout) {
		setLayout(identifier, () -> {
			final ViewItem item = new ViewItem();
			layout.accept(item);
			return item;
		});
	}

	final String[] useLayout(@NotNull PaginatedViewContext<T> context) {
		return context.getProperties().getLayout() == null
			? getProperties().getLayout() :
			context.getProperties().getLayout();
	}

	public final int getOffset() {
		return getProperties().getOffset();
	}

	@Deprecated
	public final void setOffset(int offset) {
		ensureNotInitialized();
		getProperties().setOffset(offset);
	}

	private int useOffset(@NotNull PaginatedViewContext<T> context) {
		return getOffset() == -1 ? context.getOffset() : getOffset();
	}

	public final int getLimit() {
		return getProperties().getLimit();
	}

	@Deprecated
	public final void setLimit(int limit) {
		ensureNotInitialized();
		getProperties().setLimit(limit);
	}

	private int useLimit(@NotNull PaginatedViewContext<T> context) {
		return getLimit() == -1 ? context.getLimit() : getLimit();
	}

	public final void setSource(@NotNull List<T> source) {
		ensureNotInitialized();
		getProperties().setPaginator(new Paginator<>(getPageSize(), source));
	}
	@Override
	public final void setSource(@NotNull Function<PaginatedViewContext<T>, List<T>> sourceProvider) {
		ensureNotInitialized();
		getProperties().setPaginator(new Paginator<>(getPageSize(), sourceProvider));
	}

	@Override
	public final int getPageItemsCount() {
		return getProperties().getPaginator().getPageSize();
	}

	@Override
	public final int getPageSize() {
		return getLimit() - getOffset();
	}

	public final void setPreviousPageItem(@NotNull BiConsumer<PaginatedViewContext<T>, ViewItem> previousPageItemFactory) {
		ensureNotInitialized();
		this.previousPageItemFactory = previousPageItemFactory;
	}

	public final void setNextPageItem(@NotNull BiConsumer<PaginatedViewContext<T>, ViewItem> nextPageItemFactory) {
		ensureNotInitialized();
		this.nextPageItemFactory = nextPageItemFactory;
	}

	/**
	 * @deprecated Use {@link #setPreviousPageItem(BiConsumer)} on constructor instead.
	 */
	@Deprecated
	protected ViewItem getPreviousPageItem(@NotNull PaginatedViewContext<T> context) {
		return null;
	}

	/**
	 * @deprecated Use {@link #setNextPageItem(BiConsumer)} on constructor instead.
	 */
	@Deprecated
	protected ViewItem getNextPageItem(@NotNull PaginatedViewContext<T> context) {
		return null;
	}

	@Override
	final void render(@NotNull ViewContext context) {
		if (getType() != ViewType.CHEST)
			throw new IllegalStateException(String.format(
				"Pagination is not supported in \"%s\" view type: %s." +
					" Use chest type instead.",
				getType().getIdentifier(),
				getClass().getName()
			));

		final SharedPaginationProperties<T> properties = context.<T>paginated().getProperties();
		if (!properties.hasSource())
			throw new IllegalStateException(
				"At least one pagination source must be set. " +
					"Use #setSource in the PaginatedView constructor or set just to a context" +
					" in the #onRender(...) function with \"render.paginated().setSource(...)\"."
			);

		super.render(context);
		properties.updateContext(context.paginated(), 0, true, false);
	}

	@Override
	void update(@NotNull ViewContext context) {
		super.update(context);

		final PaginatedViewContext<T> paginated = context.paginated();
		paginated.getProperties().updateContext(
			paginated,
			paginated.getPage(),
			false /* avoid intensive page checking */,
			true
		);
	}

	private void renderPaginatedItemAt(
		@NotNull PaginatedViewContext<T> context,
		int index,
		int slot,
		@NotNull T value,
		@Nullable ViewItem override
	) {
		// TODO replace this with a more sophisticated overlay detection
		ViewItem overlay = context.resolve(slot, true);
		if (overlay != null && overlay.isPaginationItem())
			overlay = null;

		// overlapping items are those that are already in the inventory but the IF is trying to render them,
		// if it is an overlapped item it means that during the layout cleanup it was detected that they should
		// not have been removed, so they are not removed and during layout rendering they are not re-rendered.
		if (override == null) {
			final ViewItem item = new ViewItem(slot);
			item.setPaginationItem(true);

			@SuppressWarnings("unchecked") final PaginatedViewSlotContext<T> slotContext =
				(PaginatedViewSlotContext<T>) PlatformUtils.getFactory().createSlotContext(
					item,
					(BaseViewContext) context,
					index,
					value
				);

			runCatching(context, () -> {
				onItemRender(slotContext, item, value);
			});
			render(slotContext, item, slot);
			item.setOverlay(overlay);
		} else {
			// we need to reset the initial rendering function of the overlaid item if not, when we
			// get to the rendering stage of the overlaid item, he overlaid item's rendering function
			// will be called first and will render the wrong item
			override.setUpdateHandler(null);

			// only if there's a fallback item available, clearing it without checking will cause
			// "No item were provided and the rendering function was not defined at slot..."
			if (override.getItem() != null)
				override.setRenderHandler(null);

			override.setSlot(slot);
			override.setOverlay(overlay);
			((BasePaginatedViewContext<T>) context).getItems()[slot] = override;
		}
	}

	void renderLayout(
		@NotNull PaginatedViewContext<T> context,
		String[] layout,
		ViewItem[] preservedItems
	) {
		renderUserDefinedLayout(context, getProperties());

		final SharedPaginationProperties<T> ctxProps = context.getProperties();
		renderUserDefinedLayout(context, ctxProps);

		final List<T> elements = ctxProps.getPaginator().getPageBlocking(context.getPage());
		final int elementsCount = elements.size();
		final int lastSlot = layout == null ? getLimit() : ctxProps
			.getItemsLayoutPattern()
			.getSlots()
			.peek();
		final int layerSize = getLayerSize(context, layout);
		final int offset = useOffset(context);

		System.out.println("rendering layout");
		System.out.println("page size = " + ctxProps.getPaginator().getPageSize());
		System.out.println("elements count: " + elementsCount);
		System.out.println("offset = " + offset + ", limit = " + getLimit());
		System.out.println("layer size = " + layerSize);
		System.out.println("last slot = " + lastSlot);

		for (int i = 0; i <= lastSlot; i++) {
			if (layout != null && i >= layerSize)
				break;

			final int targetSlot = layout == null ? offset + i : context.getProperties()
				.getItemsLayoutPattern().getSlots().elementAt(i);
			final ViewItem preserved = preservedItems == null || preservedItems.length <= i ? null : preservedItems[i];
			if (i < elementsCount)
				renderPaginatedItemAt(context, i, targetSlot, elements.get(i), preserved);
			else {
				final ViewItem item = context.resolve(targetSlot, true);
				// check if a non-virtual item has been defined in that slot
				if (item != null) {
					if (!item.isPaginationItem()) {
						render(context, item, targetSlot);
						continue;
					}

					final ViewItem overlay = item.getOverlay();
					if (overlay != null) {
						render(context, overlay, targetSlot);
						continue;
					}
				}

				removeAt(context, targetSlot);
			}
		}
	}

	private void renderUserDefinedLayout(
		@NotNull PaginatedViewContext<T> context,
		@NotNull SharedPaginationProperties<T> properties
	) {
		for (final LayoutPattern pattern : properties.getCustomLayoutPatterns()) {
			for (final int slot : pattern.getSlots()) {
				final ViewItem item = pattern.getFactory().get();

				// pattern slot must be unset
				if (item.getSlot() != -1)
					throw new IllegalStateException(String.format(
						"Items defined through the layout pattern's item factory cannot have a " +
							"pre-defined slot. Use `item()` instead of `slot(x)`. " +
							"Expected: *unset slot*, given: %s",
						item.getSlot()
					));

				item.setSlot(slot);
				render(context, item, slot);
			}
		}
	}

	private void updateLayout(
		@NotNull PaginatedViewContext<T> context,
		String[] layout
	) {
		// what we will do: first, use the old defined layout to preserve the actual item slot state and then reorder
		// these items with the new slots of the new layout on different positions but with the same preserved state
		final ViewItem[] items = clearLayout(context, useLayout(context));
		context.getProperties().resolveLayout(context, layout, true);
		renderLayout(context, layout, items);
	}

	private ViewItem[] clearLayout(
		@NotNull PaginatedViewContext<T> context,
		String[] layout
	) {
		final int elementsCount = context.getProperties()
			.getPaginator()
			.getPageBlocking(context.getPage())
			.size();
		final int lastSlot = layout == null ? useLimit(context) : context.getProperties()
			.getItemsLayoutPattern()
			.getSlots()
			.peek();
		final int layerSize = getLayerSize(context, layout);
		final int offset = useOffset(context);

		final ViewItem[] preservedItems = new ViewItem[Math.min(layerSize, elementsCount) + 1];
		for (int i = 0; i <= lastSlot; i++) {
			if (layout != null && i >= layerSize)
				break;

			final int targetSlot = layout == null ? offset + i : context.getProperties()
				.getItemsLayoutPattern()
				.getSlots()
				.elementAt(i);
			if (i < elementsCount) {
				final ViewItem preserved = context.getItem(targetSlot);
				preservedItems[i] = preserved;
			}

			removeAt(context, targetSlot);
		}

		return preservedItems;
	}

	void removeAt(@NotNull ViewContext context, int slot) {
		context.clear(slot);
		context.getContainer().removeItem(slot);
	}

	void updateLayout(@NotNull final BasePaginatedViewContext<T> context, final String[] layout) {
		throw new UnsupportedOperationException();
	}

	void updatePage(@NotNull final BasePaginatedViewContext<T> context, final int page) {
		throw new UnsupportedOperationException();
	}

	private int getLayerSize(@NotNull PaginatedViewContext<T> context, String[] layout) {
		return layout == null ? 0 /* ignored */ : context.getProperties()
			.getItemsLayoutPattern()
			.getSlots()
			.size();
	}

	ViewItem internalGetPreviousPageItem(@NotNull PaginatedViewContext<T> context) {
		final AbstractPaginatedView<T> root = context.getRoot().paginated();
		if (root.getPreviousPageItemFactory() == null)
			return root.getPreviousPageItem(context);

		final ViewItem item = new ViewItem();
		root.getPreviousPageItemFactory().accept(context, item);
		return item;
	}

	ViewItem internalGetNextPageItem(@NotNull PaginatedViewContext<T> context) {
		final AbstractPaginatedView<T> root = context.getRoot().paginated();
		if (root.getNextPageItemFactory() == null)
			return root.getNextPageItem(context);

		final ViewItem item = new ViewItem();
		root.getNextPageItemFactory().accept(context, item);
		return item;
	}

	ViewItem resolveNavigationItem(
		@NotNull PaginatedViewContext<T> context,
		@Range(from = NAVIGATE_LEFT, to = NAVIGATE_RIGHT) int direction
	) {
		final AbstractPaginatedView<T> root = context.getRoot();
		final ViewItem item = direction == NAVIGATE_LEFT
			? root.internalGetPreviousPageItem(context)
			: root.internalGetNextPageItem(context);

		if (item != null)
			return item;

		final PlatformViewFrame<?, ?, ?> vf = context.getRoot().getViewFrame();
		if (vf == null)
			return null;

		final Function<PaginatedViewContext<?>, ViewItem> fallback =
			direction == NAVIGATE_LEFT
				? vf.getDefaultPreviousPageItem()
				: vf.getDefaultNextPageItem();

		if (fallback == null)
			return null;

		return fallback.apply(context);
	}

}
