package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * A component represents one or {@link ComponentComposition more} items within a {@link VirtualView}.
 */
public interface Component {

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
     * The interaction handler for this component.
     *
     * @return The interaction handler for this component.
     */
    @NotNull
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
     * If this component is marked for removal.
     *
     * @return {@code true} if this component will be removed in the next update.
     */
    boolean isMarkedForRemoval();

}
