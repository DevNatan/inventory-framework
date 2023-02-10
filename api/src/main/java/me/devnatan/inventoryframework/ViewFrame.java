package me.devnatan.inventoryframework;

import org.jetbrains.annotations.NotNull;

public interface ViewFrame {

    /**
     * Registers a new view to this view frame.
     *
     * @param views The views that'll be registered.
     * @return This platform view frame.
     */
    ViewFrame with(@NotNull RootView... views);

    /**
     * Removes a view from this view frame.
     *
     * @param views The views that'll be removed.
     * @return This platform view frame.
     */
    ViewFrame remove(@NotNull RootView... views);

    /**
     * Registers this view frame.
     *
     * @return This platform view frame.
     * @throws IllegalStateException If this view frame is already registered.
     */
    ViewFrame register();

    /**
     * Unregisters this view frame and closes all registered views.
     *
     * @throws IllegalStateException If this view frame is not registered.
     */
    void unregister();
}
