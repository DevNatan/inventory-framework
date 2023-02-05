package me.devnatan.inventoryframework.context;

public interface IFCloseContext extends IFContext {

	/**
	 * Cancellation state of that context.
	 *
	 * @return If that context was cancelled.
	 */
	boolean isCancelled();

	/**
	 * Cancels or not this context.
	 * <p>
	 * The side effect of canceling a close context is determined by the current platform.
	 *
	 * @param cancelled If this context should be cancelled.
	 * @implNote NOOP (do nothing) if close context cancellation is not supported.
	 */
	void setCancelled(boolean cancelled);

}
