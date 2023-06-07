package me.devnatan.inventoryframework;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class IFViewFrame<S extends IFViewFrame<S>> {

    private boolean registered;
    private final Map<UUID, RootView> registeredViews = new HashMap<>();

    /**
     * All registered views.
     *
     * @return A Map containing all registered views in this view frame.
     */
    protected final @NotNull Map<UUID, RootView> getRegisteredViews() {
        return registeredViews;
    }

    /**
     * Gets an instance of a registered view from its class-type.
     *
     * @param type The view type.
     * @return An RootView instance of the given type.
     * @throws IllegalArgumentException If the given cannot be found.
     */
    protected final @NotNull RootView getRegisteredViewByType(@NotNull Class<?> type) {
        return getRegisteredViews().values().stream()
                .filter(view -> view.getClass().equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown view: %s", type)));
    }

    /**
     * Registers a new view to this view frame.
     *
     * @param views The views that'll be registered.
     * @return This platform view frame.
     */
    @SuppressWarnings("unchecked")
    @Contract("_ -> this")
    public final S with(RootView... views) {
        synchronized (registeredViews) {
            for (final RootView view : views) {
                if (registeredViews.containsKey(view.getUniqueId()))
                    throw new IllegalStateException(String.format(
                            "View %s already registered. Maybe your are using #register() before #with(...).",
                            view.getClass().getName()));

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
    public final void remove(RootView... views) {
        synchronized (registeredViews) {
            for (final RootView view : views) {
                view.closeForEveryone();
                registeredViews.remove(view.getUniqueId());
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
     * Opens a view to a {@link Viewer}.
     *
     * @param viewClass The target view to be open.
     * @param viewer    The viewer that the view will be open to.
     * @throws IllegalStateException If this view is not registered in this view frame.
     */
    public abstract void open(@NotNull Class<? extends RootView> viewClass, @NotNull Viewer viewer);

    /**
     * Opens a view to a {@link Viewer} with an initially defined data.
     *
     * @param viewClass   The target view to be open.
     * @param viewer      The viewer that the view will be open to.
     * @param initialData The initial data.
     * @throws IllegalStateException If this view is not registered in this view frame.
     */
    public abstract void open(
            @NotNull Class<? extends RootView> viewClass,
            @NotNull Viewer viewer,
            @NotNull Map<String, Object> initialData);

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
}
