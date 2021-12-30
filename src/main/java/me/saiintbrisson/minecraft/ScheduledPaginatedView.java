package me.saiintbrisson.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public abstract class ScheduledPaginatedView<T> extends PaginatedView<T> implements Runnable {

	private final Plugin plugin;

	public ScheduledPaginatedView(Plugin plugin) {
		this.plugin = plugin;
	}

	private BukkitTask task;

	@Override
	public void onOpen(OpenViewContext context) {
		if (getContexts().size() == 1) {
			task = Bukkit.getScheduler().runTaskTimer(plugin, this, 1L, 1L);
		}
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

}