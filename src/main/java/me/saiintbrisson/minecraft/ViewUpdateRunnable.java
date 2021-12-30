package me.saiintbrisson.minecraft;

import org.bukkit.scheduler.BukkitRunnable;

public class ViewUpdateRunnable extends BukkitRunnable {

	private final ViewFrame viewFrame;

	public ViewUpdateRunnable(ViewFrame viewFrame) {
		this.viewFrame = viewFrame;
	}

	@Override
	public void run() {
		if (viewFrame.getRegisteredViews().isEmpty()) return;

		for (View view : viewFrame.getRegisteredViews().values()) {
			if (view.getContexts().isEmpty() || !view.isAutoUpdate()) continue;

			view.getContexts().forEach((player, context) -> view.update(context));
		}
	}

}
