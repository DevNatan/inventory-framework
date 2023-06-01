package me.devnatan.inventoryframework.internal;

import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

@RequiredArgsConstructor
class BukkitTaskJobImpl implements Job {

    private final Plugin plugin;
    private final long intervalInTicks;
    private final Runnable execution;
    private BukkitTask task;

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
