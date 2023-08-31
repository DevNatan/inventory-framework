package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.context.IFRenderContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface Viewer {

    /**
     * Unique identifier for this viewer used for indexing.
     *
     * @return A unique identifier for this viewer.
     */
    @NotNull
    String getId();

    /**
     * Opens a container to this viewer.
     *
     * @param container The container that'll be opened.
     */
    void open(@NotNull ViewContainer container);

    /**
     * Closes the current container that this viewer is currently viewing.
     */
    void close();

    /**
     * An implementation of ViewContainer for the container of this viewer.
     *
     * @return The container of this viewer.
     */
    @NotNull
    ViewContainer getSelfContainer();

    @NotNull
    IFRenderContext getContext();

    void setContext(IFRenderContext context);

    @Contract("_ -> this")
    Viewer withContext(IFRenderContext context);
}
