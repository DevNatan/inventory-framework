package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.ToString;
import me.saiintbrisson.minecraft.exception.InitializationException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Getter
@ToString(callSuper = true)
public abstract class AbstractPaginatedView<T> extends AbstractView implements PaginatedVirtualView<T> {

	/* inherited from PaginatedVirtualView */
	private BiConsumer<PaginatedViewContext<T>, ViewItem> previousPageItemFactory, nextPageItemFactory;
	private Paginator<T> paginator;

	private int offset, limit;

	AbstractPaginatedView(int rows, String title, @NotNull ViewType type) {
		super(rows, title, type);
		this.offset = 0;
		this.limit = getItems().length - 1;
	}

	/**
	 * Where pagination will start.
	 *
	 * @return The first slot that pagination can reach in the container.
	 * @deprecated Offset and limit will be replaced by layout.
	 */
	@Deprecated
	public final int getOffset() {
		return offset;
	}

	/**
	 * Defines the slot that pagination will start in the container.
	 *
	 * @param offset Where pagination will start.
	 * @throws InitializationException If this view is initialized.
	 * @deprecated Offset and limit will be replaced by layout.
	 */
	@Deprecated
	public final void setOffset(int offset) throws InitializationException {
		ensureNotInitialized();
		this.offset = offset;
	}

	/**
	 * Where pagination will end.
	 *
	 * @return The last slot that pagination can reach in the container.
	 * @deprecated Offset and limit will be replaced by layout.
	 */
	@Deprecated
	public final int getLimit() {
		return limit;
	}

	/**
	 * Defines the last slot that pagination can reach in the container.
	 *
	 * @param limit Where pagination will end.
	 * @throws InitializationException If this view is initialized.
	 * @deprecated Offset and limit will be replaced by layout.
	 */
	@Deprecated
	public final void setLimit(int limit) throws InitializationException {
		ensureNotInitialized();
		this.limit = limit;
	}

	/**
	 * Called when a single pagination data is about to be rendered in the container.
	 *
	 * <p>The {@link ViewItem item parameter} is mutable and must be used to determine what will be
	 * rendered in that context for that specific item.
	 *
	 * <pre><code>
	 * &#64;Override
	 * protected void onItemRender(
	 *     PaginatedViewSlotContext&#60;T&#62; context,
	 *     ViewItem viewItem,
	 *     T value
	 * ) {
	 *     viewItem.withItem(platformStack).onClick(click -&#62; {
	 *         // clicked on value
	 *     });
	 * }
	 * </code></pre>
	 *
	 * <p>This function is called extensively, every time a paginated item is rendered or updated.
	 *
	 * <p>It is not allowed to call methods that {@link #inventoryModificationTriggered() trigger
	 * modifications in the container} of the context or in the view within this rendering function,
	 * and it is also not possible to use the item {@link ViewItem#onRender(ViewItemHandler) render}
	 * and {@link ViewItem#onUpdate(ViewItemHandler) update} functions within this function since it
	 * is already a rendering function of item itself.
	 *
	 * @param context  The pagination item rendering context.
	 * @param viewItem A mutable instance of item that will be rendered.
	 * @param value    The paginated value.
	 */
	protected abstract void onItemRender(
		@NotNull PaginatedViewSlotContext<T> context, @NotNull ViewItem viewItem, @NotNull T value);

	/**
	 * Called when pagination is switched.
	 *
	 * <p>The context in the parameter is the new paging context, so trying to {@link
	 * PaginatedViewContext#getPage() get the current page} will return the new page.
	 *
	 * @param context The page switch context.
	 */
	protected void onPageSwitch(@NotNull PaginatedViewContext<T> context) {
	}

	/**
	 * {@inheritDoc}
	 */
	@ApiStatus.Internal
	public final Paginator<T> getPaginator() {
		return paginator;
	}

	/**
	 * {@inheritDoc}
	 */
	@ApiStatus.Internal
	public final BiConsumer<PaginatedViewContext<T>, ViewItem> getPreviousPageItemFactory() {
		return previousPageItemFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@ApiStatus.Internal
	public final BiConsumer<PaginatedViewContext<T>, ViewItem> getNextPageItemFactory() {
		return nextPageItemFactory;
	}

	/**
	 * {@inheritDoc}
	 **/
	@Override
	public final void setLayout(@Nullable String... layout) {
		ensureNotInitialized();
		super.setLayout(layout);
	}

	/**
	 * {@inheritDoc}
	 **/
	@Override
	public final void setLayout(char character, Supplier<ViewItem> factory) {
		ensureNotInitialized();
		super.setLayout(character, factory);
	}

	/**
	 * {@inheritDoc}
	 **/
	@Override
	public final void setLayout(char identifier, @Nullable Consumer<ViewItem> layout) {
		ensureNotInitialized();
		super.setLayout(identifier, layout);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public ViewItem getPreviousPageItem(@NotNull PaginatedViewContext<T> context) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws InitializationException If this view is initialized.
	 */
	@Override
	public final void setPreviousPageItem(
		@NotNull BiConsumer<PaginatedViewContext<T>, ViewItem> previousPageItemFactory
	) throws InitializationException {
		ensureNotInitialized();
		this.previousPageItemFactory = previousPageItemFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public ViewItem getNextPageItem(@NotNull PaginatedViewContext<T> context) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws InitializationException If this view is initialized.
	 */
	@Override
	public final void setNextPageItem(
		@NotNull BiConsumer<PaginatedViewContext<T>, ViewItem> nextPageItemFactory
	) throws InitializationException {
		ensureNotInitialized();
		this.nextPageItemFactory = nextPageItemFactory;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws InitializationException If this view is initialized.
	 */
	@Override
	public final void setSource(@NotNull List<? extends T> source) throws InitializationException {
		ensureNotInitialized();
		this.paginator = new Paginator<>(getExpectedPageSize(), source);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws InitializationException If this view is initialized.
	 */
	@Override
	@ApiStatus.Experimental
	public final void setSource(@NotNull Function<PaginatedViewContext<T>, List<? extends T>> sourceProvider)
		throws InitializationException {
		ensureNotInitialized();
		this.paginator = new Paginator<>(getExpectedPageSize(), sourceProvider);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws InitializationException If this view is initialized.
	 */
	@Override
	@ApiStatus.Experimental
	public final AsyncPaginationDataState<T> setSourceAsync(
		@NotNull Function<PaginatedViewContext<T>, CompletableFuture<List<T>>> sourceFuture)
		throws InitializationException {
		ensureNotInitialized();
		final AsyncPaginationDataState<T> state = new AsyncPaginationDataState<>(sourceFuture);
		this.paginator = new Paginator<>(getExpectedPageSize(), state);
		return state;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws InitializationException If this view is initialized.
	 */
	@Override
	@ApiStatus.Experimental
	public void setPagesCount(int pagesCount) throws InitializationException {
		ensureNotInitialized();

		if (this.paginator == null)
			throw new IllegalStateException("Paginator must be initialized before set the source size.");

		this.paginator.setPagesCount(pagesCount);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void render(@NotNull ViewContext context) {
		if (context.getContainer().getType() != ViewType.CHEST)
			throw new IllegalStateException(String.format(
				"Pagination is not supported in \"%s\" view type: %s." + " Use chest type instead.",
				getType().getIdentifier(), getClass().getName()));

		super.render(context);

		if (context.paginated().getPaginator() == null) {
			throw new IllegalStateException("At least one pagination source must be set. "
				+ "Use #setSource in the PaginatedView constructor or set only to a context"
				+ " in the #onRender(...) function with \"context.paginated().setSource(...)\".");
		}

		updateContext(context.paginated(), 0, true, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void update(@NotNull ViewContext context) {
		super.update(context);

		final PaginatedViewContext<T> paginated = context.paginated();
		updateContext(paginated, paginated.getPage(), false /* avoid intensive page checking */, true);
	}

	private int getExpectedPageSize() {
		return limit - offset;
	}

	private void renderItemAndApplyOnContext(@NotNull ViewContext context, ViewItem item, int slot) {
		((AbstractVirtualView) context).getItems()[slot] = item;
		super.render(context, item, slot);
	}

	private void renderPaginatedItemAt(
		@NotNull PaginatedViewContext<T> context,
		int index,
		int slot,
		@NotNull T value,
		@Nullable ViewItem override) {
		// TODO replace this with a more sophisticated overlay detection
		ViewItem overlay = context.resolve(slot, true);
		if (overlay != null && overlay.isPaginationItem()) overlay = null;

		// overlapping items are those that are already in the inventory but the IF is trying to
		// render them, if it is an overlapped item it means that during the layout cleanup it was
		// detected that they should ot have been removed, so they are not removed and during layout
		// rendering they are not re-rendered.
		if (override == null) {
			final ViewItem item = new ViewItem(slot);
			item.setPaginationItem(true);

			@SuppressWarnings("unchecked") final PaginatedViewSlotContext<T> slotContext = (PaginatedViewSlotContext<T>)
				PlatformUtils.getFactory().createSlotContext(item, (BaseViewContext) context, index, value);

			runCatching(context, () -> {
				onItemRender(slotContext, item, value);
			});
			renderItemAndApplyOnContext(context, item, slot);
			item.setOverlay(overlay);
		} else {
			// we need to reset the initial rendering function of the overlaid item if not, when we
			// get to the rendering stage of the overlaid item, he overlaid item's rendering
			// function will be called first and will render the wrong item
			override.setUpdateHandler(null);

			// only if there's a fallback item available, clearing it without checking will cause
			// "No item were provided and the rendering function was not defined at slot..."
			if (override.getItem() != null) override.setRenderHandler(null);

			override.setSlot(slot);
			override.setOverlay(overlay);
			((BasePaginatedViewContext<T>) context).getItems()[slot] = override;
		}
	}

	private void tryRenderPagination(
		@NotNull PaginatedViewContext<T> context,
		String[] layout,
		ViewItem[] preservedItems,
		Consumer<List<T>> callback) {
		final Paginator<T> paginator = context.getPaginator();

		// GH-184 Skip layout render if signature is not checked
		if (layout != null && !context.isLayoutSignatureChecked()) {
			callback.accept(null);
			return;
		}

		if (paginator.isAsync())
			renderLayoutAsync(context, layout, preservedItems, paginator.getAsyncState(), callback);
		else if (paginator.isProvided())
			renderLayoutLazy(context, layout, preservedItems, paginator.getFactory(), callback);
		else {
			renderLayoutBlocking(context, layout, preservedItems, callback);
		}
	}

	private void renderLayoutAsync(
		@NotNull PaginatedViewContext<T> context,
		String[] layout,
		ViewItem[] preservedItems,
		@NotNull AsyncPaginationDataState<T> asyncState,
		Consumer<List<T>> callback) {
		callIfNotNull(asyncState.getLoadStarted(), handler -> handler.accept(context));

		asyncState
			.getJob()
			.apply(context)
			.whenComplete((data, $) -> {
				if (data == null)
					throw new IllegalStateException("Asynchronous pagination result cannot be null");

				context.getPaginator().setSource(data);
				callIfNotNull(asyncState.getSuccess(), handler -> handler.accept(context));
				renderLayoutBlocking(context, layout, preservedItems, callback);
			})
			.exceptionally(error -> {
				callIfNotNull(asyncState.getError(), handler -> handler.accept(context, error));
				throwException(context, new RuntimeException(error));
				return null;
			})
			.thenRun(() -> callIfNotNull(asyncState.getLoadFinished(), handler -> handler.accept(context)));
	}

	private void renderLayoutBlocking(
		@NotNull PaginatedViewContext<T> context,
		String[] layout,
		ViewItem[] preservedItems,
		Consumer<List<T>> callback) {
		final List<T> data = context.getPaginator().getPage(context.getPage());

		renderLayout(context, data, layout, preservedItems);
		callback.accept(data);
	}

	private void renderLayoutLazy(
		@NotNull PaginatedViewContext<T> context,
		String[] layout,
		ViewItem[] preservedItems,
		@NotNull Function<PaginatedViewContext<T>, List<T>> factory,
		Consumer<List<T>> callback) {
		List<T> data = factory.apply(context);
		if (data == null) throw new IllegalStateException("Lazy pagination result cannot be null");

		context.getPaginator().setSource(data);
		renderLayoutBlocking(context, layout, preservedItems, callback);
	}

	private void renderLayout(
		@NotNull PaginatedViewContext<T> context, List<T> elements, String[] layout, ViewItem[] preservedItems) {
		renderPatterns(context);

		final int elementsCount = elements.size();

		final Stack<Integer> itemsLayer = context.getLayoutItemsLayer();
		final int lastSlot = layout == null ? limit : itemsLayer.peek();
		final int layerSize = getLayerSize(context, layout);

		for (int i = 0; i <= lastSlot; i++) {
			if (layout != null && i >= layerSize) break;

			final int targetSlot = layout == null ? offset + i : itemsLayer.elementAt(i);
			final ViewItem preserved = preservedItems == null || preservedItems.length <= i ? null : preservedItems[i];
			if (i < elementsCount)
				renderPaginatedItemAt(context, i, targetSlot, elements.get(i), preserved);
			else {
				final ViewItem item = context.resolve(targetSlot, true);
				// check if a non-virtual item has been defined in that slot
				if (item != null) {
					if (!item.isPaginationItem()) {
						renderItemAndApplyOnContext(context, item, targetSlot);
						continue;
					}

					final ViewItem overlay = item.getOverlay();
					if (overlay != null) {
						renderItemAndApplyOnContext(context, overlay, targetSlot);
						continue;
					}
				}

				removeAt(context, targetSlot);
			}
		}
	}

	private void renderPatterns(@NotNull PaginatedViewContext<T> context) {
		for (final LayoutPattern pattern : context.getLayoutPatterns()) {
			for (final int slot : pattern.getSlots()) {
				final ViewItem item = pattern.getFactory().get();

				// pattern slot must be unset
				if (item.getSlot() != -1)
					throw new IllegalStateException(String.format(
						"Items defined through the layout pattern's item factory cannot have a "
							+ "pre-defined slot. Use `item()` instead of `slot(x)`. "
							+ "Expected: *unset slot*, given: %s",
						item.getSlot()));

				item.setSlot(slot);
				renderItemAndApplyOnContext(context, item, slot);
			}
		}
	}

	final void updateLayout(@NotNull PaginatedViewContext<T> context, String[] layout) {
		// what we will do: first, use the old defined layout to preserve the actual item slot state
		// and then reorder these items with the new slots of the new layout on different positions
		// but with the same preserved state
		final ViewItem[] items = clearLayout(context, useLayout(context));
		resolveLayout(context, context, layout);
		tryRenderPagination(context, layout, items, null);
	}

	private ViewItem[] clearLayout(@NotNull PaginatedViewContext<T> context, String[] layout) {
		final int elementsCount =
			context.getPaginator().getPage(context.getPage()).size();
		final Stack<Integer> itemsLayer = context.getLayoutItemsLayer();
		final int lastSlot = layout == null ? limit : itemsLayer.peek();
		final int layerSize = getLayerSize(context, layout);

		final ViewItem[] preservedItems = new ViewItem[Math.min(layerSize, elementsCount) + 1];
		for (int i = 0; i <= lastSlot; i++) {
			if (layout != null && i >= layerSize) break;

			final int targetSlot = layout == null ? offset + i : itemsLayer.elementAt(i);
			if (i < elementsCount) {
				final ViewItem preserved = context.getItem(targetSlot);
				preservedItems[i] = preserved;
			}

			removeAt(context, targetSlot);
		}

		return preservedItems;
	}

	final void removeAt(@NotNull ViewContext context, int slot) {
		context.clear(slot);
		context.getContainer().removeItem(slot);
	}

	private int getLayerSize(@NotNull PaginatedViewContext<T> context, String[] layout) {
		return layout == null ? 0 /* ignored */ : context.getLayoutItemsLayer().size();
	}

	private static <T> ViewItem internalNavigationItem(
		@NotNull PaginatedVirtualView<T> view,
		@NotNull PaginatedViewContext<T> context,
		int direction
	) {
		final boolean isBackwards = direction == NAVIGATE_LEFT;
		final BiConsumer<PaginatedViewContext<T>, ViewItem> factory = isBackwards
			? view.getPreviousPageItemFactory()
			: view.getNextPageItemFactory();

		if (factory == null)
			return isBackwards
				? view.getPreviousPageItem(context)
				: view.getNextPageItem(context);

		final ViewItem item = new ViewItem();
		factory.accept(context, item);
		return item;
	}

	static <T> ViewItem resolveNavigationItem(
		@NotNull AbstractPaginatedView<T> view,
		@NotNull PaginatedViewContext<T> context,
		int direction
	) {
		final ViewItem item = internalNavigationItem(view, context, direction);
		if (item != null)
			return item;

		final PlatformViewFrame<?, ?, ?> vf = view.getViewFrame();
		if (vf == null)
			return null;

		final Function<PaginatedViewContext<?>, ViewItem> fallback = direction == NAVIGATE_LEFT
			? vf.getDefaultPreviousPageItem()
			: vf.getDefaultNextPageItem();

		if (fallback == null)
			return null;

		return fallback.apply(context);
	}

	final void updateContext(
		@NotNull PaginatedViewContext<T> context, int page, boolean pageChecking, boolean setupForRender) {
		if (context instanceof ViewSlotContext)
			throw new IllegalStateException("Cannot update context using a slot context");

		final String[] layout = useLayout(context);
		if (pageChecking) {
			if (setupForRender
				&& (context.getPaginator().isSync()
				&& !context.getPaginator().hasPage(page))) return;

			if (layout != null && !context.isLayoutSignatureChecked())
				resolveLayout(context, context, layout);

			if (setupForRender) ((BasePaginatedViewContext<T>) context).setPage(page);
		}

		if (!setupForRender) return;

		tryRenderPagination(context, layout, null, $ -> {
			updateNavigationItem(context, NAVIGATE_LEFT);
			updateNavigationItem(context, NAVIGATE_RIGHT);
		});
	}

	private int getNavigationItemSlot(
		@NotNull PaginatedViewContext<T> context, @Range(from = NAVIGATE_LEFT, to = NAVIGATE_RIGHT) int direction) {
		return direction == NAVIGATE_LEFT ? context.getPreviousPageItemSlot() : context.getNextPageItemSlot();
	}

	private void updateNavigationItem(
		@NotNull PaginatedViewContext<T> context, @Range(from = NAVIGATE_LEFT, to = NAVIGATE_RIGHT) int direction) {
		final AbstractPaginatedView<T> root = context.getRoot();
		int expectedSlot = getNavigationItemSlot(context, direction);
		ViewItem item = null;

		// it is recommended to use layout for pagination, so at this stage the layout may have
		// already defined the slot for the pagination items, so we check if it is not defined yet
		if (expectedSlot == -1) {
			// check if navigation item was manually set by the user
			item = resolveNavigationItem(this, context, direction);

			if (item == null || item.getSlot() == -1) return;

			expectedSlot = item.getSlot();
			if (direction == NAVIGATE_LEFT) context.setPreviousPageItemSlot(expectedSlot);
			else context.setNextPageItemSlot(expectedSlot);
		}

		if (item == null) item = resolveNavigationItem(this, context, direction);

		// ensure item is removed if it was resolved and set before and is not anymore
		if (item == null) {
			root.removeAt(context, expectedSlot);
			return;
		}

		// the click handler should be checked for cases where the user has defined the navigation
		// item manually, so we will not override his handler
		if (item.getClickHandler() == null) {
			item.onClick(click -> {
				if (direction == NAVIGATE_LEFT) click.paginated().switchToPreviousPage();
				else click.paginated().switchToNextPage();
			});
		}

		renderItemAndApplyOnContext(context, item.withCancelOnClick(true), expectedSlot);
	}

	private static <T> void callIfNotNull(T handler, Consumer<T> fn) {
		if (handler == null) return;
		fn.accept(handler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	int getNextAvailableSlot() {
		return super.getNextAvailableSlot();
	}
}
