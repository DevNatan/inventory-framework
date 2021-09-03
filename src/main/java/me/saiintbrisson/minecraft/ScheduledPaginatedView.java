package me.saiintbrisson.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 * Use {@link VirtualView#setUpdateInterval(long)} instead.
 */
@Deprecated
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
	public void setUpdateInterval(long updateInterval) {
		throw new IllegalArgumentException("#setUpdateInterval(long) cannot be used with ScheduledPaginatedView");
	}

	@Override
	protected void resumeUpdateJob() {
		throw new IllegalArgumentException("#resumeUpdateJob(long) cannot be used with ScheduledPaginatedView");
	}

	@Override
	protected void cancelUpdateJob() {
		throw new IllegalArgumentException("#cancelUpdateJob(long) cannot be used with ScheduledPaginatedView");
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