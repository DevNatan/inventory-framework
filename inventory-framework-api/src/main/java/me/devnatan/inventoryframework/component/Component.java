package me.devnatan.inventoryframework.component;

import java.util.Set;
import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFComponentClearContext;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.pipeline.Pipelined;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

/**
 * A component represents one or {@link ComponentComposition more} items within a {@link VirtualView}.
 */
public interface Component extends VirtualView, Pipelined {

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    String getKey();

    /**
     * The root of this component.
     *
     * @return The root of this component.
     */
    @NotNull
    VirtualView getRoot();

    IFContext getContext();

    ViewContainer getContainer();

    /**
     * An unmodifiable set of all states that this component is watching.
     *
     * @return All states that this component is watching.
     */
    @UnmodifiableView
    Set<State<?>> getWatchingStates();

    /**
     * If this component can be seen, it is used in interaction treatments to ensure that the viewer
     * does not interact with hidden components.
     *
     * @return If this component is visible.
     */
    boolean isVisible();

    /**
     * Sets the visibility state of this component.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @param visible If this component is visible.
     */
    @ApiStatus.Internal
    void setVisible(boolean visible);

    /**
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     */
    @ApiStatus.Internal
    boolean isSelfManaged();

    boolean isContainedWithin(int position);

    boolean intersects(@NotNull Component other);

    boolean shouldRender(IFContext context);

    /**
     * Returns the reference assigned to this component.
     * <p>
     * <b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    Ref<Component> getReference();

    /**
     * Shows this component. Update is needed after this method call.
     * <p>
     * <b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    void show();

    /**
     * Hides this component. Update is needed after this method call.
     * <p>
     * <b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    void hide();
}
