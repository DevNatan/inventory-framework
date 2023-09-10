package me.devnatan.inventoryframework.context;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.state.StateValueHost;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public interface IFContext extends VirtualView, StateValueHost {

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
     * All components in this context.
     *
     * @return An unmodifiable List view of all components in this context.
     */
    @NotNull
    @UnmodifiableView
    List<Component> getComponents();

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
    Component getComponent(int position);

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
     */
    @ApiStatus.Internal
    void updateComponent(@NotNull Component component);

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
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    boolean isShared();
}
