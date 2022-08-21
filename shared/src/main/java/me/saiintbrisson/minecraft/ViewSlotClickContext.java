package me.saiintbrisson.minecraft;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * The click context represents an interaction between the actor and the container, even if an item
 * is not present, it stores information about the click, and it's origin.
 *
 * @see ViewSlotContext
 */
public interface ViewSlotClickContext extends ViewSlotContext {

    /**
     * If the click was using the left mouse button.
     *
     * @return If the click was using the left mouse button.
     */
    boolean isLeftClick();

    /**
     * If the click was using the right mouse button.
     *
     * @return If the click was using the right mouse button.
     */
    boolean isRightClick();

    /**
     * If the click was using the middle mouse button, commonly known as the scroll button.
     *
     * @return If the click was using the middle mouse button.
     */
    boolean isMiddleClick();

    /**
     * If the click was accompanied by a click holding the keyboard shift button.
     *
     * @return If it was a click holding the keyboard shift button.
     */
    boolean isShiftClick();

    /**
     * If the click source came from a keyboard, e.g. the player's toolbar number.
     *
     * @return If the click source came from a keyboard.
     */
    boolean isKeyboardClick();

    /**
     * If the click did not occur within any containers.
     *
     * @return If the click did not occur within any containers.
     */
    boolean isOutsideClick();

    /**
     * The click identifier, available only in cases where the library does not cover all types of
     * clicks, so you can discover the type of click through its identifier.
     *
     * @return The click type identifier.
     */
    @NotNull
    String getClickIdentifier();

    /**
     * Returns the event from which this context originated.
     *
     * @throws IllegalStateException If this context did not originate from a click.
     * @deprecated Platform-specific APIs will be removed from framework shared module soon. Cast this
     *     context to a BukkitClickViewSlotContext implementation instead.
     * @return The event from which this context originated.
     */
    @NotNull
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
    InventoryClickEvent getClickOrigin();
}
