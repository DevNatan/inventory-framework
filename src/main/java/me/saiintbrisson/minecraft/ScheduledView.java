package me.saiintbrisson.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public abstract class ScheduledView extends View implements Runnable {

	private BukkitTask task;

	@Override
	public void onOpen(OpenViewContext context) {
		if (getContexts().size() != 1) return;

		task = Bukkit.getScheduler().runTaskTimer(context.getView().getFrame().getOwner(), this, 1L, 1L);
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