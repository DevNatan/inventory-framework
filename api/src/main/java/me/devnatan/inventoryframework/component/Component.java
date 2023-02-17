package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.VirtualView;
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
}
