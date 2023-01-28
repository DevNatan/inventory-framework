package me.devnatan.inventoryframework.context;

import java.util.concurrent.CompletableFuture;
import me.devnatan.inventoryframework.ViewType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This context is created before the container is opened, it is used for cancellation by previously
 * defined data also can be used to change the title and size of the container before the rendering intent.
 */
public interface IFOpenContext extends IFContext {

    @NotNull String getTitle();

    int getSize();

    ViewType getType();

    CompletableFuture<Void> getAsyncOpenJob();

    /**
     * Sets the title of the container.
     *
     * @param title The title of the container that will be created.
     */
    void setTitle(@Nullable String title);

    /**
     * Sets the size of the container.
     * <p>
     * Can be the total number of slots like <code>36</code> or the number of horizontal
     * lines in the container.
     *
     * @param size The size of the container that will be created.
     */
    void setSize(int size);

    /**
     * Sets the type of the container.
     * <p>
     * @param type The type of the container that will be created.
     */
    void setType(@Nullable ViewType type);

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
}
