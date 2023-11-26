package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.context.IFRenderContext;
import org.jetbrains.annotations.ApiStatus;
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

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    @NotNull
    IFRenderContext getActiveContext();

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    void setActiveContext(@NotNull IFRenderContext context);

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    long getLastInteractionInMillis();

    /**
     * Updates the value that holds the timestamp of the last interaction of this viewer.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @param lastInteractionInMillis Timestamp of the last interaction.
     */
    @ApiStatus.Internal
    void setLastInteractionInMillis(long lastInteractionInMillis);

    /**
     * If this viewer is waiting for view's {@link ViewConfig#getInteractionDelayInMillis() interaction delay} to be able to interact again.
     * <p>
     * <b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @return If this viewer cannot interact now.
     */
    @ApiStatus.Experimental
    boolean isBlockedByInteractionDelay();

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    boolean isTransitioning();

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    void setTransitioning(boolean transitioning);

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    IFRenderContext getPreviousContext();

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    void setPreviousContext(IFRenderContext context);

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    void unsetPreviousContext();

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    Object getPlatformInstance();
}
