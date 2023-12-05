package me.devnatan.inventoryframework.state;

import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.LAYOUT_RESOLUTION;

import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
@ApiStatus.Internal
public final class PaginationState extends BaseState<Pagination> implements StateWatcher {

    @ApiStatus.Internal
    public static final PipelinePhase PAGINATION_RENDER = new PipelinePhase("pagination-render");

    private final PipelineInterceptor<VirtualView> pipelineInterceptor = new Interceptor(this);

    public PaginationState(long id, @NotNull StateValueFactory valueFactory) {
        super(id, valueFactory);
    }

    @Override
    public void stateRegistered(@NotNull State<?> state, Object caller) {
        // TODO Allow usage in abstract components
        if (!(caller instanceof RootView))
            throw new IllegalArgumentException(
                    "Pagination state can only be registered by RootView: " + caller.getClass());

        final Pipeline<VirtualView> pipeline = ((RootView) caller).getPipeline();
        pipeline.insertPhaseAfter(LAYOUT_RESOLUTION, PAGINATION_RENDER);
        pipeline.intercept(PAGINATION_RENDER, pipelineInterceptor);
    }

    @Override
    public void stateUnregistered(@NotNull State<?> state, Object caller) {
        // TODO Allow usage in abstract components
        if (!(caller instanceof RootView))
            throw new IllegalArgumentException(
                    "Pagination state can only be unregistered by RootView: " + caller.getClass());

        (((RootView) caller)).getPipeline().removeInterceptor(PAGINATION_RENDER, pipelineInterceptor);
    }

    @Override
    public void stateValueGet(
            @NotNull State<?> state,
            @NotNull StateValueHost host,
            @NotNull StateValue internalValue,
            Object rawValue) {}

    @Override
    public void stateValueSet(
            @NotNull StateValueHost host, @NotNull StateValue value, Object rawOldValue, Object rawNewValue) {}

    @Override
    public String toString() {
        return "PaginationState{" + "pipelineInterceptor=" + pipelineInterceptor + "} " + super.toString();
    }

    private static final class Interceptor implements PipelineInterceptor<VirtualView> {

        private final State<?> state;

        public Interceptor(State<?> state) {
            this.state = state;
        }

        @Override
        public void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
            if (!(subject instanceof IFRenderContext)) return;

            final IFContext context = (IFContext) subject;
            final Pagination pagination = (Pagination) context.getRawStateValue(state);

            context.addComponent(pagination);
        }
    }
}
