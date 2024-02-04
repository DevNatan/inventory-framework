package me.devnatan.inventoryframework.context;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import me.devnatan.inventoryframework.BukkitViewContainer;
import me.devnatan.inventoryframework.BukkitViewer;
import me.devnatan.inventoryframework.UpdateReason;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.AbstractBukkitComponent;
import me.devnatan.inventoryframework.component.BukkitComponentBuilder;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public final class RenderContext extends PlatformRenderContext<Context, BukkitComponentBuilder> implements Context {

    private final Player player;

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    public RenderContext(
            @NotNull UUID id,
            @NotNull View root,
            @NotNull ViewConfig config,
            @NotNull ViewContainer container,
            @NotNull Map<String, Viewer> viewers,
            Viewer subject,
            Object initialData) {
        super(id, root, config, container, viewers, subject, initialData);

        getPipeline().intercept(PipelinePhase.Context.CONTEXT_CLOSE, new BukkitCloseCancellationInterceptor());

        this.player = subject != null ? ((BukkitViewer) subject).getPlayer() : null;
    }

    @Override
    public @NotNull View getRoot() {
        return (View) root;
    }

    public @NotNull Player getPlayer() {
        tryThrowDoNotWorkWithSharedContext("getAllPlayers");
        return player;
    }

    @Override
    public List<Player> getAllPlayers() {
        return getViewers().stream()
                .map(viewer -> (BukkitViewer) viewer)
                .map(BukkitViewer::getPlayer)
                .collect(Collectors.toList());
    }

    @Override
    public void updateTitleForPlayer(@NotNull String title, @NotNull Player player) {
        ((BukkitViewContainer) getContainer()).changeTitle(title, player);
    }

    @Override
    public void resetTitleForPlayer(@NotNull Player player) {
        ((BukkitViewContainer) getContainer()).changeTitle(null, player);
    }

    @Override
    protected BukkitComponentBuilder createComponentBuilder() {
        return new BukkitComponentBuilder();
    }

    // region Platform Contexts Factory
    @Override
    IFComponentRenderContext createComponentRenderContext(Component component, boolean force) {
        return new ComponentRenderContext(this, (AbstractBukkitComponent<?>) component, getViewer());
    }

    @Override
    IFComponentUpdateContext createComponentUpdateContext(Component component, boolean force, UpdateReason reason) {
        return new ComponentUpdateContext(this, component, getViewer(), reason);
    }

    @Override
    IFComponentClearContext createComponentClearContext(Component component) {
        return new ComponentClearContext(this, component, getViewer());
    }
    // endregion

    // region Components Rendering
    /**
     * Adds an item to a specific slot in the context container.
     *
     * @param slot The slot in which the item will be positioned.
     * @return An item builder to configure the item.
     */
    public BukkitComponentBuilder slot(int slot, ItemStack item) {
        return slot(slot).withItem(item);
    }

    /**
     * Adds an item at the specific column and ROW (X, Y) in that context's container.
     *
     * @param row    The row (Y) in which the item will be positioned.
     * @param column The column (X) in which the item will be positioned.
     * @return An item builder to configure the item.
     */
    public BukkitComponentBuilder slot(int row, int column, ItemStack item) {
        return slot(row, column).withItem(item);
    }

    /**
     * Sets an item in the first slot of this context's container.
     *
     * @return An item builder to configure the item.
     */
    public BukkitComponentBuilder firstSlot(ItemStack item) {
        return firstSlot().withItem(item);
    }

    /**
     * Sets an item in the last slot of this context's container.
     *
     * @return An item builder to configure the item.
     */
    public BukkitComponentBuilder lastSlot(ItemStack item) {
        return lastSlot().withItem(item);
    }

    /**
     * Adds an item in the next available slot of this context's container.
     *
     * @return An item builder to configure the item.
     */
    public BukkitComponentBuilder availableSlot(ItemStack item) {
        return availableSlot().withItem(item);
    }

    /**
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    public BukkitComponentBuilder resultSlot(ItemStack item) {
        return resultSlot().withItem(item);
    }
    // endregion

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
