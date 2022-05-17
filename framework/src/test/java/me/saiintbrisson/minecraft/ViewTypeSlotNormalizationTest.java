package me.saiintbrisson.minecraft;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ViewTypeSlotNormalizationTest {

	@Test
	void returnRowsWhenSizeIsGreaterThanContainerRowsCount() {
		final ViewType type = ViewType.CHEST;
		final int expectedContainerSizeConversion = 45;
		final int userDefinedSizeAsRows = 5;

		Assertions.assertEquals(expectedContainerSizeConversion, type.normalize(userDefinedSizeAsRows));
	}

	@Test
	void shouldThrowExceptionWhenGivenRowsDontMatchTypeConstraints() {
		final ViewType type = ViewType.CHEST;
		final int userDefinedSizeAsRows = 8;

		Assertions.assertThrows(IllegalArgumentException.class, () ->  type.normalize(userDefinedSizeAsRows));
	}

	@Test
	void shouldThrowExceptionWhenGivenSizeExceedsContainerConstraints() {
		final ViewType type = ViewType.CHEST;
		final int size = type.getMaxSize() + 1;

		Assertions.assertThrows(IllegalArgumentException.class, () -> type.normalize(size));
	}

	@Test
	void shouldThrowExceptionWhenSizeIsNotModOfContainerRows() {
		final ViewType type = ViewType.CHEST;
		final int size = 11;

		Assertions.assertThrows(IllegalArgumentException.class, () -> type.normalize(size));
	}

}
