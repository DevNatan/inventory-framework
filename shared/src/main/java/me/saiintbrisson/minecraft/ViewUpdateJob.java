package me.saiintbrisson.minecraft;

/** Object used to store information about the state of a View's update interval. */
public interface ViewUpdateJob {

    long getDelay();

    long getInterval();

    boolean isStarted();

    /**
     * Starts this update job.
     *
     * @throws IllegalStateException If this job is interrupted or the task has already started.
     */
    void start();

    /**
     * Resumes this update job.
     *
     * @throws IllegalStateException If this job was started or uninterrupted.
     */
    void resume();

    /**
     * Pauses this update job.
     *
     * @throws IllegalStateException If this job wasn't started or it's already psued.
     */
    void pause();

    /** Cancels this update job. */
    void cancel();
}
