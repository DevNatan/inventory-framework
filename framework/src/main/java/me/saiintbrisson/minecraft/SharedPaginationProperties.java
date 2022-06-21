package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static me.saiintbrisson.minecraft.AbstractPaginatedView.NAVIGATE_LEFT;
import static me.saiintbrisson.minecraft.AbstractPaginatedView.NAVIGATE_RIGHT;

/**
 * Properties shared between entities that can be paginated, like {@link PaginatedVirtualView}.
 */
@Getter
@Setter
@RequiredArgsConstructor
class SharedPaginationProperties<T> {

	static final char PREVIOUS_PAGE_CHAR = '<';
	static final char NEXT_PAGE_CHAR = '>';
	static final char EMPTY_SLOT_CHAR = 'X';
	static final char ITEM_SLOT_CHAR = 'O';

	private final PaginatedVirtualView<T> holder;

	private final List<LayoutPattern> customLayoutPatterns = new ArrayList<>();
	private LayoutPattern itemsLayoutPattern;
	private int offset = -1;
	private int limit = -1;
	private String[] layout;
	private Paginator<T> paginator;

	public String[] getLayout() {
		return tryGet(
			layout,
			"layout",
			view -> view.getProperties().getLayout()
		);
	}

	public LayoutPattern getItemsLayoutPattern() {
		return tryGet(
			itemsLayoutPattern,
			"items layout pattern",
			view -> view.getProperties().getItemsLayoutPattern()
		);
	}

	public Paginator<T> getPaginator() {
		return tryGet(
			paginator,
			"paginator",
			view -> view.getProperties().getPaginator()
		);
	}

	public int getOffset() {
		return tryGet(
			offset,
			"offset",
			view -> view.getProperties().getOffset()
		);
	}

	public void setOffset(int offset) {
		if (getLayout() != null)
			throw new IllegalStateException(
				"Layout, offset and limit slot cannot be used together."
			);

		this.offset = offset;
	}

	public int getLimit() {
		return tryGet(
			limit,
			"limit",
			view -> view.getProperties().getLimit()
		);
	}

	public void setLimit(int limit) {
		if (getLayout() != null)
			throw new IllegalStateException(
				"Layout, offset and limit slot cannot be used together."
			);

		this.limit = limit;
	}

	public void setLayout(char character, Supplier<ViewItem> factory) {
		final String name = this instanceof ViewContext ? "context" : "view";
		if (character == EMPTY_SLOT_CHAR
			|| character == ITEM_SLOT_CHAR
			|| character == NEXT_PAGE_CHAR
			|| character == PREVIOUS_PAGE_CHAR
		) throw new IllegalArgumentException(String.format(
			"The \"%c\" character is reserved in %s layout" +
				" and cannot be used due to backwards compatibility.",
			character,
			name
		));

		customLayoutPatterns.add(new LayoutPattern(character, factory));
	}

	LayoutPattern getLayoutOrNull(char character) {
		return customLayoutPatterns.stream()
			.filter(pattern -> pattern.getCharacter() == character)
			.findFirst()
			.orElse(null);
	}

	public boolean hasSource() {
		final Paginator<T> paginator = getPaginator();
		return paginator != null && paginator.getSource() != null;
	}

	void updateContext(
		@NotNull PaginatedViewContext<T> context,
		int page,
		boolean pageChecking,
		boolean setupForRender
	) {
		final String[] layout = getLayout();
		if (pageChecking) {
			if (setupForRender && !context.getProperties().getPaginator().hasPage(page))
				return;

			if (layout != null && !context.isLayoutSignatureChecked())
				resolveLayout(context, layout, setupForRender);

			if (setupForRender)
				((BasePaginatedViewContext<T>) context).setPage(page);
		}

		if (!setupForRender) return;

		context.getRoot().renderLayout(context, layout, null);
		updateNavigationItem(context, NAVIGATE_LEFT);
		updateNavigationItem(context, NAVIGATE_RIGHT);
	}

	private int getNavigationItemSlot(
		@NotNull PaginatedViewContext<T> context,
		@Range(from = NAVIGATE_LEFT, to = NAVIGATE_RIGHT) int direction
	) {
		return direction == NAVIGATE_LEFT
			? context.getPreviousPageItemSlot()
			: context.getNextPageItemSlot();
	}

	void updateNavigationItem(
		@NotNull PaginatedViewContext<T> context,
		@Range(from = NAVIGATE_LEFT, to = NAVIGATE_RIGHT) int direction
	) {
		final AbstractPaginatedView<T> root = context.getRoot();
		int expectedSlot = getNavigationItemSlot(context, direction);
		ViewItem item = null;

		// it is recommended to use layout for pagination, so at this stage the layout may have
		// already defined the slot for the pagination items, so we check if it is not defined yet
		if (expectedSlot == -1) {
			// check if navigation item was manually set by the user
			item = root.resolveNavigationItem(context, direction);

			if (item == null || item.getSlot() == -1)
				return;

			expectedSlot = item.getSlot();
			if (direction == NAVIGATE_LEFT) context.setPreviousPageItemSlot(expectedSlot);
			else context.setNextPageItemSlot(expectedSlot);
		}

		if (item == null)
			item = root.resolveNavigationItem(context, direction);

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

		root.render(context, item.withCancelOnClick(true), expectedSlot);
	}

	void resolveLayout(
		@NotNull PaginatedViewContext<T> context,
		String[] layout,
		boolean setupForRender
	) {
		// since the layout is only defined once, we cache it
		// to avoid unnecessary processing every time we update the context.
		final int len = layout.length;
		final int containerRowsCount = context.getContainer().getRowsCount();
		if (len != containerRowsCount)
			throw new IllegalArgumentException(String.format(
				"Layout columns must respect the rows count of the container" +
					" (layout size: %d, container rows: %d)",
				len,
				containerRowsCount
			));

		System.out.println("resolving layouit, setupForRender = " + setupForRender);

		final SharedPaginationProperties<T> ctxProps = context.getProperties();
		ctxProps.setItemsLayoutPattern(new LayoutPattern(ITEM_SLOT_CHAR, null));

		final AbstractPaginatedView<T> root = context.getRoot();
		final LayoutPattern defaultPattern = ctxProps.getItemsLayoutPattern();
		final int containerColumnsCount = context.getContainer().getColumnsCount();
		for (int row = 0; row < len; row++) {
			final String layer = layout[row];

			final int layerLength = layer.length();
			if (layerLength != containerColumnsCount)
				throw new IllegalArgumentException(String.format(
					"Layout layer length located at %d must respect the columns count of the" +
						" container (layer length: %d, container columns: %d).",
					row,
					layerLength,
					containerColumnsCount
				));

			for (int col = 0; col < containerRowsCount; col++) {
				final int targetSlot = col + (row * containerRowsCount);
				final char character = layer.charAt(col);
				switch (character) {
					case EMPTY_SLOT_CHAR:
						break;
					case ITEM_SLOT_CHAR: {
						defaultPattern.getSlots().push(targetSlot);
						break;
					}
					case PREVIOUS_PAGE_CHAR: {
						if (setupForRender) {
							root.resolveNavigationItem(context, NAVIGATE_LEFT);
							context.setPreviousPageItemSlot(targetSlot);
						}
						break;
					}
					case NEXT_PAGE_CHAR: {
						if (setupForRender) {
							root.resolveNavigationItem(context, NAVIGATE_RIGHT);
							context.setNextPageItemSlot(targetSlot);
						}
						break;
					}
					default: {
						final LayoutPattern pattern = context.getProperties().getLayoutOrNull(character);
						if (pattern != null)
							pattern.getSlots().push(targetSlot);
					}
				}
			}
		}

		if (!setupForRender)
			return;

		ctxProps.getPaginator().setPageSize(
			ctxProps.getItemsLayoutPattern().getSlots().size()
		);
		context.setLayoutSignatureChecked(true);
	}

	@Contract("!null, _, _ -> param1")
	private <R> R tryGet(
		R value,
		@NotNull String fieldName,
		@NotNull Function<AbstractPaginatedView<T>, R> fallbackAccessor
	) {
		if ((value instanceof Integer && ((Integer) value) != -1) || value != null)
			return value;

		if (holder instanceof PaginatedViewContext)
			return fallbackAccessor.apply(((PaginatedViewContext<T>) holder).getRoot());

		throw new IllegalStateException(String.format(
			"Tried to get %s but it couldn't be determined",
			fieldName
		));
	}

}
