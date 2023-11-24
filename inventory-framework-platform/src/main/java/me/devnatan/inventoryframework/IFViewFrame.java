package me.devnatan.inventoryframework;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import kotlin.NotImplementedError;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

abstract class IFViewFrame<S extends IFViewFrame<S, V>, V extends PlatformView<S, ?, ?, ?, ?, ?, ?>> {

    /**
     * Called when a {@link IFViewFrame} is registered.
     */
    public static final PipelinePhase FRAME_REGISTERED = new PipelinePhase("frame-registered");

    /**
     * Called when a {@link IFViewFrame} is unregistered.
     */
    public static final PipelinePhase FRAME_UNREGISTERED = new PipelinePhase("frame-unregistered");

    private boolean registered;
    protected final Map<UUID, V> registeredViews = new HashMap<>();
    protected final Map<String, Viewer> viewerById = new HashMap<>();
    protected Consumer<ViewConfigBuilder> defaultConfig;

    @SuppressWarnings("rawtypes")
    private final Pipeline<IFViewFrame> pipeline = new Pipeline<>(FRAME_REGISTERED, FRAME_UNREGISTERED);

    protected IFViewFrame() {}

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    @SuppressWarnings("rawtypes")
    public final @NotNull Pipeline<IFViewFrame> getPipeline() {
        return pipeline;
    }

    /**
     * All registered views.
     *
     * @return A Map containing all registered views in this view frame.
     */
    protected final @NotNull @UnmodifiableView Map<UUID, V> getRegisteredViews() {
        return Collections.unmodifiableMap(registeredViews);
    }

    /**
     * Gets an instance of a registered view from its class-type.
     *
     * @param type The view type.
     * @return An RootView instance of the given type.
     * @throws IllegalArgumentException If the given cannot be found.
     */
    public final @NotNull V getRegisteredViewByType(@NotNull Class<?> type) {
        return getRegisteredViews().values().stream()
                .filter(view -> view.getClass().equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("View not registered: %s", type)));
    }

    /**
     * Registers a new view to this view frame.
     *
     * @param views The views that'll be registered.
     * @return This platform view frame.
     */
    @SuppressWarnings("unchecked")
    @Contract("_ -> this")
    public final S with(V... views) {
        synchronized (registeredViews) {
            for (final V view : views) {
                if (registeredViews.containsKey(view.getUniqueId()))
                    throw new IllegalStateException(String.format(
                            "View %s already registered. Maybe your are using #register() before #with(...).",
                            view.getClass().getName()));

                IFDebug.debug(
                        "Registered view \"%s\" identified as %s",
                        view.getUniqueId(), view.getClass().getSimpleName());
                registeredViews.put(view.getUniqueId(), view);
            }
        }
        return (S) this;
    }

    /**
     * Removes a view from this view frame.
     *
     * @param views The views that'll be removed.
     */
    public final void remove(V... views) {
        synchronized (registeredViews) {
            for (final V view : views) {
                view.closeForEveryone();
                registeredViews.remove(view.getUniqueId());
                IFDebug.debug("Removed view %s", view.getUniqueId());
            }
        }
    }

    /**
     * Registers this view frame.
     *
     * @return This platform view frame.
     * @throws IllegalStateException If this view frame is already registered.
     */
    public abstract S register();

    /**
     * Unregisters this view frame and closes all registered views.
     */
    public abstract void unregister();

    /**
     * If this view frame is registered.
     *
     * @return {@code true} if it's registered or {@code false} otherwise.
     */
    public final boolean isRegistered() {
        return registered;
    }

    /**
     * Sets the registered state of this view frame.
     *
     * @param registered {@code true} to mark this view frame as registered.
     */
    protected final void setRegistered(boolean registered) {
        this.registered = registered;
    }

    /**
     * Opens a view to more than one player with initial data.
     * <p>
     * These players will see the same inventory and share the same context.
     *
     * @param viewClass   The target view to be opened.
     * @param players     The players that the view will be open to.
     * @param initialData The initial data.
     */
    protected final void internalOpen(
            @NotNull Class<? extends V> viewClass, @NotNull Collection<?> players, Object initialData) {
        final V view = getRegisteredViewByType(viewClass);
        final List<Viewer> viewers = players.stream()
                .map(player -> view.getElementFactory().createViewer(player, null))
                .collect(Collectors.toList());

        viewers.forEach(Viewer::close);
        view.open(viewers, initialData);
    }

	/**
	 * Attaches a viewer to an already active context.
	 *
	 * @param contextId Context id to attach into.
	 * @param viewer The viewer to attach into the specified context.
	 */
	@ApiStatus.Experimental
	protected final void internalEnterInContext(UUID contextId, Viewer viewer) {
		throw new NotImplementedError();
	}

	/**
	 * Creates a shared context for later use.
	 * <p>
	 * TODO Needs a very descriptive documentation about how this really works
	 *      also explain that the context created by this method never invalidates.
	 *
	 * @param viewClass The target view to be later opened.
	 * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/Shared-Contexts">Shared Contexts on Wiki</a>
	 */
	@ApiStatus.Experimental
	public final UUID createIndefiniteContext(@NotNull Class<? extends V> viewClass) {
		throw new NotImplementedError();
	}

    void addViewer(@NotNull Viewer viewer) {
        synchronized (viewerById) {
            viewerById.put(viewer.getId(), viewer);
        }
    }

    void removeViewer(@NotNull Viewer viewer) {
        synchronized (viewerById) {
            viewerById.remove(viewer.getId());
        }
    }

    /**
     * Sets the default configuration that will be used for all views registered from this framework.
     * <p>
     * <b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @return This framework instance.
     * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/basic-usage#default-configuration">Default Configuration on Wiki</a>
     */
    @SuppressWarnings("unchecked")
    @ApiStatus.Experimental
    public final S defaultConfig(@NotNull Consumer<ViewConfigBuilder> defaultConfig) {
        this.defaultConfig = defaultConfig;
        return (S) this;
    }

    /**
     * The default configuration applier of this framework.
     *
     * @return The default configuration applier of this framework.
     */
    final Consumer<ViewConfigBuilder> getDefaultConfig() {
        return defaultConfig;
    }

    /**
     * Enables internal code debugs.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @return Enable InventoryFramework debugs.
     */
    @SuppressWarnings("unchecked")
    @ApiStatus.Internal
    public final S enableDebug() {
        IFDebug.setEnabled(true);
        return (S) this;
    }
}
