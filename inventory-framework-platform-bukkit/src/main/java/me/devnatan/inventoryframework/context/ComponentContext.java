package me.devnatan.inventoryframework.context;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.state.StateValue;
import me.devnatan.inventoryframework.state.StateWatcher;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class ComponentContext extends PlatformConfinedContext implements IFComponentRenderContext, Context {

    private final RenderContext parent;
    private final Component component;

    public ComponentContext(RenderContext parent, Component component) {
        this.parent = parent;
        this.component = component;
    }

    @Override
    public final @NotNull String getTitle() {
        return getParent().getTitle();
    }

    @Override
    protected final void setUpdatedTitle(String updatedTitle) {
        getParent().setUpdatedTitle(updatedTitle);
    }

    @Override
    public final boolean isActive() {
        return getParent().isActive();
    }

    @Override
    public final void setActive(boolean active) {
        getParent().setActive(active);
    }

    @Override
    public final boolean isEndless() {
        return getParent().isEndless();
    }

    @Override
    public final void setEndless(boolean endless) {
        getParent().setEndless(endless);
    }

    @Override
    public IFContext getTopLevelContext() {
        return getParent();
    }

    @Override
    public RenderContext getParent() {
        return parent;
    }

    @Override
    public final Component getComponent() {
        return component;
    }

    @Override
    public final @NotNull UUID getId() {
        return getParent().getId();
    }

    @Override
    public final @NotNull ViewConfig getConfig() {
        return getParent().getConfig();
    }

    @Override
    public final Object getInitialData() {
        return getParent().getInitialData();
    }

    @Override
    public final void setInitialData(Object initialData) {
        getParent().setInitialData(initialData);
    }

    @Override
    public final List<Player> getAllPlayers() {
        return getParent().getAllPlayers();
    }

    @Override
    public final void updateTitleForPlayer(@NotNull String title, @NotNull Player player) {
        getParent().updateTitleForPlayer(title, player);
    }

    @Override
    public final void resetTitleForPlayer(@NotNull Player player) {
        getParent().resetTitleForPlayer(player);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public final @NotNull PlatformView getRoot() {
        return getParent().getRoot();
    }

    @Override
    public final Map<Long, StateValue> getStateValues() {
        return getParent().getStateValues();
    }

    @Override
    public final Map<Long, List<StateWatcher>> getStateWatchers() {
        return getParent().getStateWatchers();
    }

    @Override
    public final @NotNull Map<String, Viewer> getIndexedViewers() {
        return getParent().getIndexedViewers();
    }

    @Override
    public final void performClickInComponent(
            @NotNull Component component,
            @NotNull Viewer viewer,
            @NotNull ViewContainer clickedContainer,
            Object platformEvent,
            int clickedSlot,
            boolean combined) {
        getParent().performClickInComponent(component, viewer, clickedContainer, platformEvent, clickedSlot, combined);
    }

    @Override
    public final void update() {
        getParent().update();
    }

    @Override
    public final void closeForPlayer() {
        getParent().closeForPlayer();
    }

    @Override
    public final void openForPlayer(@NotNull Class<? extends RootView> other) {
        getParent().openForPlayer(other);
    }

    @Override
    public final void openForPlayer(@NotNull Class<? extends RootView> other, Object initialData) {
        getParent().openForPlayer(other, initialData);
    }

    @Override
    public final void updateTitleForPlayer(@NotNull String title) {
        getParent().updateTitleForPlayer(title);
    }

    @Override
    public final void resetTitleForPlayer() {
        getParent().resetTitleForPlayer();
    }

    @Override
    public final void back() {
        getParent().back();
    }

    @Override
    public final void back(Object initialData) {
        getParent().back(initialData);
    }

    @Override
    public final boolean canBack() {
        return getParent().canBack();
    }

    @Override
    public String toString() {
        return "ComponentContext{" + "parent=" + parent + ", component=" + component + "} " + super.toString();
    }
}
