package me.saiintbrisson.minecraft;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * Object used to store information about the state of a View's update interval.
 */
final class ViewUpdateJob implements Runnable {

	private final VirtualView view;
	final long delay;
	final long interval;

	private BukkitTask task;
	private boolean interrupted;

	public ViewUpdateJob(@NotNull VirtualView view, long delay, long interval) {
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

	/**
	 * Starts the view update task.
	 * @param plugin The plugin used to launch the task.
	 * @throws IllegalStateException If this job is interrupted or the task has already started.
	 */
	void start(@NotNull Plugin plugin) {
		if (interrupted)
			throw new IllegalStateException("Cannot start a interrupted view update job");

		if (task != null)
			throw new IllegalStateException("View update job already started");

		task = plugin.getServer().getScheduler().runTaskTimer(plugin, this, delay, interval);
	}

	/**
	 * Cancels the current task (also marks it as <code>null</code>) and sets the value of {@link #interrupted} to
	 * <code>false</code>.
	 */
	void cancel() {
		if (task == null)
			return;

		task.cancel();
		task = null;
		interrupted = false;
	}

	/**
	 * Marks the current task as interrupted or not.
	 * This will cause the update cycle to be interrupted or not.
	 */
	void interrupt() {
		this.interrupted = !interrupted;
	}

}
