package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.VirtualView;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface Pipelined {

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    @NotNull
    Pipeline<VirtualView> getPipeline();
}
