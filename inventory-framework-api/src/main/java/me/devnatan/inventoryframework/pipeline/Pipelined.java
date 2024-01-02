package me.devnatan.inventoryframework.pipeline;

import org.jetbrains.annotations.ApiStatus;

public interface Pipelined {

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    void interceptPipelineCall(PipelinePhase phase, PipelineInterceptor<?> interceptor);
}
