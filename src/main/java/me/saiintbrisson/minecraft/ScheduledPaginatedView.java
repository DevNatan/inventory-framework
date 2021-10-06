package me.saiintbrisson.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public abstract class ScheduledPaginatedView<T> extends PaginatedView<T> {

	private BukkitTask task;

	@Override
	protected void onOpen(OpenViewContext ctx) {
		if (getContexts().isEmpty()) {
			task = Bukkit.getScheduler().runTaskTimer(ctx.getView().getFrame().getOwner(), this::update, 1L, 1L);
		}
	}


	@Override
	protected void onClose(ViewContext context) {
		if (getContexts().size() == 1 && task != null) {
			task.cancel();
			task = null;
		}
	}
}
