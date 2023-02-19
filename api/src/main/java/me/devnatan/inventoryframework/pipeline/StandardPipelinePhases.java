package me.devnatan.inventoryframework.pipeline;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.context.IFContext;

/**
 * Pipeline phases used internally by the framework. All phases have a {@link IFContext} as subject.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StandardPipelinePhases {

    /**
     * Called when a {@link RootView root} is initialized for the first time.
     */
    public static final PipelinePhase INIT = new PipelinePhase("init");

    /**
     * Called when a context is about to open.
     */
    public static final PipelinePhase OPEN = new PipelinePhase("open");

    /**
     * Called when a {@link IFContext context} is going to be rendered.
     */
    public static final PipelinePhase FIRST_RENDER = new PipelinePhase("first-render");

    /**
     * Called when a {@link IFContext context} is going to be updated.
     */
    public static final PipelinePhase UPDATE = new PipelinePhase("update");

    /**
     * Called when a click is detected in a context.
     */
    public static final PipelinePhase CLICK = new PipelinePhase("click");

    /**
     * Called when a context is closed for a viewer. This phase is called several times in shared
     * contexts and its complete closure can be determined by the number of viewers.
     */
    public static final PipelinePhase CLOSE = new PipelinePhase("close");

    /**
     * A context is invalidated, or considered invalidated, when there are no more viewers in it.
     */
    public static final PipelinePhase INVALIDATION = new PipelinePhase("invalidation");
}
