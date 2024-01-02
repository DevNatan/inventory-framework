package me.devnatan.inventoryframework.context;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.devnatan.inventoryframework.BukkitViewer;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import me.devnatan.inventoryframework.state.StateValue;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public final class CloseContext extends PlatformConfinedContext implements IFCloseContext, Context {

    private final Viewer subject;
    private final Player player;
    private final IFRenderContext parent;

    private boolean cancelled;

    @ApiStatus.Internal
    public CloseContext(@NotNull Viewer subject, @NotNull IFRenderContext parent) {
        this.subject = subject;
        this.player = ((BukkitViewer) subject).getPlayer();
        this.parent = parent;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public void simulateCloseForPlayer() {
        getPipeline().execute(PipelinePhase.Context.CONTEXT_CLOSE, this);
    }

    @Override
    public List<Player> getAllPlayers() {
        return getParent().getAllPlayers();
    }

    @Override
    public void updateTitleForPlayer(@NotNull String title, @NotNull Player player) {
        getParent().updateTitleForPlayer(title, player);
    }

    @Override
    public void resetTitleForPlayer(@NotNull Player player) {
        getParent().resetTitleForPlayer(player);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public @NotNull Viewer getViewer() {
        return subject;
    }

    @Override
    public RenderContext getParent() {
        return (RenderContext) parent;
    }

    @Override
    public @NotNull UUID getId() {
        return getParent().getId();
    }

    @Override
    public @NotNull ViewConfig getConfig() {
        return getParent().getConfig();
    }

    @Override
    public @NotNull ViewContainer getContainer() {
        return getParent().getContainer();
    }

    @Override
    public @NotNull View getRoot() {
        return getParent().getRoot();
    }

    @Override
    public Object getInitialData() {
        return getParent().getInitialData();
    }

    @Override
    public void setInitialData(Object initialData) {
        getParent().setInitialData(initialData);
    }

    @Override
    public Map<Long, StateValue> getStateValues() {
        return getParent().getStateValues();
    }

    @Override
    public String toString() {
        return "CloseContext{" + "subject="
                + subject + ", player="
                + player + ", parent="
                + parent + ", cancelled="
                + cancelled + "} "
                + super.toString();
    }
}
