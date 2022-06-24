package me.saiintbrisson.minecraft;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PlatformUtilsTest {

    @Test
    void shouldThrowExceptionWhenNoFactoryIsAvailable() {
        Assertions.assertThrows(IllegalStateException.class, PlatformUtils::getFactory);
    }

    @Test
    void shouldThrowExceptionWhenFactoryWontWorkInTheCurrentPlatform() {
        PlatformUtils.setFactory(new PlatformTestViewComponentFactory(false));
        Assertions.assertThrows(IllegalStateException.class, PlatformUtils::getFactory);
    }

    @AfterEach
    void cleanupFactory() {
        PlatformUtils.removeFactory();
    }

    @AllArgsConstructor
    private static class PlatformTestViewComponentFactory extends NoopViewComponentFactory {

        private final boolean worksInCurrentPlatform;

        @Override
        public boolean worksInCurrentPlatform() {
            return worksInCurrentPlatform;
        }
    }
}
