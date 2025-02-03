package me.devnatan.inventoryframework.pipeline

import me.devnatan.inventoryframework.VirtualView
import me.devnatan.inventoryframework.context.CloseContext


class CancelledCloseInterceptor : PipelineInterceptor<VirtualView> {
    override fun intercept(pipeline: PipelineContext<VirtualView>, context: VirtualView) {
        if (context !is CloseContext) return

        if (!context.isCancelled) return

        context.root.nextTick { context.viewer.open(context.container) }
    }
}
