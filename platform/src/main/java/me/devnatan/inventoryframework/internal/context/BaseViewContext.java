package me.devnatan.inventoryframework.internal.context;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.DefaultVirtualViewImpl;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import me.devnatan.inventoryframework.internal.platform.Viewer;
import me.devnatan.inventoryframework.internal.state.DefaultStateHolder;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateHolder;
import me.devnatan.inventoryframework.state.StateValueHolder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

@RequiredArgsConstructor
@ApiStatus.Internal
public class BaseViewContext implements IFContext {

    private final @NotNull RootView root;
    private final @NotNull ViewContainer container;

    private final StateHolder stateHolder = new DefaultStateHolder();
    private final VirtualView virtualView = new DefaultVirtualViewImpl();

    protected final Set<Viewer> viewers = new HashSet<>();

    private String updatedTitle;

    @Override
    public final @NotNull RootView getRoot() {
        return root;
    }

    @Override
    public final @NotNull ViewContainer getContainer() {
        return container;
    }

    @Override
    public final @NotNull Set<Viewer> getViewers() {
        return Collections.unmodifiableSet(viewers);
    }

    @Override
    public final void addViewer(@NotNull Viewer viewer) {
        synchronized (viewers) {
            viewers.add(viewer);
        }
    }

    @Override
    public final void removeViewer(@NotNull Viewer viewer) {
        synchronized (viewers) {
            viewers.remove(viewer);
        }
    }

    @Override
    public final @NotNull String getTitle() {
        return getUpdatedTitle() == null ? getInitialTitle() : getUpdatedTitle();
    }

    @Override
    public final @NotNull String getInitialTitle() {
        return container.getTitle();
    }

    @Override
    public final @Nullable String getUpdatedTitle() {
        return updatedTitle;
    }

    @Override
    public final void updateTitle(@NotNull String title) {
        this.updatedTitle = title;
        getContainer().changeTitle(title);
    }

    @Override
    public final void resetTitle() {
        this.updatedTitle = null;
        getContainer().changeTitle(null);
    }

    @Override
    public final void close() {
        getContainer().close();
    }

    @Override
    public final void open(Class<? extends RootView> other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final <T> T get(Class<? extends T> state) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final @UnmodifiableView List<Component> getComponents() {
        return virtualView.getComponents();
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
}
