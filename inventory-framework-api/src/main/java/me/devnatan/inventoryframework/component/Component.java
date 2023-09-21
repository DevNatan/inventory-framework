package me.devnatan.inventoryframework.component;

import java.util.Set;
import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

/**
 * A component represents one or {@link ComponentComposition more} items within a {@link VirtualView}.
 */
public interface Component extends VirtualView {

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

    /**
     * The current position of this component relative to its root view.
     *
     * @return The current position of this component.
     */
    int getPosition();

    /**
     * Checks if this component is in a specific position.
     *
     * @param position The position.
     * @return If this component is contained in the given position.
     */
    boolean isContainedWithin(int position);

    /**
     * If this component are intersects with other component.
     *
     * @param other The other component.
     * @return If both this and other component intersects in area.
     */
    boolean intersects(@NotNull Component other);

    /**
     * The interaction handler for this component.
     *
     * @return The interaction handler for this component.
     */
    InteractionHandler getInteractionHandler();

    /**
     * Renders this component to the given context.
     *
     * @param context The context that this component will be rendered on.
     */
    void render(@NotNull IFSlotRenderContext context);

    /**
     * Called when this component is updated in the given context.
     *
     * @param context The update context.
     */
    void updated(@NotNull IFSlotRenderContext context);

    /**
     * Clears this component from the given context.
     *
     * @param context The context that this component will be cleared from.
     */
    void clear(@NotNull IFContext context);

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
    boolean isManagedExternally();

    // TODO Needs documentation
    boolean shouldRender(IFContext context);

    /**
     * Updates this component.
     */
    void update();

    /**
     * Checks if two components area intersects with each other.
     *
     * @param component The component A.
     * @param other     The component B.
     * @return If component B area conflicts with area of component A.
     */
    static boolean intersects(@NotNull Component component, @NotNull Component other) {
        if (component instanceof ComponentComposition) return intersects(other, component);

        if (other instanceof ComponentComposition) {
            for (final Component otherChildren : (ComponentComposition) other) {
                if (otherChildren.intersects(component)) return true;
            }
        }

        return other.isContainedWithin(component.getPosition());
    }

    /**
     * Returns the reference assigned to this component.
     * <p>
     * <b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    Ref<Component> getReference();

    /**
     * Forces this component to be updated.
     *
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    void forceUpdate();
}
