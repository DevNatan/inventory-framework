package me.devnatan.inventoryframework.context;

import java.util.HashMap;
import me.devnatan.inventoryframework.*;
import me.devnatan.inventoryframework.exception.InvalidLayoutException;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

final class ContextOpenInterceptor implements PipelineInterceptor<IFContext> {

    @TestOnly
    boolean skipOpen = false;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void intercept(@NotNull PipelineContext<IFContext> pipeline, IFContext subject) {
        if (!(subject instanceof IFOpenContext)) return;
        final IFOpenContext openContext = (IFOpenContext) subject;

        final PlatformView root = (PlatformView) openContext.getRoot();

        // Initialize all states to allow them to be modified in open handler
        for (final State<?> state : root.getStateRegistry())
            openContext.initializeState(state.internalId(), state.factory().create(openContext, state));

        root.onOpen(openContext);

        if (openContext.getAsyncOpenJob() == null) {
            finishOpen(pipeline, openContext);
            return;
        }

        openContext
                .getAsyncOpenJob()
                .thenRun(() -> finishOpen(pipeline, openContext))
                .exceptionally(error -> {
                    // TODO invalidate context
                    pipeline.finish();
                    throw new InventoryFrameworkException("An error occurred in the opening asynchronous job.", error);
                });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void finishOpen(@NotNull PipelineContext<IFContext> pipeline, @NotNull IFOpenContext openContext) {
        if (openContext.isCancelled()) {
            pipeline.finish();
            return;
        }

        if (skipOpen) return;

        final PlatformView root = (PlatformView) openContext.getRoot();
        final IFRenderContext render = createRenderContext(openContext);
        root.addContext(render);
        root.renderContext(render);
    }

    @SuppressWarnings("unchecked")
    IFRenderContext createRenderContext(IFOpenContext openContext) {
        @SuppressWarnings("rawtypes")
        final PlatformView root = (PlatformView) openContext.getRoot();

        final ViewConfig contextConfig = openContext.getConfig();
        final String[] layout = contextConfig.getLayout();
        if (layout != null) {
            if (contextConfig.getSize() != 0 && contextConfig.getSize() != layout.length) {
                // TODO Needs a more detailed error message
                throw new InvalidLayoutException("The layout length differs from the set inventory size.");
            }
            openContext.modifyConfig().size(layout.length);
        }

        final ElementFactory elementFactory = root.getElementFactory();

        if (openContext.getContainer() == null) openContext.setContainer(elementFactory.createContainer(openContext));
        contextConfig.getModifiers().forEach(modifier -> modifier.apply(openContext.modifyConfig(), openContext));

        final IFRenderContext renderContext = elementFactory.createRenderContext(
                openContext.getId(),
                root,
                openContext.getConfig(),
                openContext.getContainer(),
                new HashMap<>(),
                openContext.isShared() ? null : openContext.getViewer(),
                openContext.getInitialData());

        renderContext.setEndless(openContext.isEndless());

        // We need to recreate all state values here (with the same value) to ensure that the
        // StateValueHost used in the StateValueFactory is a render context and not an open context
        openContext.getStateValues().forEach((id, value) -> {
            final State<?> state = root.getStateRegistry().getState(id);
            final StateValue recreatedValue = state.factory().create(renderContext, state);
            recreatedValue.set(value.get());

            renderContext.initializeState(id, recreatedValue);
        });

        for (final Viewer viewer : openContext.getIndexedViewers().values()) {
            if (!viewer.isTransitioning()) viewer.setActiveContext(renderContext);
            root.onViewerAdded(renderContext, viewer.getPlatformInstance(), renderContext.getInitialData());
            renderContext.addViewer(viewer);
        }

        renderContext.setActive(true);
        return renderContext;
    }
}
