package me.devnatan.inventoryframework.internal;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

class BukkitTaskJobImpl implements Job {

    private final Plugin plugin;
    private final long intervalInTicks;
    private final Runnable execution;
    private BukkitTask task;

    public BukkitTaskJobImpl(Plugin plugin, long intervalInTicks, Runnable execution) {
        this.plugin = plugin;
        this.intervalInTicks = intervalInTicks;
        this.execution = execution;
    }

    @Override
    public boolean isStarted() {
        return task != null;
    }

    @Override
    public void start() {
        if (isStarted()) return;
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, this::loop, intervalInTicks, intervalInTicks);
    }

    @Override
    public void cancel() {
        if (!isStarted()) return;
        task.cancel();
        task = null;
    }

    @Override
    public void loop() {
        execution.run();
    }
}
