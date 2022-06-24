package me.saiintbrisson.minecraft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import me.saiintbrisson.minecraft.bukkit.FakeInventory;
import me.saiintbrisson.minecraft.bukkit.FakeItemStack;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

public class HopperViewTypeTest {

    @Test
    void testHopperSize() {
        ViewContainer container = createHopperContainer();
        assertEquals(5, container.getSize());
    }

    @Test
    void testHopperRowsCount() {
        ViewContainer container = createHopperContainer();
        assertEquals(1, container.getRowsCount());
    }

    @Test
    void testHopperColumnsCount() {
        ViewContainer container = createHopperContainer();
        assertEquals(5, container.getColumnsCount());
    }

    @Test
    void shouldThrowExceptionWhenRenderedSlotExceedsHopperInventorySize() {
        ViewContainer container = createHopperContainer();
        ItemStack stack = new FakeItemStack(Material.ITEM_FRAME);
        int slot = 5;

        assertThrows(IndexOutOfBoundsException.class, () -> container.renderItem(slot, stack));
    }

    private ViewContainer createHopperContainer() {
        return new BukkitSimpleViewContainer(
                new FakeInventory(5, InventoryType.HOPPER), ViewType.HOPPER);
    }
}
