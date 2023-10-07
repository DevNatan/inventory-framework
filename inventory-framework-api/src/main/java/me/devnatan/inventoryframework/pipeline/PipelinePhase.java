package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFConfinedContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFOpenContext;
import me.devnatan.inventoryframework.context.IFRenderContext;

/**
 * Phases are groups of interceptors that can be ordered topologically, defining relationships
 * between them.
 *
 * @see Pipeline
 */
public final class PipelinePhase {

    private final String name;

    public PipelinePhase(String name) {
        this.name = name;
    }

    /**
     * The pipeline phase name.
     *
     * @return The name of this pipeline phase.
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "PipelinePhase{" + "name='" + name + '\'' + '}';
    }

    /**
     * Called when a {@link me.devnatan.inventoryframework.RootView} is initialized for the first time.
     */
    public static final PipelinePhase VIEW_INIT = new PipelinePhase("view-init");

    /**
     * Called before a {@link me.devnatan.inventoryframework.context.IFContext context} is open.
     * In this pipeline phase the pipeline interceptor subject is a {@link IFOpenContext}.
     */
    public static final PipelinePhase CONTEXT_OPEN = new PipelinePhase("context-open");

    /**
     * Called when a {@link IFContext context} is going to be rendered.
     * In this pipeline phase the pipeline interceptor subject is a {@link IFRenderContext}.
     */
    public static final PipelinePhase CONTEXT_RENDER = new PipelinePhase("context-render");

    /**
     * Called during layout resolution phase before rendering phase.
     * In this pipeline phase the pipeline interceptor subject is a {@link IFRenderContext}.
     */
    public static final PipelinePhase CONTEXT_LAYOUT_RESOLUTION = new PipelinePhase("context-layout-resolution");

    /**
     * Called when a {@link IFContext context} is going to be updated.
     * In this pipeline phase the pipeline interceptor subject is a {@link IFRenderContext}.
     */
    public static final PipelinePhase CONTEXT_UPDATE = new PipelinePhase("context-update");

    /**
     * Called when a click is detected in a context.
     * In this pipeline phase the pipeline interceptor subject is a {@link IFConfinedContext}.
     */
    public static final PipelinePhase CONTEXT_CLICK = new PipelinePhase("context-click");

    /**
     * Called when a context is closed for a viewer. This phase is called several times in shared
     * contexts and its complete closure can be determined by the number of viewers.
     * In this pipeline phase the pipeline interceptor subject is a {@link IFCloseContext}.
     */
    public static final PipelinePhase CONTEXT_CLOSE = new PipelinePhase("context-close");

    /**
     * A context is invalidated, or considered invalidated, when there are no more viewers in it.
     * In this pipeline phase the pipeline interceptor subject is a {@link IFContext} (non-confined).
     */
    public static final PipelinePhase CONTEXT_INVALIDATED = new PipelinePhase("context-invalidated");
}
