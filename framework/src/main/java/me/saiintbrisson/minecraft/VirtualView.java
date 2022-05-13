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
	 * Closes this view.
	 */
	void close();

	ViewErrorHandler getErrorHandler();

	/**
	 * Returns a new {@link ViewItem}.
	 */
	@NotNull
	ViewItem item();

	/**
	 * Returns a new {@link ViewItem} with an item stack.
	 *
	 * @param item The item.
	 * @deprecated
	 */
	@NotNull
	ViewItem item(@NotNull Object item);

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
	 * @param slot Tthe item slot.
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

	void update();

	void open(@NotNull final Object viewer, @NotNull final Map<String, Object> data);

}
