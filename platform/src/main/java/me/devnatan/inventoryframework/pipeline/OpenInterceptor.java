package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFOpenContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.exception.InventoryFrameworkException;
import me.devnatan.inventoryframework.internal.ElementFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

public final class OpenInterceptor implements PipelineInterceptor<IFContext> {

    @TestOnly
    boolean skipOpen = false;

    @Override
    public void intercept(@NotNull PipelineContext<IFContext> pipeline, IFContext context) {
        if (!(context instanceof IFOpenContext)) return;

        final IFOpenContext openContext = (IFOpenContext) context;
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

    @SuppressWarnings("rawtypes")
    private void finishOpen(@NotNull PipelineContext<IFContext> pipeline, @NotNull IFOpenContext openContext) {
        if (openContext.isCancelled()) {
            pipeline.finish();
            return;
        }

        if (skipOpen) return;

        final RootView root = openContext.getRoot();
        final ElementFactory elementFactory = root.getElementFactory();
        final ViewConfig contextConfig = openContext.getConfig();
        final ViewContainer container = elementFactory.createContainer(
                openContext,
                contextConfig.getType().normalize(contextConfig.getSize()),
                contextConfig.getTitle(),
                contextConfig.getType());

        final Viewer viewer = openContext.getViewer();
        final IFRenderContext renderCtx =
                elementFactory.createContext(root, container, viewer, IFRenderContext.class, false, openContext);

        renderCtx.addViewer(viewer);
        root.addContext(renderCtx);

        // TODO call onFirstRender on `renderContext` and move this interceptor to framework module
        if (root instanceof PlatformView) ((PlatformView) root).onFirstRender(renderCtx);
        root.renderContext(renderCtx);
        container.open(viewer);
    }
}
