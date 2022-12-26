package me.saiintbrisson.minecraft;

import java.util.concurrent.CompletableFuture;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.devnatan.inventoryframework.IFOpenContext;
import me.devnatan.inventoryframework.bukkit.BukkitIFContext;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public final class OpenViewContext extends BaseViewContext implements IFOpenContext, BukkitIFContext {

    private final Player player;

    private String title;
    private int size;
    private ViewType type;
    private boolean cancelled;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private CompletableFuture<Void> waitTask;

    protected OpenViewContext(@NotNull AbstractView root, @Nullable ViewContainer container, @NotNull Player player) {
        super(root, container);
        this.player = player;
    }

    @Override
    public final @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public final void waitUntil(@NotNull CompletableFuture<Void> task) {
        this.waitTask = task;
    }

    /**
     * @deprecated Use {@link #setTitle(String)} instead.
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
    public final void setContainerTitle(@Nullable String containerTitle) {
        setTitle(containerTitle);
    }

    /**
     * @deprecated Use {@link #setSize(int)} instead.
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
    public final void setContainerSize(int containerSize) {
        setSize(size);
    }

    /**
     * @deprecated Use {@link #setType(ViewType)} instead.
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
    public final void setContainerType(@Nullable ViewType containerType) {
        setType(containerType);
    }
}
