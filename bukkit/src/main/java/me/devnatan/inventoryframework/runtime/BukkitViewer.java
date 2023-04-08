package me.devnatan.inventoryframework.runtime;

import lombok.Data;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@Data
@ApiStatus.Internal
public final class BukkitViewer implements Viewer {

    private final Player player;
    private ViewContainer container;

    @Override
    public @NotNull String getId() {
        return player.getUniqueId().toString();
    }

    @Override
    public void open(@NotNull final ViewContainer container) {
        player.openInventory(((BukkitViewContainer) container).getInventory());
    }

    @Override
    public void close() {
        player.closeInventory();
    }

    @Override
    public @NotNull ViewContainer getSelfContainer() {
        if (container == null) container = new BukkitViewContainer(player.getInventory(), false, null);

        return container;
    }
}
