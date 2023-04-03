package me.devnatan.inventoryframework.context;

import lombok.Getter;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.runtime.BukkitViewer;
import me.devnatan.inventoryframework.component.BukkitItemComponentBuilder;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public final class RenderContext extends PlatformRenderContext<BukkitItemComponentBuilder> implements Context {

    private static final LayoutSlot filledReservedCharLayoutSlot =
            new LayoutSlot(LayoutSlot.FILLED_RESERVED_CHAR, $ -> {
                throw new UnsupportedOperationException("Cannot use factory of reserved layout character");
            });

    private final @NotNull Player player;

    @ApiStatus.Internal
    public RenderContext(RootView root, ViewContainer container, Viewer viewer, @NotNull IFContext parent) {
        super(root, container, viewer, parent);
        this.player = ((BukkitViewer) viewer).getPlayer();
    }

    /**
     * Adds an item to a specific slot in the context container.
     *
     * @param slot The slot in which the item will be positioned.
     * @return An item builder to configure the item.
     */
    public @NotNull BukkitItemComponentBuilder slot(int slot, @Nullable ItemStack item) {
        return slot(slot).withItem(item);
    }

    /**
     * Adds an item at the specific column and ROW (X, Y) in that context's container.
     *
     * @param row    The row (Y) in which the item will be positioned.
     * @param column The column (X) in which the item will be positioned.
     * @return An item builder to configure the item.
     */
    @NotNull
    public BukkitItemComponentBuilder slot(int row, int column, @Nullable ItemStack item) {
        return slot(row, column).withItem(item);
    }

    /**
     * Sets an item in the first slot of this context's container.
     *
     * @param item The item that'll be set.
     * @return An item builder to configure the item.
     */
    public @NotNull BukkitItemComponentBuilder firstSlot(@Nullable ItemStack item) {
        return firstSlot().withItem(item);
    }

    /**
     * Sets an item in the last slot of this context's container.
     *
     * @param item The item that'll be set.
     * @return An item builder to configure the item.
     */
    public @NotNull BukkitItemComponentBuilder lastSlot(@Nullable ItemStack item) {
        return lastSlot().withItem(item);
    }

    /**
     * Adds an item in the next available slot of this context's container.
     *
     * @param item The item that'll be added.
     * @return An item builder to configure the item.
     */
    public @NotNull BukkitItemComponentBuilder availableSlot(@Nullable ItemStack item) {
        return availableSlot().withItem(item);
    }

    /**
     * Defines the item that will represent a character provided in the context layout.
     *
     * @param character The layout character target.
     * @param item      The item that'll represent the layout character.
     * @return An item builder to configure the item.
     */
    public @NotNull BukkitItemComponentBuilder layoutSlot(char character, @Nullable ItemStack item) {
        return layoutSlot(character).withItem(item);
    }

    @Override
    protected BukkitItemComponentBuilder createBuilder() {
        return new BukkitItemComponentBuilder();
    }
}
