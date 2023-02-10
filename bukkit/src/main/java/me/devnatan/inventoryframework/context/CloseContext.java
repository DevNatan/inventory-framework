package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.internal.BukkitViewer;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import me.devnatan.inventoryframework.internal.platform.Viewer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class CloseContext extends ConfinedContext implements IFCloseContext, Context {

    private final Player player;
    private boolean cancelled;

    public CloseContext(@NotNull RootView root, @NotNull ViewContainer container, @NotNull Viewer viewer) {
        super(root, container, viewer);
        this.player = ((BukkitViewer) viewer).getPlayer();
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