package me.devnatan.inventoryframework;

import org.jetbrains.annotations.NotNull;

public interface ViewFrame {

    static ViewFrame of() {
        throw new UnsupportedOperationException();
    }

    /**
     * Executes a function on the next iteration.
     * <p>
     * The implementation of this function is per platform and should essentially be used to ensure
     * synchronization guarantee across IF methods.
     *
     * @param runnable The task that'll be run.
     */
    void nextTick(@NotNull Runnable runnable);

    /**
     * Registers this view frame.
     *
     * @throws IllegalStateException If this view frame is already registered.
     */
    void register();
}
