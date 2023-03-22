package me.devnatan.inventoryframework.pipeline;

import static me.devnatan.inventoryframework.TestUtils.createContextMock;
import static me.devnatan.inventoryframework.TestUtils.createRootMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFOpenContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"rawtypes", "unchecked"})
public class OpenInterceptorTest {

    @Test
    void rethrowExceptionWhenAsyncJobFails() {
        Pipeline<VirtualView> pipeline = new Pipeline<>(StandardPipelinePhases.OPEN);
        pipeline.intercept(StandardPipelinePhases.OPEN, new OpenInterceptor());

        RootView root = createRootMock();
        IFOpenContext context = mock(IFOpenContext.class);
        when(context.getRoot()).thenReturn(root);
        when(context.getAsyncOpenJob()).thenReturn(CompletableFuture.runAsync(() -> {
            throw new RuntimeException();
        }));

        pipeline.execute(StandardPipelinePhases.OPEN, context);

        assertThrows(CompletionException.class, () -> context.getAsyncOpenJob().join());
        assertTrue(context.getAsyncOpenJob().isCompletedExceptionally());
    }

    @Test
    void finishPipelineIfContextIsCancelled() {
        RootView root = createRootMock();
        IFOpenContext context = mock(IFOpenContext.class);
        when(context.getRoot()).thenReturn(root);
        when(context.getAsyncOpenJob()).thenReturn(null);
        when(context.isCancelled()).thenReturn(true);

        PipelineContext pipelineContext = mock(PipelineContext.class);
        new OpenInterceptor().intercept(pipelineContext, context);

        verify(pipelineContext, times(1)).finish();
    }

    @Test
    void mergeConfigurationPreservedRoot() {
        Pipeline<VirtualView> pipeline = new Pipeline<>(StandardPipelinePhases.OPEN);
        OpenInterceptor interceptor = new OpenInterceptor();
        pipeline.intercept(StandardPipelinePhases.OPEN, interceptor);

        RootView root = createRootMock();
        ViewConfig rootConfig = mock(ViewConfig.class);
        when(rootConfig.getTitle()).thenReturn("Root title");
        when(rootConfig.getType()).thenReturn(ViewType.FURNACE);
        when(rootConfig.merge(any())).thenCallRealMethod();
        when(root.getConfig()).thenReturn(rootConfig);

        IFOpenContext context = createContextMock(root, IFOpenContext.class);
        when(context.modifyConfig()).thenReturn(new ViewConfigBuilder());

        ViewContainer container = mock(ViewContainer.class);
        when(context.getContainer()).thenReturn(container);

        pipeline.execute(StandardPipelinePhases.OPEN, context);

        IFRenderContext render = interceptor.createRenderContext(context);

        assertEquals("Root title", render.getContainer().getTitle());
        assertEquals(ViewType.FURNACE, render.getContainer().getType());
    }
}
