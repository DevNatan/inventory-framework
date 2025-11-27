package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.component.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Represents a context in which there is a specific slot related to it, the main context
 * encompasses the entire container in an actor's view, the ViewSlotContext encapsulates a context
 * for just one slot of a container.
 *
 * <p>Methods specific to a ViewSlotContext will only apply to that slot.
 *
 * @see IFContext
 * @see IFSlotClickContext
 */
public interface IFSlotContext extends IFContext {

    /**
     * The parent context of this context.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @return The parent context of this context.
     */
    @ApiStatus.Internal
    IFRenderContext getParent();

    /**
     * Returns the slot position of this context in the current container.
     *
     * @return The slot position of this context.
     */
    int getSlot();

    // TODO needs documentation about dynamic slot positioning (some cases are unsupported)
    void setSlot(int slot);

    /**
     * Whether this context originated from an interaction coming from the actor's container and not
     * from the view's container.
     *
     * @return If this context originated from the actor's container
     */
    boolean isOnEntityContainer();

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
	 * The component associated with this slot context.
	 *
	 * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided.</i></b>
	 *
	 * @return The component associated with this slot context.
	 */
    @ApiStatus.Internal
    @UnknownNullability
    Component getComponent();
}
