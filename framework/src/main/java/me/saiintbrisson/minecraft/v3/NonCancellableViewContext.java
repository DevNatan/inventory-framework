package me.saiintbrisson.minecraft.v3;

interface NonCancellableViewContext extends CancellableViewContext {

	@Override
	default boolean isCancelled() {
		return false;
	}

	@Override
	default void setCancelled(boolean cancelled) {
		throw new IllegalStateException(getClass().getName() + " is not cancellable");
	}

}
