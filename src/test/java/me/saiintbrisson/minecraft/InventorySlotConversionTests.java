package me.saiintbrisson.minecraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InventorySlotConversionTests {

	@Test
	public void shouldConvertRowAndColumnToSlot() {
		final int row = 6;
		final int column = 3;

		assertEquals(47, VirtualView.toSlot(row, column));
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenColumnExceedsTheLimit() {
		assertThrows(IllegalArgumentException.class, () -> new View(3).toSlot0(4, 7));
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenSlotExceedsViewSize() {
		assertThrows(IllegalArgumentException.class, () -> new View(3).toSlot0(4, 3));
	}

}
