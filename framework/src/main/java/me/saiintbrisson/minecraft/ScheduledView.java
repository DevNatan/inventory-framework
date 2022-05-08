package me.saiintbrisson.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * @deprecated Use {@link VirtualView#scheduleUpdate(long, long)} instead.
 */
@Deprecated
public abstract class ScheduledView extends View implements Runnable {

	private final Plugin plugin = getFrame().getOwner();

	BukkitTask _task;
	final BukkitTask task = _task;
	private Long delay, period;

	@Override
	public void onOpen(OpenViewContext context) {
		if (getContexts().size() != 1) return;

		if (delay == null) delay = 1L;
		if (period == null) period = 1L;

		this._task = Bukkit.getScheduler().runTaskTimer(plugin, this, delay, period);
	}

	@Override
	public void onClose(ViewContext context) {
		if (getContexts().size() != 1) return;

		task.cancel();
		_task = null;
	}

	@Override
	public void run() {
		update();
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

}