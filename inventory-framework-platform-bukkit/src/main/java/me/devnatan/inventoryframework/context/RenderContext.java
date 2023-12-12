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
import me.devnatan.inventoryframework.component.BukkitItemComponentBuilder;
import me.devnatan.inventoryframework.component.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("rawtypes")
public final class RenderContext extends PlatformRenderContext<Context, BukkitItemComponentBuilder, ItemStack>
        implements Context {

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
    protected BukkitItemComponentBuilder createItemBuilder() {
        return new BukkitItemComponentBuilder();
    }

    // region Platform Contexts Factory
    @Override
    IFComponentRenderContext createComponentRenderContext(Component component, boolean force) {
        return new ComponentRenderContext(this, component, getViewer());
    }

    @Override
    IFComponentUpdateContext createComponentUpdateContext(Component component, boolean force, UpdateReason reason) {
        return new ComponentUpdateContext(this, component, getViewer());
    }

    @Override
    IFComponentClearContext createComponentClearContext(Component component) {
        return new ComponentClearContext(this, component, getViewer());
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
