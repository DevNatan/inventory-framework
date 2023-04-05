package me.devnatan.inventoryframework.state;

import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.LAYOUT_RESOLUTION;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
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

@ToString(callSuper = true)
public final class PaginationState extends BaseState<Pagination> implements StateWatcher {

    @ApiStatus.Internal
    public static final PipelinePhase PAGINATION_RENDER = new PipelinePhase("pagination-render");

    private final PipelineInterceptor<VirtualView> pipelineInterceptor = new Interceptor(this);

    public PaginationState(long id, @NotNull StateValueFactory valueFactory) {
        super(id, valueFactory);
    }

    @Override
    public void stateRegistered(@NotNull State<?> state, Object caller) {
        if (!(caller instanceof RootView))
            throw new IllegalArgumentException("Pagination state can only be registered by RootView");

        final Pipeline<VirtualView> pipeline = ((RootView) caller).getPipeline();
        pipeline.insertPhaseAfter(LAYOUT_RESOLUTION, PAGINATION_RENDER);
        pipeline.intercept(PAGINATION_RENDER, pipelineInterceptor);
    }

    @Override
    public void stateUnregistered(@NotNull State<?> state, Object caller) {
        if (!(caller instanceof RootView))
            throw new IllegalArgumentException("Pagination state can only be unregistered by RootView");

        (((RootView) caller)).getPipeline().removeInterceptor(PAGINATION_RENDER, pipelineInterceptor);
    }

    @Override
    public void stateValueInitialized(@NotNull StateValueHost host, @NotNull StateValue value, Object initialValue) {}

    @Override
    public void stateValueGet(
            @NotNull State<?> state,
            @NotNull StateValueHost host,
            @NotNull StateValue internalValue,
            Object rawValue) {}

    @Override
    public void stateValueSet(
            @NotNull StateValueHost host, @NotNull StateValue value, Object rawOldValue, Object rawNewValue) {}

    @RequiredArgsConstructor
    private static final class Interceptor implements PipelineInterceptor<VirtualView> {

        private final State<?> state;

        @Override
        public void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
            if (!(subject instanceof IFRenderContext)) return;

            final IFContext context = (IFContext) subject;
            final Pagination pagination = (Pagination) context.getState(state);

            context.addComponent(pagination);
        }
    }
}
