package me.devnatan.inventoryframework.context;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import me.devnatan.inventoryframework.BukkitViewer;
import me.devnatan.inventoryframework.InventoryFrameworkException;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.Viewer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public class OpenContext extends ConfinedContext implements IFOpenContext, Context {

    private final Player player;
    private boolean cancelled;
    private CompletableFuture<Void> waitTask;
    private ViewConfigBuilder inheritedConfigBuilder;

    @ApiStatus.Internal
    public OpenContext(
            @NotNull RootView root, Viewer subject, @NotNull Map<String, Viewer> viewers, Object initialData) {
        super(root, null, subject, viewers, initialData);
        this.player = subject != null ? ((BukkitViewer) subject).getPlayer() : null;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    private ViewConfigBuilder getInheritedConfigBuilder() {
        return inheritedConfigBuilder;
    }

    public void setInheritedConfigBuilder(ViewConfigBuilder inheritedConfigBuilder) {
        this.inheritedConfigBuilder = inheritedConfigBuilder;
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
    public void openForPlayer(@NotNull Class<? extends RootView> other, Object initialData) {
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
    public void updateTitleForPlayer(@NotNull String title, @NotNull Player player) {
        unsupportedOperation();
    }

    @Override
    public void resetTitleForPlayer(@NotNull Player player) {
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
    public @UnmodifiableView List<Player> getAllPlayers() {
        return getViewers().stream()
                .map(viewer -> (BukkitViewer) viewer)
                .map(BukkitViewer::getPlayer)
                .collect(Collectors.toList());
    }

    @Override
    public void waitUntil(@NotNull CompletableFuture<Void> task) {
        this.waitTask = task;
    }

    @Override
    public @NotNull ViewConfig getConfig() {
        return inheritedConfigBuilder == null
                ? super.getConfig()
                : inheritedConfigBuilder.build().merge(getRoot().getConfig());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OpenContext that = (OpenContext) o;
        return isCancelled() == that.isCancelled()
                && Objects.equals(getPlayer(), that.getPlayer())
                && Objects.equals(getInheritedConfigBuilder(), that.getInheritedConfigBuilder());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPlayer(), isCancelled(), getInheritedConfigBuilder());
    }

    @Override
    public String toString() {
        return "OpenContext{" + "player="
                + player + ", cancelled="
                + cancelled + ", waitTask="
                + waitTask + ", inheritedConfigBuilder="
                + inheritedConfigBuilder + "} "
                + super.toString();
    }
}