package me.devnatan.inventoryframework.internal;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.Plugin;

class FoliaTaskJobImpl implements Job {

	private final Plugin plugin;
	private final HumanEntity entity;
	private final long intervalInTicks;
	private final Runnable execution;

	private ScheduledTask task;

	public FoliaTaskJobImpl(Plugin plugin, HumanEntity entity, long intervalInTicks, Runnable execution) {
		this.plugin = plugin;
		this.entity = entity;
		this.intervalInTicks = intervalInTicks;
		this.execution = execution;
	}

	@Override
	public boolean isStarted() {
		return task != null && !task.isCancelled();
	}

	@Override
	public void start() {
		if (entity != null) {
			if (plugin.getServer().isOwnedByCurrentRegion(entity)) {
				entity.getScheduler().runAtFixedRate(plugin, $ -> loop(), null, intervalInTicks, intervalInTicks);
			} else {

			}
		} else
			plugin.getServer().getGlobalRegionScheduler()
				.runAtFixedRate(plugin, $ -> loop(), intervalInTicks, intervalInTicks);
	}

	@Override
	public void cancel() {
		if (isStarted()) return;
		task.cancel();
		task = null;
	}

	@Override
	public void loop() {
		execution.run();
	}
}
