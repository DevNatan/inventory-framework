package me.devnatan.inventoryframework;

import org.jetbrains.annotations.NotNull;

public interface IFViewFrame {

    /**
     * Registers a new view to this view frame.
     *
     * @param views The views that'll be registered.
     * @return This platform view frame.
     */
    IFViewFrame with(@NotNull RootView... views);

    /**
     * Removes a view from this view frame.
     *
     * @param views The views that'll be removed.
     */
    void remove(@NotNull RootView... views);

    /**
     * Registers this view frame.
     *
     * @return This platform view frame.
     * @throws IllegalStateException If this view frame is already registered.
     */
    IFViewFrame register();

    /**
     * Unregisters this view frame and closes all registered views.
     *
     * @throws IllegalStateException If this view frame is not registered.
     */
    void unregister();
}
