package me.devnatan.inventoryframework;

import java.util.concurrent.CompletableFuture;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.devnatan.inventoryframework.context.IFOpenContext;
import me.devnatan.inventoryframework.internal.context.BaseViewContext;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public final class ViewOpenContext extends BaseViewContext implements IFOpenContext, ViewContext {

    private final Player player;

    private String title;
    private int size;
    private ViewType type;
    private boolean cancelled;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private CompletableFuture<Void> waitTask;

    ViewOpenContext(@NotNull RootView root, @NotNull ViewContainer container, @NotNull Player player) {
        super(root, container);
        this.title = root.getConfig().getTitle();
        this.size = root.getConfig().getSize();
        this.type = root.getConfig().getType();
        this.player = player;
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
}
