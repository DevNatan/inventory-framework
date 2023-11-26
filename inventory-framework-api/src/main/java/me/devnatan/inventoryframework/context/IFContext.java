package me.devnatan.inventoryframework.context;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentContainer;
import me.devnatan.inventoryframework.state.StateValueHost;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public interface IFContext extends VirtualView, StateValueHost, ComponentContainer {

    /**
     * An unique id for this context.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @return The unique identifier for this context.
     */
    @ApiStatus.Internal
    @NotNull
    UUID getId();

    /**
     * The configuration for this context.
     * <p>
     * By default, contexts inherit their root configuration.
     *
     * @return The configuration for this context.
     */
    @NotNull
    ViewConfig getConfig();

    /**
     * An unmodifiable copy of all viewers that are tied to this context.
     *
     * @return All view of all viewers.
     */
    @NotNull
    @UnmodifiableView
    List<Viewer> getViewers();

    /**
     * A Map containing all viewers in that context.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @return A Map containing all viewers in that context.
     */
    @NotNull
    @ApiStatus.Internal
    Map<String, Viewer> getIndexedViewers();

    /**
     * Adds a new viewer to this context.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @param viewer The viewer that'll be added.
     */
    @ApiStatus.Internal
    void addViewer(@NotNull Viewer viewer);

    /**
     * Removes a new viewer to this context.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @param viewer The viewer that'll be removed.
     */
    @ApiStatus.Internal
    void removeViewer(@NotNull Viewer viewer);

    /**
     * View root from which this context originated.
     *
     * @return The root of this context.
     */
    @NotNull
    RootView getRoot();

    /**
     * The initial title of this context, that is, even if it has been changed, it will return the
     * title that has been initially defined.
     *
     * @return The initial title of this context, the current title of this view.
     */
    @NotNull
    String getInitialTitle();

    /**
     * Gets the component that is at a certain position.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @param position The position.
     * @return The component in the given position or {@code null}.
     */
    @ApiStatus.Internal
    List<Component> getComponentsAt(int position);

    /**
     * Adds a new component to this context.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @param component The component to be added.
     */
    @ApiStatus.Internal
    void addComponent(@NotNull Component component);

    /**
     * Removes a component from this context.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @param component The component to be removed.
     */
    @ApiStatus.Internal
    void removeComponent(@NotNull Component component);

    /**
     * Renders a component in this context.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @param component The component to be rendered.
     */
    @ApiStatus.Internal
    void renderComponent(@NotNull Component component);

    /**
     * Updates a component in this context.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @param component The component to be updated.
     * @param force If update should be forced.
     */
    @ApiStatus.Internal
    void updateComponent(@NotNull Component component, boolean force);

    /**
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @param component The clicked component.
     */
    @ApiStatus.Internal
    void performClickInComponent(
            @NotNull Component component,
            @NotNull Viewer viewer,
            @NotNull ViewContainer clickedContainer,
            Object platformEvent,
            int clickedSlot);

    /**
     * Updates all components for all viewers in this context.
     */
    void update();

    /**
     * Data defined when a context is created, usually this is data set when the context is
     * opened for a viewer.
     *
     * @return The initial context data.
     */
    Object getInitialData();

    /**
     * <b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Internal
    void setInitialData(Object initialData);

    /**
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    boolean isShared();

    /**
     * Updates the container title for everyone that's viewing it.
     *
     * <p>This should not be used before the container is opened, if you need to set the __initial
     * title__ use {@link IFOpenContext#modifyConfig()} on open handler instead.
     *
     * <p>This method is version dependant, so it may be that your server version is not yet
     * supported, if you try to use this method and fail (can fail silently), report it to the
     * library developers to add support to your version.
     *
     * @param title The new container title.
     */
    void updateTitleForEveryone(@NotNull String title);

    /**
     * Updates the container title to all viewers in this context, to the initially defined title.
     * Must be used after {@link #updateTitleForEveryone(String)} to take effect.
     */
    void resetTitleForEveryone();

    /**
     * Closes this context's container to all viewers who are viewing it.
     */
    void closeForEveryone();

    /**
     * Opens a new view for all viewers in that context.
     * <p>
     * This context will be immediately invalidated if there are no viewers left after opening.
     *
     * @param other The view to be opened.
     */
    void openForEveryone(@NotNull Class<? extends RootView> other);

    /**
     * Opens a new view for all viewers in that context with an initially defined data.
     * <p>
     * This context will be immediately invalidated if there are no viewers left after opening.
     *
     * @param other       The view to be opened.
     * @param initialData The initial data.
     */
    void openForEveryone(@NotNull Class<? extends RootView> other, Object initialData);

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    boolean isActive();

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    void setActive(boolean active);

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    boolean isEndless();

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    void setEndless(boolean endless);
}
