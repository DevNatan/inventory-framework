package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFConfinedContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFOpenContext;
import me.devnatan.inventoryframework.context.IFRenderContext;

/**
 * Pipeline phases used internally by the framework. All phases have a {@link IFContext} as subject.
 */
public final class StandardPipelinePhases {

    private StandardPipelinePhases() {}

    public static final PipelinePhase LAYOUT_RESOLUTION = new PipelinePhase("layout-resolution");

    /**
     * Called when a click is detected in a context.
     * In this pipeline phase the pipeline interceptor subject is a {@link IFConfinedContext}.
     */
    public static final PipelinePhase CLICK = new PipelinePhase("click");

    /**
     * Called during layout resolution phase before {@link #FIRST_RENDER} phase.
     * In this pipeline phase the pipeline interceptor subject is a {@link IFRenderContext}.
     */
    //    public static final PipelinePhase LAYOUT_RESOLUTION = new PipelinePhase("layout-resolution");
}
