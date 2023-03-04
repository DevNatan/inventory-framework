package me.devnatan.inventoryframework.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static java.lang.String.format;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SlotConverter {

	public static int convertSlot(int row, int column, int maxRowsCount, int maxColumnsCount) {
		if (row > maxRowsCount)
			throw new IllegalArgumentException(format("Row cannot be greater than %d (given %d)", maxRowsCount, row));

		if (column > maxColumnsCount)
			throw new IllegalArgumentException(format("Column cannot be greater than %d (given %d)", maxColumnsCount, column));

		return Math.max(row - 1, 0) * maxColumnsCount + Math.max(column - 1, 0);
	}


}
