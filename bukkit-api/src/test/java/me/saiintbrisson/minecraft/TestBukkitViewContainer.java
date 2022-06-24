package me.saiintbrisson.minecraft;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import me.saiintbrisson.minecraft.bukkit.FakeInventory;
import me.saiintbrisson.minecraft.bukkit.FakeItemStack;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit container implementation that overrides item conversion method to prevent ItemFactory
 * calls since its only available in a real server so tests will fail because there's no instance of
 * this.
 */
@AllArgsConstructor
@ToString(callSuper = true)
class TestBukkitViewContainer extends BukkitViewContainer {

    @Getter private final Inventory inventory;
    private final ViewType type = ViewType.CHEST;

    public TestBukkitViewContainer() {
        this(new FakeInventory(54, InventoryType.CHEST));
    }

    public TestBukkitViewContainer(
            @NotNull ViewType viewType, @NotNull InventoryType inventoryType) {
        this(new FakeInventory(viewType.getMaxSize(), inventoryType));
    }

    @Override
    public ItemStack convertItem(Object source) {
        requireSupportedItem(source);

        if (source instanceof ItemStack) return new FakeItemStack((ItemStack) source);
        if (source instanceof Material) return new FakeItemStack((Material) source);

        return null;
    }

    @Override
    public int getRowsCount() {
        return 0;
    }

    @Override
    public int getColumnsCount() {
        return 0;
    }

    @Override
    public @NotNull ViewType getType() {
        return type;
    }
}
