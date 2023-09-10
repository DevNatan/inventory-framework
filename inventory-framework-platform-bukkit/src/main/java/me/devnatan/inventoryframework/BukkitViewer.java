package me.devnatan.inventoryframework;

import java.util.Objects;
import me.devnatan.inventoryframework.context.IFRenderContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class BukkitViewer implements Viewer {

    private final Player player;
    private ViewContainer selfContainer;
    private IFRenderContext context;
    private long lastInteractionInMillis;

    public BukkitViewer(@NotNull Player player, IFRenderContext context) {
        this(player, null, context);
    }

    private BukkitViewer(@NotNull Player player, @NotNull ViewContainer selfContainer, IFRenderContext context) {
        this.player = player;
        this.selfContainer = selfContainer;
        this.context = context;
    }

    public Player getPlayer() {
        return player;
    }

    @NotNull
    @Override
    public IFRenderContext getContext() {
        return context;
    }

    @Override
    public void setContext(IFRenderContext context) {
        this.context = context;
    }

    @Override
    public Viewer withContext(IFRenderContext context) {
        return new BukkitViewer(player, selfContainer, context);
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
                    getPlayer().getInventory(), getContext().isShared(), ViewType.PLAYER);

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
        final long configuredDelay = context.getConfig().getInteractionDelayInMillis();
        if (configuredDelay <= 0) return false;

        final long now = System.currentTimeMillis();
        return getLastInteractionInMillis() + configuredDelay < now;
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
        return "BukkitViewer{" + "player=" + player + ", selfContainer=" + selfContainer + ", lastInteractionInMillis="
                + lastInteractionInMillis + "}";
    }
}
