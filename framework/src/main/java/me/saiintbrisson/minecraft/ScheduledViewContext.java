package me.saiintbrisson.minecraft;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a context that is updated every certain time interval by a View.
 * <p>
 * When this context is created, an attempt is made to initialize an update interval task for the View. And when
 * discarded, if the View no longer has any viewers (besides the context itself, which will be discarded later) the
 * View update task is interrupted.
 *
 * @see VirtualView#scheduleUpdate(long, long)
 */
public final class ScheduledViewContext extends ViewContext {

	public ScheduledViewContext(@NotNull View view, @NotNull Player player, @NotNull Inventory inventory) {
		super(view, player, inventory);

		// checks if other contexts are currently active
		if (view.getContexts().size() > 1)
			return;

		view.updateJob.start(view.getFrame().getOwner());
	}

	@Override
	public void invalidate() {
		super.invalidate();

		// checks for other active contexts (except this one) in the View
		if (view.getContexts().size() <= 1)
			view.updateJob.cancel();
	}

}
