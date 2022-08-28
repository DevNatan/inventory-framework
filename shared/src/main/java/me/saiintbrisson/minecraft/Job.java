package me.saiintbrisson.minecraft;

public interface Job {

    /**
     * Whether the job has already started or not.
     *
     * @return <code>true</code> if job was started or <code>false</code> otherwise.
     */
    boolean isStarted();

    /**
     * Starts this job.
     */
    void start();

    /**
     * Cancels this job if started.
     */
    void cancel();

}
