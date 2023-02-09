package me.devnatan.inventoryframework.context;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.DefaultVirtualViewImpl;
import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfig;
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

@ApiStatus.Internal
class BaseViewContext implements IFContext {

    private final @NotNull RootView root;

	/* container can be null on pre-render/intermediate contexts */
	private final @Nullable ViewContainer container;

    private final StateHolder stateHolder = new DefaultStateHolder();
    private final VirtualView virtualView = new DefaultVirtualViewImpl();
    protected final Set<Viewer> viewers = new HashSet<>();
	protected final ViewConfig config;

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
        if (container == null)
			throw new IllegalStateException("Unable to get null container");
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

	@Override
	public @Nullable IFItem<?> getItem(int index) {
		throw new UnsupportedOperationException();
	}
}
