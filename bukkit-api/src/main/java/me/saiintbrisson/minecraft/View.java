package me.saiintbrisson.minecraft;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit platform View backward compatible implementation.
 */
public class View extends AbstractView implements InventoryHolder {

    /**
     * @deprecated Use {@link #onInit()} instead.
     */
    @Deprecated
    public View() {
        this(0);
    }

    /**
     * @deprecated Use {@link #onInit()} instead.
     */
    @Deprecated
    public View(int size) {
        this(size, null);
    }

    /**
     * @deprecated Use {@link #onInit()} instead.
     */
    @Deprecated
    public View(String title) {
        this(0, title);
    }

    /**
     * @deprecated Use {@link #onInit()} instead.
     */
    @Deprecated
    public View(String title, @NotNull ViewType type) {
        this(0, title, type);
    }

    /**
     * @deprecated Use {@link #onInit()} instead.
     */
    @Deprecated
    public View(@NotNull ViewType type) {
        this(0, null, type);
    }

    /**
     * @deprecated Use {@link #onInit()} instead.
     */
    @Deprecated
    public View(int size, String title) {
        this(size, title, ViewType.CHEST);
    }

    /**
     * @deprecated Use {@link #onInit()} instead.
     */
    @Deprecated
    public View(int size, String title, @NotNull ViewType type) {
        super(size, title, type);
    }

    @NotNull
    @Override
    public final Inventory getInventory() {
        throw new IllegalStateException("View inventory cannot be accessed");
    }

    @Override
    public final String toString() {
        return super.toString();
    }
}
