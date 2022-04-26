package me.saiintbrisson.minecraft;

import me.saiintbrisson.minecraft.utils.Paginator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static me.saiintbrisson.minecraft.View.UNSET_SLOT;

public class PaginatedViewContext<T> extends ViewContext {

	public static final int FIRST_PAGE = 0;
	private int page;
	private int previousPageItemSlot = UNSET_SLOT;
	private int nextPageItemSlot = UNSET_SLOT;

	public PaginatedViewContext(View view, Player player, Inventory inventory, int page) {
		super(view, player, inventory);
		this.page = page;
	}

	@Override
	@SuppressWarnings("unchecked")
	public final void setLayout(String... layout) {
		// dynamic layout update
		if (isCheckedLayerSignature()) {
			getView().getFrame().debug("[context] layout updated");
			this.layout = layout;
			((PaginatedView<T>) view).updateLayout(this, layout);
		} else {
			getView().getFrame().debug("[context] layout set");
			super.setLayout(layout);
		}
	}

	/**
	 * Returns the current page for that context.
	 */
	public int getPage() {
		return page;
	}

	/**
	 * Sets the current page in that context.
	 *
	 * @param page the new page numbering.
	 */
	void setPage(int page) {
		this.page = page;
	}

	/**
	 * Returns the number of pages available in that context.
	 *
	 * @return the total page count.
	 */
	public int getPagesCount() {
		return getPaginator().count();
	}

	/**
	 * Returns the total maximum number of fixed elements that a page can contain.
	 *
	 * @return the items count.
	 */
	public int getPageSize() {
		return getPaginator().getPageSize();
	}

	/**
	 * Returns the total maximum number of items that a page in a layered context can contain.
	 *
	 * @return the max items count.
	 */
	public int getPageMaxItemsCount() {
		if (getItemsLayer() == null)
			throw new IllegalStateException("layout not resolved");

		return getItemsLayer().size();
	}

	/**
	 * Returns the number of the previous page with a minimum value of {@link #FIRST_PAGE}.
	 */
	public int getPreviousPage() {
		return Math.max(FIRST_PAGE, page - 1);
	}

	/**
	 * Returns `false` if the current page is the first page of the available pages or `true` otherwise
	 */
	public boolean hasPreviousPage() {
		return !isFirstPage();
	}

	/**
	 * Returns the number of the next page with the maximum value of the number of available pages.
	 */
	public int getNextPage() {
		return Math.min(getPaginator().count(), page + 1);
	}

	/**
	 * Returns `true` if there are more pages than the current one available or `false` otherwise.
	 */
	public boolean hasNextPage() {
		return getPaginator().hasPage(page + 1);
	}

	/**
	 * Returns `true` if the current page is the first page of the available pages or `false` otherwise.
	 */
	public boolean isFirstPage() {
		return page == FIRST_PAGE;
	}

	/**
	 * Returns `true` if the current page is the last page of the available pages or `false` otherwise.
	 */
	public boolean isLastPage() {
		return !hasNextPage();
	}

	/**
	 * Updates the current context by jumping to the specified page.
	 *
	 * @param page the new page.
	 */
	@SuppressWarnings("unchecked")
	public void switchTo(int page) {
		final PaginatedView<T> paginatedView = ((PaginatedView<T>) view);
		paginatedView.updateContext(this, page);
		paginatedView.onPageSwitch(this);
	}

	/**
	 * Updates the current context by switching to the previous page if available.
	 */
	public void switchToPreviousPage() {
		switchTo(page - 1);
	}

	/**
	 * Updates the current context by switching to the next page if available.
	 */
	public void switchToNextPage() {
		switchTo(page + 1);
	}

	/**
	 * Returns the {@link Paginator} of that context.
	 */
	@SuppressWarnings("unchecked")
	Paginator<T> getPaginator() {
		final Paginator<T> paginator = (Paginator<T>) ((PaginatedView<T>) view).getPaginator();
		if (paginator != null)
			return paginator;

		throw new IllegalArgumentException("No pagination source was provided.");
	}

	/**
	 * Defines the source of the {@link Paginator} for this context.
	 *
	 * @param source the pagination source.
	 * @deprecated Use {@link #setSource(List)} instead.
	 */
	@Deprecated
	public void setPaginationSource(List<?> source) {
		setSource(source);
	}

	/**
	 * Inventory data pagination source.
	 *
	 * @return The paging source for all pages, the one defined earlier.
	 */
	public List<T> getSource() {
		return Collections.unmodifiableList(getPaginator().getSource());
	}

	/**
	 * Inventory data pagination source of a specific page.
	 *
	 * @param page The page you want to get the source from.
	 * @return The paging source for a specific page.
	 */
	public List<T> getSource(int page) {
		return Collections.unmodifiableList(
			getPaginator().getPage(Math.min(page, getPagesCount()))
		);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void setSource(List<?> source) {
		final PaginatedView<T> view = (PaginatedView<T>) getView();
		final boolean checkedLayer = isCheckedLayerSignature();
		final String[] layout = view.useLayout(this);

		// force layout resolving but do not render
		if (!checkedLayer && layout != null)
			view.resolveLayout(this, layout, false);

		view.setPaginator(new Paginator(
			checkedLayer ? getItemsLayer().size() : view.getPageSize(),
			source
		));
	}

	public int getPreviousPageItemSlot() {
		return previousPageItemSlot;
	}

	void setPreviousPageItemSlot(int previousPageItemSlot) {
		this.previousPageItemSlot = previousPageItemSlot;
	}

	public int getNextPageItemSlot() {
		return nextPageItemSlot;
	}

	void setNextPageItemSlot(int nextPageItemSlot) {
		this.nextPageItemSlot = nextPageItemSlot;
	}

	@Override
	SlotFindResult findNextAvailableSlot(@NotNull ItemStack currentItem) {
		// the next slot must respect the layout order and move the item to the target slot
		if (getLayout() != null) {
			// TODO
		}

		return super.findNextAvailableSlot(currentItem);
	}

	@Override
	public String toString() {
		return "PaginatedViewContext{" +
			"page=" + page +
			"} " + super.toString();
	}

}
