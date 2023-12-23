package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFOpenContext;
import me.devnatan.inventoryframework.context.IFRenderContext;

/**
 * Phases are groups of interceptors that can be ordered topologically, defining relationships
 * between them.
 *
 * @see Pipeline
 */
public interface PipelinePhase {
    enum Frame implements PipelinePhase {
        /**
         * Called when a view frame is registered.
         */
        FRAME_REGISTERED,

        /**
         * Called when a view frame is unregistered.
         */
        FRAME_UNREGISTERED;
    }

    enum View implements PipelinePhase {
        /**
         * Called when a {@link RootView root view} is initialized.
         */
        VIEW_INIT
    }

    enum Context implements PipelinePhase {
        /**
         * Called when a context is rendered for the first time.
         * At this phase the pipeline interceptor subject is a {@link IFRenderContext}.
         */
        CONTEXT_RENDER,

        /**
         * Called when a context is about to open to a viewer.
         * This phase is called once in a Shared Context.
         * At this phase the pipeline interceptor subject is a {@link IFOpenContext}.
         */
        CONTEXT_OPEN,

        /**
         * Called when a context is closed for a viewer.
         * This phase is called several times in a Shared Context.
         * At this phase the pipeline interceptor subject is a {@link IFCloseContext}.
         */
        CONTEXT_CLOSE,

        /**
         * Called when a context is invalidated. A context is invalidated, or considered invalidated,
         * when there are no more viewers in it.
         * At this phase the pipeline interceptor subject is a {@link IFRenderContext}.
         */
        CONTEXT_INVALIDATION,

        /**
         * Called when a context is updated.
         * At this phase the pipeline interceptor subject is a {@link IFRenderContext}.
         */
        CONTEXT_UPDATE,

        /**
         * Called when a {@link Viewer} is added to a context.
         * At this phase the pipeline interceptor subject is a {@link Viewer}.
         */
        CONTEXT_VIEWER_ADDED,

        /**
         * Called when a {@link Viewer} is removed from a context.
         * At this phase the pipeline interceptor subject is a {@link Viewer}.
         */
        CONTEXT_VIEWER_REMOVED,

        /**
         * Called during layout resolution phase before the {@link #CONTEXT_RENDER} phase.
         * At this phase the pipeline interceptor subject is a {@link IFRenderContext}.
         */
        CONTEXT_LAYOUT_RESOLUTION,

        CONTEXT_SLOT_CLICK
    }

    enum Component implements PipelinePhase {
        COMPONENT_RENDER,
        COMPONENT_UPDATE,
        COMPONENT_CLICK,
        COMPONENT_CLEAR
    }
}
