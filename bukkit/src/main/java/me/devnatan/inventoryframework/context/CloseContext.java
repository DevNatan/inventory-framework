package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.bukkit.BukkitViewer;
import me.devnatan.inventoryframework.state.StateHost;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class CloseContext extends ConfinedContext implements IFCloseContext, Context {

    private final IFContext parent;
    private final Player player;
    private boolean cancelled;

    public CloseContext(
            @NotNull RootView root,
            @NotNull ViewContainer container,
            @NotNull Viewer viewer,
            @NotNull IFContext parent) {
        super(root, container, viewer);
        this.player = ((BukkitViewer) viewer).getPlayer();
        this.parent = parent;
    }

    @Override
    public @NotNull StateHost getStateHost() {
        return parent.getStateHost();
    }

    @Override
    public void close() {}

    @Override
    public void closeForPlayer() {}

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
