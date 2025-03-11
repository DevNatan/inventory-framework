package me.devnatan.inventoryframework.internal

import net.minestom.server.MinecraftServer
import net.minestom.server.timer.Task
import net.minestom.server.timer.TaskSchedule

internal class MinestomTaskJobImpl(
    private val intervalInTicks: Int,
    private val execution: Runnable,
) : Job {
    private var task: Task? = null

    override fun isStarted(): Boolean {
        return task != null
    }

    override fun start() {
        if (isStarted) return
        val schedule = TaskSchedule.tick(intervalInTicks)
        task = MinecraftServer.getSchedulerManager().scheduleTask(this::loop, schedule, schedule)
    }

    override fun cancel() {
        if (!isStarted) return
        task?.cancel()
        task = null
    }

    override fun loop() {
        execution.run()
    }
}
