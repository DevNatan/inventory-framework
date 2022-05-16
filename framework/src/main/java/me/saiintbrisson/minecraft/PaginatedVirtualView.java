package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface PaginatedVirtualView<T> extends VirtualView {

	/**
	 * Returns the total maximum number of fixed elements that a page can contain.
	 *
	 * @return the items count.
	 */
	@Deprecated
	default int getPageSize() {
		return getPageItemsCount();
	}

	/**
	 * Returns the total maximum number of fixed elements that a page can contain.
	 *
	 * @return the items count.
	 */
	int getPageItemsCount();

	void setSource(@NotNull List<T> source);

}
