package me.devnatan.inventoryframework.context;

import java.util.concurrent.CompletableFuture;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * This context is created before the container is opened, it is used for cancellation by previously
 * defined data also can be used to change the title and size of the container before the rendering intent.
 */
public interface IFOpenContext extends IFConfinedContext {

    CompletableFuture<Void> getAsyncOpenJob();

    /**
     * Waits until the specified task to be completed to show the container to the player.
     *
     * @param task The task that will be waited for.
     */
    void waitUntil(@NotNull CompletableFuture<Void> task);

    /**
     * If the event was cancelled.
     *
     * @return If <code>true</code> the container will not be displayed to the player.
     */
    boolean isCancelled();

    /**
     * Cancel opening the container for the player.
     *
     * @param cancelled If <code>true</code>, the container will not open for the player.
     */
    void setCancelled(boolean cancelled);

    /**
     * Allows access and change the current configuration specifically to that context.
     * <p>
     * By default, all contexts inherit their root configuration but context configuration always
     * takes precedence over root.
     *
     * @return The current context configuration.
     */
    @NotNull
    ViewConfigBuilder modifyConfig();
}
