package me.devnatan.inventoryframework.pipeline;

import static me.devnatan.inventoryframework.TestUtils.createRootMock;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFOpenContext;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"rawtypes", "unchecked"})
public class OpenInterceptorTest {

    @Test
    void rethrowExceptionWhenAsyncJobFails() {
        Pipeline<IFContext> pipeline = new Pipeline<>(StandardPipelinePhases.OPEN);
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
    void validatedRootConfiguration() {}

    @Test
    void validatedContextConfiguration() {}
}
