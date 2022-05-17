package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.String.format;

interface ViewContainer {

	@NotNull
	ViewType getType();

	/**
	 * Returns the position of the first slot of this container.
	 *
	 * @return The first slot of this container.
	 */
	int getFirstSlot();

	/**
	 * Returns the position of the last slot of this container.
	 *
	 * @return The last slot of this container.
	 */
	int getLastSlot();

	/**
	 * Returns whether a container slot is filled by an item.
	 *
	 * @param slot The item slot.
	 * @return Whether there is an item in the specified slot.
	 */
	boolean hasItem(int slot);

	void renderItem(int slot, Object item);

	boolean matchesItem(int slot, Object item, boolean exactly);

	Object convertItem(Object source);

	boolean isSupportedItem(Object item);

	/**
	 * The number of slots in this container.
	 *
	 * @return The number of slots in this container.
	 */
	int getSize();

	/**
	 * The number of slot indexes in this container (available or not).
	 *
	 * @return All slot indexes in this container.
	 */
	int getSlotsCount();

	/**
	 * The amount of horizontal lines present in the container.
	 *
	 * @return The amount of horizontal lines present in the container.
	 */
	int getRowsCount();

	/**
	 * The amount of vertical lines present in the container.
	 *
	 * @return The amount of vertical lines present in the container.
	 */
	int getColumnsCount();

	void open(@NotNull Viewer viewer);

	void close();

	void changeTitle(@Nullable String title);

}
