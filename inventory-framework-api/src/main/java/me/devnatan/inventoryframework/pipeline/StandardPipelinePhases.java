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

    /**
     * Called when a {@link RootView root} is initialized for the first time.
     * In this pipeline phase the pipeline interceptor subject is a {@link RootView}.
     */
    public static final PipelinePhase INIT = new PipelinePhase("init");

    /**
     * Called when a context is about to open.
     * In this pipeline phase the pipeline interceptor subject is a {@link IFOpenContext}.
     */
    public static final PipelinePhase OPEN = new PipelinePhase("open");

    /**
     * Called when a {@link IFContext context} is going to be rendered.
     * In this pipeline phase the pipeline interceptor subject is a {@link IFRenderContext}.
     */
    public static final PipelinePhase FIRST_RENDER = new PipelinePhase("first-render");

    public static final PipelinePhase LAYOUT_RESOLUTION = new PipelinePhase("layout-resolution");

    /**
     * Called when a {@link IFContext context} is going to be updated.
     * In this pipeline phase the pipeline interceptor subject is a {@link IFRenderContext}.
     */
    public static final PipelinePhase UPDATE = new PipelinePhase("update");

    /**
     * Called when a click is detected in a context.
     * In this pipeline phase the pipeline interceptor subject is a {@link IFConfinedContext}.
     */
    public static final PipelinePhase CLICK = new PipelinePhase("click");

    /**
     * Called when a context is closed for a viewer. This phase is called several times in shared
     * contexts and its complete closure can be determined by the number of viewers.
     * In this pipeline phase the pipeline interceptor subject is a {@link IFCloseContext}.
     */
    public static final PipelinePhase CLOSE = new PipelinePhase("close");

    /**
     * A context is invalidated, or considered invalidated, when there are no more viewers in it.
     * In this pipeline phase the pipeline interceptor subject is a {@link IFContext} (non-confined).
     */
    public static final PipelinePhase INVALIDATION = new PipelinePhase("invalidation");

    /**
     * Called during layout resolution phase before {@link #FIRST_RENDER} phase.
     * In this pipeline phase the pipeline interceptor subject is a {@link IFRenderContext}.
     */
    //    public static final PipelinePhase LAYOUT_RESOLUTION = new PipelinePhase("layout-resolution");

	/**
	 * Called when a component is about to be rendered.
	 */
	public static final PipelinePhase COMPONENT_RENDER = new PipelinePhase("component-render");
}
