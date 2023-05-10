package me.devnatan.inventoryframework.context;

import java.util.concurrent.CompletableFuture;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.devnatan.inventoryframework.InventoryFrameworkException;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.runtime.BukkitViewer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class OpenContext extends ConfinedContext implements IFOpenContext, Context {

    private final Player player;
    private boolean cancelled;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private CompletableFuture<Void> waitTask;

    @Getter(AccessLevel.PRIVATE)
    private ViewConfigBuilder inheritedConfigBuilder;

    @ApiStatus.Internal
    public OpenContext(@NotNull RootView root, @NotNull Viewer viewer) {
        super(root, null, viewer);
        this.player = ((BukkitViewer) viewer).getPlayer();
    }

    @Override
    public @NotNull IFContext getParent() {
        throw new RuntimeException("OpenContext parent can't be accessed");
    }

    @Override
    public void closeForEveryone() {
        unsupportedOperation("#setCancelled(true)");
    }

    @Override
    public void closeForPlayer() {
        unsupportedOperation("#setCancelled(true)");
    }

    @Override
    public void openForPlayer(@NotNull Class<? extends RootView> other) {
        unsupportedOperation();
    }

    @Override
    public void resetTitleForPlayer() {
        unsupportedOperation();
    }

    @Override
    public void updateTitleForPlayer(@NotNull String title) {
        unsupportedOperation();
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
        if (inheritedConfigBuilder == null) inheritedConfigBuilder = new ViewConfigBuilder();

        return inheritedConfigBuilder;
    }

    private void unsupportedOperation() {
        throw new InventoryFrameworkException(
                new IllegalStateException("This operation cannot be called on open handler."));
    }

    private void unsupportedOperation(String usage) {
        throw new InventoryFrameworkException(new IllegalStateException(
                String.format("This operation cannot be called on open handler. Use %s instead.", usage)));
    }
}
