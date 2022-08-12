package me.saiintbrisson.minecraft.pipeline.interceptors;

import me.saiintbrisson.minecraft.AsyncPaginationDataState;
import me.saiintbrisson.minecraft.PaginatedViewContext;
import me.saiintbrisson.minecraft.PaginatedViewSlotContext;
import me.saiintbrisson.minecraft.Paginator;
import me.saiintbrisson.minecraft.PlatformUtils;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.ViewItem;
import me.saiintbrisson.minecraft.ViewSlotContext;
import me.saiintbrisson.minecraft.pipeline.PipelineContext;
import me.saiintbrisson.minecraft.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;

import static me.saiintbrisson.minecraft.IFUtils.callIfNotNull;
import static me.saiintbrisson.minecraft.IFUtils.checkContainerType;
import static me.saiintbrisson.minecraft.IFUtils.checkPaginationSourceAvailability;
import static me.saiintbrisson.minecraft.IFUtils.useLayout;
import static me.saiintbrisson.minecraft.IFUtils.useLayoutItemsLayer;
import static me.saiintbrisson.minecraft.IFUtils.useLayoutItemsLayerSize;
import static me.saiintbrisson.minecraft.PaginatedVirtualView.NAVIGATE_LEFT;
import static me.saiintbrisson.minecraft.PaginatedVirtualView.NAVIGATE_RIGHT;

public final class PaginationRenderInterceptor implements PipelineInterceptor<ViewContext> {

	@Override
	public void intercept(@NotNull PipelineContext<ViewContext> pipeline, ViewContext context) {
		checkContainerType(context);

		// proceed to regular renderization
		pipeline.proceed();
		checkPaginationSourceAvailability(context);

		updateContext(context.paginated(), 0, true, true);
	}

	private <T> void updateContext(
		@NotNull PaginatedViewContext<T> context,
		int page,
		boolean pageChecking,
		boolean setupForRender
	) {
		if (context instanceof ViewSlotContext)
			throw new IllegalStateException("Cannot update context using a slot context");

		final String[] layout = useLayout(context.getRoot(), context);
		if (pageChecking) {
			if (setupForRender
				&& (context.getPaginator().isSync()
				&& !context.getPaginator().hasPage(page))) return;

			if (layout != null && !context.isLayoutSignatureChecked())
				throw new IllegalStateException("Layout not resolved");

			if (setupForRender)
				context.setPage(page);
		}

		if (!setupForRender) return;

		tryRenderPagination(context, layout, null, $ -> {
			updateNavigationItem(context, NAVIGATE_LEFT);
			updateNavigationItem(context, NAVIGATE_RIGHT);
		});
	}

	/**
	 * Attempts to render pagination data in a context.
	 *
	 * @param context        The pagination context.
	 * @param layout         The layout that'll be used as base to renderization.
	 * @param preservedItems Preserved items from previous renders.
	 * @param callback       Callback containing paging data
	 * @param <T>            The pagination data type.
	 */
	private <T> void tryRenderPagination(
		@NotNull PaginatedViewContext<T> context,
		String[] layout,
		ViewItem[] preservedItems,
		Consumer<List<T>> callback
	) {
		final Paginator<T> paginator = context.getPaginator();

		// GH-184 Skip layout render if signature is not checked
		if (layout != null && !context.isLayoutSignatureChecked()) {
			callback.accept(null);
			return;
		}

		if (paginator.isAsync())
			renderPaginationAsync(context, layout, preservedItems, paginator.getAsyncState(), callback);
		else if (paginator.isProvided())
			renderPaginationLazy(context, layout, preservedItems, paginator.getFactory(), callback);
		else {
			renderPaginationBlocking(context, layout, preservedItems, callback);
		}
	}

	/**
	 * Returns the position of the navigation item for the specified direction.
	 * <p>
	 * The item slot is defined during {@link LayoutResolutionInterceptor layout resolution}.
	 *
	 * @param context   The pagination context.
	 * @param direction The navigation direction.
	 * @return Current navigation (of the specified direction) item slot.
	 */
	private int getNavigationItemSlot(
		@NotNull PaginatedViewContext<?> context,
		int direction
	) {
		return direction == NAVIGATE_LEFT
			? context.getPreviousPageItemSlot()
			: context.getNextPageItemSlot();
	}

	private <T> void updateNavigationItem(
		@NotNull PaginatedViewContext<T> context,
		int direction
	) {
//		final AbstractPaginatedView<T> root = context.getRoot();
//		int expectedSlot = getNavigationItemSlot(context, direction);
//		ViewItem item = null;
//
//		// it is recommended to use layout for pagination, so at this stage the layout may have
//		// already defined the slot for the pagination items, so we check if it is not defined yet
//		if (expectedSlot == -1) {
//			// check if navigation item was manually set by the user
//			item = resolveNavigationItem(this, context, direction);
//
//			if (item == null || item.getSlot() == -1) return;
//
//			expectedSlot = item.getSlot();
//			if (direction == NAVIGATE_LEFT) context.setPreviousPageItemSlot(expectedSlot);
//			else context.setNextPageItemSlot(expectedSlot);
//		}
//
//		if (item == null) item = resolveNavigationItem(this, context, direction);
//
//		// ensure item is removed if it was resolved and set before and is not anymore
//		if (item == null) {
//			root.removeAt(context, expectedSlot);
//			return;
//		}
//
//		// the click handler should be checked for cases where the user has defined the navigation
//		// item manually, so we will not override his handler
//		if (item.getClickHandler() == null) {
//			item.onClick(click -> {
//				if (direction == NAVIGATE_LEFT) click.paginated().switchToPreviousPage();
//				else click.paginated().switchToNextPage();
//			});
//		}
//
//		renderItemAndApplyOnContext(context, item.withCancelOnClick(true), expectedSlot);
	}

	private <T> void renderPaginationAsync(
		@NotNull PaginatedViewContext<T> context,
		String[] layout,
		ViewItem[] preservedItems,
		@NotNull AsyncPaginationDataState<T> asyncState,
		Consumer<List<T>> callback
	) {
		callIfNotNull(asyncState.getLoadStarted(), handler -> handler.accept(context));

		asyncState
			.getJob()
			.apply(context)
			.whenComplete((data, $) -> {
				if (data == null)
					throw new IllegalStateException("Asynchronous pagination result cannot be null");

				context.getPaginator().setSource(data);
				callIfNotNull(asyncState.getSuccess(), handler -> handler.accept(context));
				renderPaginationBlocking(context, layout, preservedItems, callback);
			}).exceptionally(error -> {
				callIfNotNull(asyncState.getError(), handler -> handler.accept(context, error));
				throw new RuntimeException("Failed to retrieve pagination data", error);
			}).thenRun(() -> callIfNotNull(asyncState.getLoadFinished(), handler -> handler.accept(context)));
	}

	private <T> void renderPaginationLazy(
		@NotNull PaginatedViewContext<T> context,
		String[] layout,
		ViewItem[] preservedItems,
		@NotNull Function<PaginatedViewContext<T>, List<T>> factory,
		Consumer<List<T>> callback
	) {
		List<T> data = factory.apply(context);
		if (data == null) throw new IllegalStateException("Lazy pagination result cannot be null");

		context.getPaginator().setSource(data);
		renderPaginationBlocking(context, layout, preservedItems, callback);
	}

	private <T> void renderPaginationBlocking(
		@NotNull PaginatedViewContext<T> context,
		String[] layout,
		ViewItem[] preservedItems,
		Consumer<List<T>> callback
	) {
		final List<T> data = context.getPaginator().getPage(context.getPage());

		renderPagination(context, data, layout, preservedItems);
		callback.accept(data);
	}

	private <T> void renderPagination(
		@NotNull PaginatedViewContext<T> context,
		List<T> elements,
		String[] layout,
		ViewItem[] preservedItems
	) {
//		renderPatterns(context);

		final int elementsCount = elements.size();
		final Stack<Integer> layoutItemsLayer = useLayoutItemsLayer(context.getRoot(), context);
		final int lastSlot = layout == null ? context.getRoot().getLimit() : layoutItemsLayer.peek();
		final int layerSize = useLayoutItemsLayerSize(layoutItemsLayer, layout);

		for (int i = 0; i <= lastSlot; i++) {
			if (layout != null && i >= layerSize) break;

			final int targetSlot = layout == null ? context.getRoot().getOffset() + i : layoutItemsLayer.elementAt(i);
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

	private void removeAt(@NotNull ViewContext context, int slot) {
		context.clear(slot);
		context.getContainer().removeItem(slot);
	}

	private void renderItemAndApplyOnContext(@NotNull ViewContext context, ViewItem item, int slot) {
		context.getItems()[slot] = item;
		context.getRoot().render(context, item, slot);
	}

	private <T> void renderPaginatedItemAt(
		@NotNull PaginatedViewContext<T> context,
		int index,
		int slot,
		@NotNull T value,
		@Nullable ViewItem override
	) {
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
				PlatformUtils.getFactory().createSlotContext(item, context, index, value);

			context.getRoot().runCatching(context, () -> {
				context.getRoot().callItemRender(slotContext, item, value);
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
			context.apply(overlay, slot);
		}
	}

}
