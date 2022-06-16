package me.saiintbrisson.minecraft;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The click context represents an interaction between the actor and the container,
 * even if an item is not present, it stores information about the click, and it's origin.
 *
 * @see ViewSlotContext
 */
public interface ViewSlotClickContext extends ViewSlotContext {

	/**
	 * If the click was using the left mouse button.
	 *
	 * @return <tt>true</tt> if the click was using the left mouse button or <tt>false</tt> otherwise.
	 */
	boolean isLeftClick();

	/**
	 * If the click was using the right mouse button.
	 *
	 * @return <tt>true</tt> if the click was using the right mouse button or <tt>false</tt> otherwise.
	 */
	boolean isRightClick();

	/**
	 * If the click was using the middle mouse button, commonly known as the scroll button.
	 *
	 * @return <tt>true</tt> if the click was using the middle mouse button or <tt>false</tt> otherwise.
	 */
	boolean isMiddleClick();

	/**
	 * Whether the click was accompanied by a click holding the keyboard shift button.
	 *
	 * @return <tt>true</tt> if it was a click holding the keyboard shift button or <tt>false</tt> otherwise.
	 */
	boolean isShiftClick();

	/**
	 * If the click source came from a keyboard, e.g. the player's toolbar number.
	 *
	 * @return <tt>true</tt> if the click source came from a keyboard or <tt>false</tt> otherwise.
	 */
	boolean isKeyboardClick();

	/**
	 * If the click did not occur within any containers.
	 *
	 * @return <tt>true</tt> if the click did not occur within any containers or <tt>false</tt> otherwise.
	 */
	boolean isOutsideClick();

	/**
	 * The click identifier, available only in cases where the library does not cover
	 * all types of clicks, so you can discover the type of click through its identifier.
	 *
	 * @return The click type identifier.
	 */
	@NotNull
	String getClickIdentifier();

	/**
	 * Returns the event from which this context originated.
	 *
	 * @throws IllegalStateException If this context did not originate from a click.
	 * @deprecated Platform-specific APIs will be removed from framework base module soon.
	 * Cast this context to a BukkitClickViewSlotContext implementation instead.
	 * @return The event from which this context originated.
	 */
	@NotNull
	@Deprecated
	InventoryClickEvent getClickOrigin();

}