package me.saiintbrisson.minecraft;

import java.util.Map;
import org.jetbrains.annotations.NotNull;

public interface VirtualView {

	/**
	 * The current title of this view's container.
	 *
	 * @return The title of container of this view.
	 */
	@NotNull
	String getTitle();

	/**
	 * Returns the row count of this view.
	 *
	 * @return The row count of this view.compileOnly
	 */
	int getRows();

	/**
	 * Mark this view to be closed when needed.
	 */
	void close();

	/**
	 * Closes this view immediately.
	 */
	void closeUninterruptedly();

	ViewErrorHandler getErrorHandler();

	/**
	 * Registers a {@link ViewItem} in the specified slot.
	 *
	 * @param slot The item slot.
	 */
	@NotNull
	ViewItem slot(int slot);

	/**
	 * Registers a {@link ViewItem} with an item stack in the specified slot.
	 *
	 * @param slot The item slot.
	 * @param item The item to be set.
	 */
	@NotNull
	ViewItem slot(int slot, Object item);

	/**
	 * Registers a {@link ViewItem} in the specified row and column.
	 *
	 * @param row    The item slot row.
	 * @param column The item slot column.
	 */
	@NotNull
	ViewItem slot(int row, int column);

	/**
	 * Registers a {@link ViewItem} with an item stack in the specified row and column.
	 *
	 * @param row    The item slot row.
	 * @param column The item slot column.
	 * @param item   The item to be set.
	 */
	@NotNull
	ViewItem slot(int row, int column, Object item);

	void with(@NotNull ViewItem item);

	void update();

}
