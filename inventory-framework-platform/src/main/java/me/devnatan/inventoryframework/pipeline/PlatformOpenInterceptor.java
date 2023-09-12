package me.devnatan.inventoryframework.pipeline;

import java.util.HashMap;
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

        final PlatformView root = (PlatformView) openContext.getRoot();
        final IFRenderContext render = createRenderContext(openContext);
        root.addContext(render);
        root.renderContext(render);
    }

    IFRenderContext createRenderContext(IFOpenContext openContext) {
        final RootView root = openContext.getRoot();

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
        final ViewContainer createdContainer = elementFactory.createContainer(openContext);

        final IFRenderContext context = elementFactory.createRenderContext(
                openContext.getId(),
                openContext.getRoot(),
                openContext.getConfig(),
                createdContainer,
                new HashMap<>(),
                openContext.getSubject(),
                openContext.getInitialData());

        for (final Viewer viewer : openContext.getIndexedViewers().values()) {
            if (!viewer.isTransitioning()) viewer.setActiveContext(context);
            context.addViewer(viewer);
        }

        return context;
    }
}
