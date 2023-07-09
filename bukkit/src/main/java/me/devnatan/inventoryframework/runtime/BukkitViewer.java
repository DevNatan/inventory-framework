package me.devnatan.inventoryframework.runtime;

import java.util.Objects;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class BukkitViewer implements Viewer {

    private final Player player;
    private ViewContainer container;

    public BukkitViewer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public ViewContainer getContainer() {
        return container;
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
        if (getContainer() == null)
            container = new BukkitViewContainer(getPlayer().getInventory(), false, null);

        return getContainer();
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
        return "BukkitViewer{" + "player=" + player + ", container=" + container + '}';
    }
}
