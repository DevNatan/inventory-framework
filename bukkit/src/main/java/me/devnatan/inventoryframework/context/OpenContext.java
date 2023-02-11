package me.devnatan.inventoryframework.context;

import java.util.concurrent.CompletableFuture;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.bukkit.BukkitViewer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public final class OpenContext extends ConfinedContext implements IFOpenContext, Context {

    private final Player player;

    private String title;
    private int size;
    private ViewType type;
    private boolean cancelled;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private CompletableFuture<Void> waitTask;

    @ApiStatus.Internal
    public OpenContext(@NotNull RootView root, @NotNull Viewer viewer) {
        super(root, null, viewer);
        this.title = root.getConfig().getTitle();
        this.size = root.getConfig().getSize();
        this.type = root.getConfig().getType();
        this.player = ((BukkitViewer) viewer).getPlayer();
    }

    @Override
    public @NotNull String getTitle() {
        return title == null ? super.getTitle() : title;
    }

    @Override
    public CompletableFuture<Void> getAsyncOpenJob() {
        return waitTask;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public void waitUntil(@NotNull CompletableFuture<Void> task) {
        this.waitTask = task;
    }

    @Override
    public @Nullable IFItem<?> getItem(int index) {
        throw new IllegalStateException(
                "Container has not yet been created in the opening phase, so items cannot be obtained.");
    }
}
