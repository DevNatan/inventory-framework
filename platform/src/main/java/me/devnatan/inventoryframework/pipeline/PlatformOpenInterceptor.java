package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.*;
import me.devnatan.inventoryframework.context.IFOpenContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.exception.InvalidLayoutException;
import me.devnatan.inventoryframework.internal.ElementFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

public final class PlatformOpenInterceptor implements PipelineInterceptor<VirtualView> {

    @TestOnly
    boolean skipOpen = false;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, VirtualView subject) {
        if (!(subject instanceof IFOpenContext)) return;

        final IFOpenContext openContext = (IFOpenContext) subject;

        if (openContext.getRoot() instanceof PlatformView) {
            ((PlatformView) openContext.getRoot()).onOpen(openContext);
        }

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
    private void finishOpen(@NotNull PipelineContext<VirtualView> pipeline, @NotNull IFOpenContext openContext) {
        if (openContext.isCancelled()) {
            pipeline.finish();
            return;
        }

        if (skipOpen) return;

        final RootView root = openContext.getRoot();
        final Viewer viewer = openContext.getViewer();
        final IFRenderContext render = createRenderContext(openContext);

        // TODO call onFirstRender on `renderContext` and move this interceptor to framework module
        if (root instanceof PlatformView) ((PlatformView) root).onFirstRender(render);

        root.renderContext(render);
        render.getContainer().open(viewer);
    }

    IFRenderContext createRenderContext(IFOpenContext openContext) {
        final ElementFactory elementFactory = openContext.getRoot().getElementFactory();

        final ViewConfig contextConfig = openContext.getConfig();
        final String[] layout = contextConfig.getLayout();
        if (layout != null) {
            if (contextConfig.getSize() != 0 && contextConfig.getSize() != layout.length) {
                throw new InvalidLayoutException("The layout length differs from the set inventory size.");
            }
            openContext.modifyConfig().size(layout.length);
        }

        final ViewContainer container = elementFactory.createContainer(openContext);
        final Viewer viewer = openContext.getViewer();
        final IFRenderContext renderCtx = elementFactory.createContext(
                openContext.getRoot(), container, viewer, IFRenderContext.class, false, openContext);

        renderCtx.addViewer(viewer);
        openContext.getRoot().addContext(renderCtx);
        return renderCtx;
    }
}
