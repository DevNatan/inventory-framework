package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Range;

import static java.lang.String.format;

@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ViewType {

	private final String identifier;
	private final int maxSize;

	int normalize(int size) {
		return size;
	}

	public static final ViewType FURNACE = new ViewType("minecraft:furnace", 3);
	public static final ViewType CRAFTING_TABLE = new ViewType("minecraft:crafting_table", 9);
	public static final ViewType HOPPER = new ViewType("minecraft:hopper", 5);

	public static final ViewType CHEST = new ViewType("minecraft:chest", 54) {

		private static final int MAX_ROWS = 6;
		private static final int COLUMNS = 9;

		/**
		 * Normalizes the specified parameter to conform to container constraints and does not exceed
		 * or fail in an attempt to set the container size.
		 *
		 * @param size The expected size of the container.
		 * @return The size of the container according to the specified parameter.
		 */
		@Override
		public int normalize(@Range(from = 0, to = MAX_ROWS * COLUMNS) final int size) {
			if (size == 0) return size;

			if (size > MAX_ROWS) {
				if (size % COLUMNS != 0)
					throw new IllegalArgumentException(format(
						"Container size must be a multiple of %d (given: %d)",
						COLUMNS,
						size
					));

				final int fullSize = size * COLUMNS;
				if (fullSize > getMaxSize())
					throw new IllegalArgumentException(format(
						"Size cannot exceed container max size of %d (given: %d (%s rows))",
						getMaxSize(),
						fullSize,
						size
					));

				return fullSize;
			}

			return size * COLUMNS;
		}
	};

}