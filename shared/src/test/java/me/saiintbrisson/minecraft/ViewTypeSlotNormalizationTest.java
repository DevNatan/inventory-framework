package me.saiintbrisson.minecraft;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ViewTypeSlotNormalizationTest {

    @Test
    void returnRowsWhenSizeIsGreaterThanContainerRowsCount() {
        ViewType type = ViewType.CHEST;
        int expectedContainerSize = 45;
        int givenContainerSize = 5;

        Assertions.assertEquals(expectedContainerSize, type.normalize(givenContainerSize));
    }

    @Test
    void shouldThrowExceptionWhenGivenRowsDontMatchTypeConstraints() {
        ViewType type = ViewType.CHEST;
        int givenContainerSize = 8;

        Assertions.assertThrows(
                IllegalArgumentException.class, () -> type.normalize(givenContainerSize));
    }

    @Test
    void shouldThrowExceptionWhenGivenSizeExceedsContainerConstraints() {
        ViewType type = ViewType.CHEST;
        int givenContainerSize = type.getMaxSize() + 1;

        Assertions.assertThrows(
                IllegalArgumentException.class, () -> type.normalize(givenContainerSize));
    }
}
