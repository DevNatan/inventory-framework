package me.devnatan.inventoryframework.internal;

import org.jetbrains.annotations.ApiStatus;

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

    /**
     * Internal loop.
     *
     * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    void loop();
}
