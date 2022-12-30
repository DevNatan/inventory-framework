package me.devnatan.inventoryframework.bukkit;

import me.saiintbrisson.minecraft.PlatformUtils;
import me.saiintbrisson.minecraft.ViewComponentFactory;
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
