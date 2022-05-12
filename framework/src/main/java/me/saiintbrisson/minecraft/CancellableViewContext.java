package me.saiintbrisson.minecraft;

interface CancellableViewContext extends ViewContext {

	boolean isCancelled();

	void setCancelled(boolean cancelled);

}
