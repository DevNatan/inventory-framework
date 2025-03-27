package me.devnatan.inventoryframework

import me.devnatan.inventoryframework.component.MinestomIemComponentBuilder
import me.devnatan.inventoryframework.context.CloseContext
import me.devnatan.inventoryframework.context.Context
import me.devnatan.inventoryframework.context.OpenContext
import me.devnatan.inventoryframework.context.RenderContext
import me.devnatan.inventoryframework.context.SlotClickContext
import me.devnatan.inventoryframework.pipeline.CancelledCloseInterceptor
import me.devnatan.inventoryframework.pipeline.GlobalClickInterceptor
import me.devnatan.inventoryframework.pipeline.ItemClickInterceptor
import me.devnatan.inventoryframework.pipeline.ItemCloseOnClickInterceptor
import me.devnatan.inventoryframework.pipeline.Pipeline
import me.devnatan.inventoryframework.pipeline.StandardPipelinePhases
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import org.jetbrains.annotations.ApiStatus.OverrideOnly

/** Bukkit platform [PlatformView] implementation. */
@OverrideOnly
open class View :
    PlatformView<
        ViewFrame,
        Player,
        MinestomIemComponentBuilder,
        Context,
        OpenContext,
        CloseContext,
        RenderContext,
        SlotClickContext,
    >() {
    public override fun registerPlatformInterceptors() {
        val pipeline: Pipeline<in VirtualView> = pipeline
        pipeline.intercept(StandardPipelinePhases.CLICK, ItemClickInterceptor())
        pipeline.intercept(StandardPipelinePhases.CLICK, GlobalClickInterceptor())
        pipeline.intercept(StandardPipelinePhases.CLICK, ItemCloseOnClickInterceptor())
        pipeline.intercept(StandardPipelinePhases.CLOSE, CancelledCloseInterceptor())
    }

    override fun nextTick(task: Runnable) {
        MinecraftServer.getSchedulerManager().scheduleNextTick(task)
    }
}
