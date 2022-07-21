package me.saiintbrisson.minecraft;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import me.saiintbrisson.minecraft.bukkit.FakeInventory;
import me.saiintbrisson.minecraft.bukkit.FakeItemStack;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

public class BukkitViewContainerTest {

    @Test
    void testFirstSlotMatch() {
        ViewContainer container = new TestBukkitViewContainer();

        // minecraft first slot is always zero
        assertEquals(0, container.getFirstSlot());
    }

    @Test
    void testLastSlotMatch() {
        ViewContainer container = new TestBukkitViewContainer();
        int size = container.getSlotsCount();
        assertEquals(size, container.getLastSlot());
    }

    @Test
    void testItemSuccessfulRender() {
        ViewContainer container = new TestBukkitViewContainer();
        ItemStack stack = new FakeItemStack(Material.ITEM_FRAME);
        int slot = 1;

        assertDoesNotThrow(() -> container.renderItem(slot, stack));
        assertTrue(container.hasItem(slot));
    }

    @Test
    void testItemSuccessfulRenderAndMatch() {
        ViewContainer container = new TestBukkitViewContainer();
        ItemStack stack = new FakeItemStack(Material.ITEM_FRAME);
        int slot = 1;

        assertDoesNotThrow(() -> container.renderItem(slot, stack));
        assertTrue(container.hasItem(slot));
        assertTrue(container.matchesItem(slot, stack, true));
    }

    @Test
    void shouldThrowExceptionWhenRenderedSlotExceedsInventorySize() {
        ViewContainer container = new TestBukkitViewContainer(new FakeInventory(0, InventoryType.CHEST));
        ItemStack stack = new FakeItemStack(Material.ITEM_FRAME);
        int slot = 54;

        assertThrows(IndexOutOfBoundsException.class, () -> container.renderItem(slot, stack));
    }
}
