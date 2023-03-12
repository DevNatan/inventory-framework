package me.devnatan.inventoryframework.context;

import java.util.concurrent.CompletableFuture;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.bukkit.BukkitViewer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

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

    @Getter(AccessLevel.PRIVATE)
    private final ViewConfigBuilder inheritedConfigBuilder = new ViewConfigBuilder();

    @ApiStatus.Internal
    public OpenContext(@NotNull RootView root, @NotNull Viewer viewer) {
        super(root, null, viewer);
        this.player = ((BukkitViewer) viewer).getPlayer();
    }

    @Override
    public @NotNull String getTitle() {
        return title == null ? getRoot().getConfig().getTitle() : title;
    }

    @Override
    public int getSize() {
        return size == 0 ? getRoot().getConfig().getSize() : size;
    }

    @Override
    public @NotNull ViewType getType() {
        if (type != null) return type;

        final ViewType rootType = getRoot().getConfig().getType();
        if (rootType == null) return ViewType.CHEST;

        return rootType;
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
    public @NotNull ViewConfigBuilder modifyConfig() {
        return inheritedConfigBuilder;
    }
}
