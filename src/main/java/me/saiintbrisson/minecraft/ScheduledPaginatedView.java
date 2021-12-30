package me.saiintbrisson.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public abstract class ScheduledPaginatedView<T> extends PaginatedView<T> implements Runnable {

	private final Plugin plugin = getFrame().getOwner();

	private BukkitTask task;
	private Long delay, period;

	@Override
	public void onOpen(OpenViewContext context) {
		if (getContexts().size() != 1) return;

		if (delay == null) delay = 1L;
		if (period == null) period = 1L;

		task = Bukkit.getScheduler().runTaskTimer(plugin, this, delay, period);
	}

	@Override
	public void onClose(ViewContext context) {
		if (getContexts().size() != 1) return;

		task.cancel();
		task = null;
	}

	@Override
	public final void run() {
		update();
	}

	/*
	Delay & period
	 */

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

}