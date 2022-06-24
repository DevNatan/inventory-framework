package me.saiintbrisson.minecraft;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BukkitViewComponentFactoryRetrievalTest {

    @Test
    void shouldRetrieveBukkitFactoryByDefault() {
        PlatformUtils.removeFactory();
        ViewComponentFactory factory = Assertions.assertDoesNotThrow(PlatformUtils::getFactory);
        Assertions.assertTrue(factory instanceof BukkitViewComponentFactory);
    }
}
