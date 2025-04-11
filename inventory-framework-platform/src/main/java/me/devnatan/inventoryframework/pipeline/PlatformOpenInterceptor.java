package me.devnatan.inventoryframework.pipeline;

import java.util.HashMap;
import me.devnatan.inventoryframework.*;
import me.devnatan.inventoryframework.context.IFOpenContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.PlatformRenderContext;
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

        final IFRenderContext renderContext = elementFactory.createRenderContext(
                openContext.getId(),
                root,
                openContext.getConfig(),
                null,
                new HashMap<>(),
                openContext.isShared() ? null : openContext.getViewer(),
                openContext.getInitialData());

        ViewContainer createdContainer = openContext.getContainer();
        if (createdContainer == null) createdContainer = elementFactory.createContainer(renderContext);

        ((PlatformRenderContext) renderContext).setContainer(createdContainer);
        renderContext.setEndless(openContext.isEndless());
        openContext.getStateValues().forEach(renderContext::initializeState);

        for (final Viewer viewer : renderContext.getIndexedViewers().values()) {
            setupViewer(viewer, renderContext, root);
        }

        final Viewer viewer = renderContext.getViewer();
        if (viewer != null) setupViewer(viewer, renderContext, root);

        renderContext.setActive(true);
        return renderContext;
    }

    private void setupViewer(Viewer viewer, IFRenderContext context, PlatformView root) {
        viewer.setActiveContext(context);

        if (!viewer.isSwitching()) root.getFramework().addViewer(viewer);

        // TODO Pass viewer object as parameter instead
        root.onViewerAdded(context, viewer.getPlatformInstance(), context.getInitialData());
        context.addViewer(viewer);
    }
}
