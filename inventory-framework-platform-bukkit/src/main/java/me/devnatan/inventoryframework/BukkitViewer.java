package me.devnatan.inventoryframework;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;
import me.devnatan.inventoryframework.context.IFRenderContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class BukkitViewer implements Viewer {

    private final Player player;
    private ViewContainer selfContainer;
    private IFRenderContext activeContext;
    private Deque<IFRenderContext> previousContexts = new LinkedList<>();
    private long lastInteractionInMillis;
    private boolean transitioning;

    public BukkitViewer(@NotNull Player player, IFRenderContext activeContext) {
        this.player = player;
        this.activeContext = activeContext;
    }

    public Player getPlayer() {
        return player;
    }

    @NotNull
    @Override
    public IFRenderContext getActiveContext() {
        return activeContext;
    }

    @Override
    public void setActiveContext(@NotNull IFRenderContext context) {
        this.activeContext = context;
    }

    @Override
    public @NotNull String getId() {
        return getPlayer().getUniqueId().toString();
    }

    @Override
    public void open(@NotNull final ViewContainer container) {
        getPlayer().openInventory(((BukkitViewContainer) container).getInventory());
    }

    @Override
    public void close() {
        getPlayer().closeInventory();
    }

    @Override
    public @NotNull ViewContainer getSelfContainer() {
        if (selfContainer == null)
            selfContainer = new BukkitViewContainer(
                    getPlayer().getInventory(), getActiveContext().isShared(), ViewType.PLAYER, false);

        return selfContainer;
    }

    @Override
    public long getLastInteractionInMillis() {
        return lastInteractionInMillis;
    }

    @Override
    public void setLastInteractionInMillis(long lastInteractionInMillis) {
        this.lastInteractionInMillis = lastInteractionInMillis;
    }

    @Override
    public boolean isBlockedByInteractionDelay() {
        final long configuredDelay = activeContext.getConfig().getInteractionDelayInMillis();
        if (configuredDelay <= 0 || getLastInteractionInMillis() <= 0) return false;

        return getLastInteractionInMillis() + configuredDelay >= System.currentTimeMillis();
    }

    @Override
    public boolean isTransitioning() {
        return transitioning;
    }

    @Override
    public void setTransitioning(boolean transitioning) {
        this.transitioning = transitioning;
    }

    @Override
    public IFRenderContext getPreviousContext() {
        return previousContexts.peekLast();
    }

    @Override
    public void setPreviousContext(IFRenderContext previousContext) {
        previousContexts.add(previousContext);
    }

    @Override
    public void unsetPreviousContext() {
        previousContexts.pollLast();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BukkitViewer that = (BukkitViewer) o;
        return Objects.equals(getPlayer(), that.getPlayer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPlayer());
    }

    @Override
    public String toString() {
        return "BukkitViewer{"
                + "player=" + player
                + ", selfContainer=" + selfContainer
                + ", lastInteractionInMillis=" + lastInteractionInMillis
                + ", isTransitioning=" + transitioning
                + "}";
    }
}
