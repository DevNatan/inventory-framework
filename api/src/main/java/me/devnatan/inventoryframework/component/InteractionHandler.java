package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.context.IFSlotClickContext;
import org.jetbrains.annotations.NotNull;

public interface InteractionHandler {

	/**
	 * Called when a component is clicked.
	 *
	 * @param component The clicked component.
	 * @param context   The click context.
	 */
	void clicked(@NotNull Component component, @NotNull IFSlotClickContext context);

}
