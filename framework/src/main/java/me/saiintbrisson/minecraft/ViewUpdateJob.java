package me.saiintbrisson.minecraft;

/**
 * Object used to store information about the state of a View's update interval.
 */
interface ViewUpdateJob {

	long getDelay();

	long getInterval();

	/**
	 * Starts the view update task.
	 *
	 * @throws IllegalStateException If this job is interrupted or the task has already started.
	 */
	void start();

	/**
	 * Cancels the current update task.
	 */
	void cancel();

}