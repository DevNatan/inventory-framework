package me.devnatan.inventoryframework.context;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import me.devnatan.inventoryframework.BukkitViewContainer;
import me.devnatan.inventoryframework.BukkitViewer;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public final class CloseContext extends ConfinedContext implements IFCloseContext, Context {

    private final IFContext parent;
    private final Player player;
    private boolean cancelled;

    public CloseContext(
            @NotNull RootView root,
            @NotNull ViewContainer container,
            Viewer subject,
            @NotNull Map<String, Viewer> viewers,
            @NotNull IFContext parent) {
        super(root, container, subject, viewers, parent.getInitialData());
        this.player = ((BukkitViewer) subject).getPlayer();
        this.parent = parent;
    }

    public IFContext getParent() {
        return parent;
    }

    @NotNull
    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public @UnmodifiableView List<Player> getAllPlayers() {
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
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public @NotNull UUID getId() {
        return getParent().getId();
    }

    @Override
    public @NotNull ViewConfig getConfig() {
        return getParent().getConfig();
    }

    @Override
    public void closeForEveryone() {}

    @Override
    public void closeForPlayer() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CloseContext that = (CloseContext) o;
        return isCancelled() == that.isCancelled()
                && Objects.equals(getParent(), that.getParent())
                && Objects.equals(getPlayer(), that.getPlayer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getParent(), getPlayer(), isCancelled());
    }

    @Override
    public String toString() {
        return "CloseContext{" + "parent="
                + parent + ", player="
                + player + ", cancelled="
                + cancelled + "} "
                + super.toString();
    }
}
