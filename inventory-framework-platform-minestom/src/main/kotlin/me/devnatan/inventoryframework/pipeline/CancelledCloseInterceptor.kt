package me.devnatan.inventoryframework.pipeline

import me.devnatan.inventoryframework.VirtualView
import me.devnatan.inventoryframework.context.CloseContext


class CancelledCloseInterceptor : PipelineInterceptor<VirtualView> {
    override fun intercept(pipeline: PipelineContext<VirtualView>, subject: VirtualView) {
        if (subject !is CloseContext) return

        if (!subject.isCancelled) return

        subject.root.nextTick { subject.viewer.open(subject.container) }
    }
}
