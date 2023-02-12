package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFOpenContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

public final class OpenInterceptor implements PipelineInterceptor<IFContext> {

    @TestOnly
    boolean skipOpen = false;

    @Override
    public void intercept(@NotNull PipelineContext<IFContext> pipeline, IFContext context) {
        System.out.println("open interceptor " + context);
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
                    throw new IllegalStateException("An error occurred in the opening asynchronous job.", error);
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
        final ElementFactory elementFactory = ((PlatformView) root).getElementFactory();
        final ViewContainer container = elementFactory.createContainer(
                openContext,
                openContext.getType().normalize(openContext.getSize()),
                openContext.getTitle(),
                openContext.getType());

        final Viewer viewer = openContext.getViewer();
        final IFRenderContext renderCtx =
                elementFactory.createContext(root, container, viewer, IFRenderContext.class, false);

        renderCtx.addViewer(viewer);
        root.addContext(renderCtx);
        if (root instanceof PlatformView) ((PlatformView) root).onFirstRender(renderCtx);
        root.renderContext(renderCtx);
        container.open(viewer);
    }
}
