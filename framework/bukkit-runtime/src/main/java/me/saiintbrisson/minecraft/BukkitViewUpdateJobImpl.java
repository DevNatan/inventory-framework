package me.saiintbrisson.minecraft;

import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * Object used to store information about the state of a View's update interval.
 */
final class BukkitViewUpdateJobImpl implements ViewUpdateJob, Runnable {

	private final Plugin plugin;
	private final VirtualView view;

	@Getter
	private final long delay;
	@Getter
	private final long interval;

	private BukkitTask task;
	private boolean interrupted;

	public BukkitViewUpdateJobImpl(
		@NotNull Plugin plugin,
		@NotNull VirtualView view,
		long delay,
		long interval
	) {
		this.plugin = plugin;
		this.view = view;
		this.delay = delay;
		this.interval = interval;
	}

	@Override
	public void run() {
		if (interrupted)
			return;

		view.update();
	}

	@Override
	public void start() {
		if (interrupted)
			throw new IllegalStateException("Cannot start a interrupted view update job");

		if (task != null)
			throw new IllegalStateException("View update job already started");

		task = plugin.getServer().getScheduler().runTaskTimer(plugin, this, delay, interval);
	}

	@Override
	public void cancel() {
		if (task == null)
			return;

		task.cancel();
		task = null;
		interrupted = false;
	}

	void interrupt() {
		this.interrupted = !interrupted;
	}

}