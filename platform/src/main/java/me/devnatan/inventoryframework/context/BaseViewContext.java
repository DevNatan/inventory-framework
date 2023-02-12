package me.devnatan.inventoryframework.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentComposition;
import me.devnatan.inventoryframework.internal.state.DefaultStateHolder;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateHolder;
import me.devnatan.inventoryframework.state.StateValueHolder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

@ApiStatus.Internal
class BaseViewContext implements IFContext {

    private final @NotNull RootView root;

    /* container can be null on pre-render/intermediate contexts */
    private final @Nullable ViewContainer container;

    private final StateHolder stateHolder = new DefaultStateHolder();
    protected final Map<String, Viewer> viewers = new HashMap<>();
    protected final ViewConfig config;
    private final List<Component> components = new ArrayList<>();

    public BaseViewContext(@NotNull RootView root, @Nullable ViewContainer container) {
        this.root = root;
        this.container = container;
        this.config = root.getConfig();
    }

    @Override
    public final @NotNull ViewConfig getConfig() {
        return config;
    }

    @Override
    public final @NotNull RootView getRoot() {
        return root;
    }

    @Override
    public final @NotNull ViewContainer getContainer() {
        if (container == null) throw new IllegalStateException("Unable to get null container");
        return container;
    }

    @Override
    public final @NotNull @Unmodifiable Set<Viewer> getViewers() {
        return Collections.unmodifiableSet(new HashSet<>(getIndexedViewers().values()));
    }

    @Override
    public @NotNull @UnmodifiableView Map<String, Viewer> getIndexedViewers() {
        return Collections.unmodifiableMap(viewers);
    }

    @Override
    public final void addViewer(@NotNull Viewer viewer) {
        synchronized (viewers) {
            viewers.put(viewer.getId(), viewer);
        }
    }

    @Override
    public final void removeViewer(@NotNull Viewer viewer) {
        synchronized (viewers) {
            viewers.remove(viewer.getId());
        }
    }

    @Override
    public @NotNull String getTitle() {
        return getUpdatedTitle() == null ? getInitialTitle() : getUpdatedTitle();
    }

    @Override
    public final @NotNull String getInitialTitle() {
        return container.getTitle();
    }

    @Override
    public final @Nullable String getUpdatedTitle() {
        return getContainer().getTitle();
    }

    @Override
    public final void updateTitle(@NotNull String title) {
        getContainer().changeTitle(title);
    }

    @Override
    public final void resetTitle() {
        getContainer().changeTitle(null);
    }

    @Override
    public void close() {
        getContainer().close();
    }

    @Override
    public final void open(Class<? extends RootView> other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final long generateId() {
        return stateHolder.generateId();
    }

    @Override
    public final StateValueHolder retrieve(long id) {
        return stateHolder.retrieve(id);
    }

    @Override
    public final void updateCaught(@NotNull State<?> state, Object oldValue, Object newValue) {
        stateHolder.updateCaught(state, oldValue, newValue);
    }

    @Override
    public final StateValueHolder createUnchecked(Object initialValue) {
        return stateHolder.createUnchecked(initialValue);
    }

    @Override
    public final <T> void watch(@NotNull State<?> state, @NotNull BiConsumer<T, T> callback) {
        stateHolder.watch(state, callback);
    }

    @Override
    public final @UnmodifiableView @NotNull List<Component> getComponents() {
        return Collections.unmodifiableList(components);
    }

    @Override
    public final Component getComponent(int position) {
        for (final Component component : getComponents()) {
            if (component instanceof ComponentComposition
                    && ((ComponentComposition) component).isContainedWithin(position)) {
                return component;
            }

            if (component.getPosition() == position) return component;
        }
        return null;
    }

    @Override
    public final void addComponent(@NotNull Component component) {
        synchronized (components) {
            components.add(component);
        }
    }

    @Override
    public final void removeComponent(@NotNull Component component) {
        synchronized (components) {
            components.remove(component);
        }
    }
}
