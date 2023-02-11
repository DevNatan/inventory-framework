package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.feature.FeatureInstaller;
import org.jetbrains.annotations.Contract;

public interface IFViewFrame<S extends IFViewFrame<S>> extends FeatureInstaller<S> {

    /**
     * Registers a new view to this view frame.
     *
     * @param views The views that'll be registered.
     * @return This platform view frame.
     */
    @Contract("_ -> this")
    S with(RootView... views);

    /**
     * Removes a view from this view frame.
     *
     * @param views The views that'll be removed.
     */
    void remove(RootView... views);

    /**
     * Registers this view frame.
     *
     * @return This platform view frame.
     * @throws IllegalStateException If this view frame is already registered.
     */
    S register();

    /**
     * Unregisters this view frame and closes all registered views.
     */
    void unregister();

    /**
     * If this view frame is registered.
     *
     * @return {@code true} if it's registered or {@code false} otherwise.
     */
    boolean isRegistered();
}
