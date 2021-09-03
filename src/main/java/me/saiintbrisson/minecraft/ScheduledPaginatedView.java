package me.saiintbrisson.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public abstract class ScheduledPaginatedView<T> extends PaginatedView<T> {

	private long delay = 1L;
	private long period = 1L;

	private BukkitTask task;

	public final long getDelay() {
		return delay;
	}

	protected void setDelay(long delay) {
		this.delay = delay;
	}

	public final long getPeriod() {
		return period;
	}

	protected void setPeriod(long period) {
		this.period = period;
	}

	@Override
	protected void onOpen(OpenViewContext ctx) {
		if (getContexts().isEmpty()) {
			task = Bukkit.getScheduler().runTaskTimer(ctx.getView().getFrame().getOwner(), this::update, delay, period);
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