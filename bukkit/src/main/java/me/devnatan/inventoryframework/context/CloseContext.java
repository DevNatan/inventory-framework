package me.devnatan.inventoryframework.context;

import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.bukkit.BukkitViewer;
import me.devnatan.inventoryframework.state.StateHost;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public final class CloseContext extends ConfinedContext implements IFCloseContext, Context {

    private final IFContext parent;
    private final Player player;

    @Setter
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
    public @NotNull UUID getId() {
        return getParent().getId();
    }

    @Override
    public @NotNull StateHost getStateHost() {
        return getParent().getStateHost();
    }

    @Override
    public void close() {}

    @Override
    public void closeForPlayer() {}
}
