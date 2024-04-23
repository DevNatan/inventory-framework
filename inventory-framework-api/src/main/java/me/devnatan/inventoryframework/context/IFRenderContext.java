package me.devnatan.inventoryframework.context;

import java.util.List;
import java.util.function.BiFunction;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.UpdateReason;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentBuilder;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface IFRenderContext extends IFConfinedContext {

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    List<ComponentBuilder> getNotRenderedComponents();

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    List<LayoutSlot> getLayoutSlots();

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    void addLayoutSlot(@NotNull LayoutSlot layoutSlot);

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    List<BiFunction<Integer, Integer, ComponentBuilder>> getAvailableSlotFactories();

    /**
     * The container of this context.
     * <p>
     * The container is where all the changes that are displayed to the user are applied.
     * <p>
     * Direct modifications to the container must launch an inventory modification error, which
     * signals that that function will change the container for whoever is seeing what, which, if it
     * is not possible at that moment or if the container is not sufficiently prepared for this,
     * it must fail.
     *
     * @return The container of this context.
     */
    @NotNull
    ViewContainer getContainer();

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    boolean isRendered();

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    void markRendered();

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    void simulateClick(int rawSlot, Viewer whoClicked, Object platformEvent, boolean isCombined);

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
     * @param reason Reason why the component was updated.
     */
    @ApiStatus.Internal
    void updateComponent(Component component, boolean force, UpdateReason reason);

    /**
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @param component The component to be cleared.
     */
    @ApiStatus.Internal
    void clearComponent(@NotNull Component component);

    /**
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @param component The clicked component.
     */
    @ApiStatus.Internal
    void clickComponent(
            Component component,
            Viewer viewer,
            ViewContainer clickedContainer,
            Object platformEvent,
            int clickedSlot,
            boolean combined);

    /** {@inheritDoc } */
    @Override
    @NotNull
    RootView getRoot();
}
