package me.saiintbrisson.minecraft;

import org.bukkit.plugin.Plugin;

public class DelegatedViewProvider implements ViewProvider {

	private final ViewFrame frame;

	public DelegatedViewProvider(ViewFrame viewFrame) {
		this.frame = viewFrame;
	}

	@Override
	public Plugin getHolder() {
		return frame.getOwner();
	}

	@Override
	public ViewFrame getFrame() {
		return frame;
	}
}
