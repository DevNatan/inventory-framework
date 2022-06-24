package me.saiintbrisson.minecraft;

import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/** Object used to store information about the state of a View's update interval. */
final class BukkitViewUpdateJobImpl implements ViewUpdateJob, Runnable {

    private final Plugin plugin;
    private final VirtualView view;

    @Getter private final long delay;
    @Getter private final long interval;

    private BukkitTask task;
    private boolean interrupted;
    private boolean started;

    public BukkitViewUpdateJobImpl(
            @NotNull Plugin plugin, @NotNull VirtualView view, long delay, long interval) {
        this.plugin = plugin;
        this.view = view;
        this.delay = delay;
        this.interval = interval;
    }

    @Override
    public void run() {
        if (interrupted) return;

        view.update();
    }

    @Override
    public void start() {
        if (interrupted)
            throw new IllegalStateException("Cannot start a interrupted view update job");
        if (task != null) throw new IllegalStateException("View update job already started");

        task = plugin.getServer().getScheduler().runTaskTimer(plugin, this, delay, interval);
        started = true;
    }

    @Override
    public void resume() {
        if (task == null) throw new IllegalStateException("Update job not defined to be resumed");

        if (!started) {
            start();
            return;
        }

        if (!interrupted) throw new IllegalStateException("Update job not started to be resumed");

        interrupted = false;
    }

    @Override
    public void pause() {
        if (task == null) throw new IllegalStateException("Update job not defined to be paused");
        if (!started) throw new IllegalStateException("Update job not started to be paused");
        if (interrupted) throw new IllegalStateException("Update job already interrupted");

        interrupted = true;
    }

    @Override
    public void cancel() {
        if (task == null) return;

        task.cancel();
        task = null;
        interrupted = false;
        started = false;
    }

    void interrupt() {
        this.interrupted = !interrupted;
    }
}
