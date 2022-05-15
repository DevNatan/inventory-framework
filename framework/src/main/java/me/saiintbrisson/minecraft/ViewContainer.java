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

	/**
	 * Normalizes the specified parameter to conform to container constraints and does not exceed
	 * or fail in an attempt to set the container size.
	 *
	 * @param size The expected size of the container.
	 * @return The size of the container according to the specified parameter.
	 */
	default int normalizeSize(final int size) {
		// is less than the row size, probably the user thought it was to put the row size
		if (size < getRowSize()) return size * getRowSize();

		if (size % getRowSize() != 0)
			throw new IllegalArgumentException(format(
				"Container size must be a multiple of %d (given: %d)",
				getRowSize(),
				size
			));

		return size;
	}

	void open(@NotNull final Iterable<Viewer> viewers);

	void close();

	void changeTitle(@Nullable final String title);

}
