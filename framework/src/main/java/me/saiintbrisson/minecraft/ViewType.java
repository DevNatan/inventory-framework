package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static java.lang.String.format;

@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ViewType {

	public static final ViewType HOPPER = new ViewType("hopper", 5, 1, 5, false);
	public static final ViewType CHEST = new ViewType("chest", 54, 6, 9, true);

	public static final ViewType FURNACE = new ViewType("furnace", 2, 1, 1, false) {
		private static final int RESULT_SLOT = 2;

		@Override
		public int[] getResultSlots() {
			return new int[] { RESULT_SLOT };
		}

		@Override
		public boolean hasResultSlot() {
			return true;
		}
	};

	public static final ViewType CRAFTING_TABLE = new ViewType("crafting-table", 9, 3, 3, false) {
		private static final int RESULT_SLOT = 3;

		@Override
		public int[] getResultSlots() {
			return new int[] { RESULT_SLOT };
		}

		@Override
		public boolean hasResultSlot() {
			return true;
		}

		@Override
		public boolean canPlayerInteractOn(int slot) {
			return slot != RESULT_SLOT;
		}
	};

	@EqualsAndHashCode.Include
	private final String identifier;

	private final int maxSize, rows, columns;
	private final boolean extendable;

	/**
	 * Normalizes the specified parameter to conform to container constraints and does not exceed
	 * or fail in an attempt to set the container size, e.g.: if player provides inventory rows count
	 * instead of the full inventory size, it will return the inventory size.
	 *
	 * @param size The expected size of the container.
	 * @return The size of the container according to the specified parameter.
	 */
	public final int normalize(final int size) {
		if (size == 0) return size;

		if (size >= rows) {
			if (size % columns != 0)
				throw new IllegalArgumentException(format(
					"Container size must be a multiple of %d (given: %d)",
					columns,
					size
				));

			final int fullSize = size * columns;
			if (fullSize > getMaxSize())
				throw new IllegalArgumentException(format(
					"Size cannot exceed container max size of %d (given: %d (%s rows))",
					getMaxSize(),
					fullSize,
					size
				));

			return fullSize;
		}

		return size * columns;
	}

	public int[] getResultSlots() {
		return null;
	}

	public boolean hasResultSlot() {
		return getResultSlots() != null;
	}

	public boolean canPlayerInteractOn(int slot) {
		return true;
	}

}