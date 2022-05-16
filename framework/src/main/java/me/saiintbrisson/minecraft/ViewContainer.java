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
	boolean hasItem(final int slot);

	/**
	 * The number of slots in this inventory (available or not).
	 *
	 * @return The number of slots in this container.
	 */
	int getSlotsCount();

	/**
	 * The amount of vertical lines present in the container.
	 *
	 * @return The amount of vertical lines present in the container.
	 */
	int getRowSize();

	void open(@NotNull final Iterable<Viewer> viewers);

	void close();

	void changeTitle(@Nullable final String title);

}
