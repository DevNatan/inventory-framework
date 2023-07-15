package me.devnatan.inventoryframework.context;

import java.util.Objects;
import java.util.UUID;
import me.devnatan.inventoryframework.BukkitViewer;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.BukkitItemComponentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RenderContext extends PlatformRenderContext<BukkitItemComponentBuilder> implements Context {

    private final Player player;

    @ApiStatus.Internal
    public RenderContext(
            UUID id, RootView root, ViewContainer container, Viewer viewer, ViewConfig config, Object initialData) {
        super(id, root, container, viewer, config, initialData);
        this.player = ((BukkitViewer) viewer).getPlayer();
    }

    @NotNull
    @Override
    public Player getPlayer() {
        return player;
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
        return new BukkitItemComponentBuilder(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RenderContext that = (RenderContext) o;
        return Objects.equals(getPlayer(), that.getPlayer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPlayer());
    }

    @Override
    public String toString() {
        return "RenderContext{" + "player=" + player + "} " + super.toString();
    }
}
