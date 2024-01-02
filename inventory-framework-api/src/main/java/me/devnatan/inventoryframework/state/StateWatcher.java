package me.devnatan.inventoryframework.state;

import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.ApiStatus;

/**
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
@ApiStatus.Internal
@FunctionalInterface
public interface StateWatcher extends PipelineInterceptor<StateValueDiff> {}
