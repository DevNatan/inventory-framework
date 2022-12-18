package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.devnatan.inventoryframework.IFContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@ToString
@Getter
@RequiredArgsConstructor
class BukkitViewer implements Viewer {

    @NotNull
    private final Player player;

    @Override
    public void open(@NotNull final ViewContainer container) {
        if (!(container instanceof BukkitViewContainer))
            throw new IllegalArgumentException("Only BukkitViewContainer is supported");

        player.openInventory(((BukkitViewContainer) container).getInventory());
    }

    @Override
    public void close() {
        player.closeInventory();
    }

    public static Player toPlayerOfContext(IFContext context) {
        final Viewer viewer = context.getViewers().get(0);
        if (viewer == null)
            throw new IllegalStateException("Tried to retrieve context player while it's not valid anymore.");

        return ((BukkitViewer) viewer).getPlayer();
    }
}
