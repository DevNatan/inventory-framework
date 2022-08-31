package me.saiintbrisson.minecraft.pipeline.interceptors;

import static me.saiintbrisson.minecraft.AbstractView.OPEN;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import me.saiintbrisson.minecraft.OpenViewContext;
import me.saiintbrisson.minecraft.VirtualView;
import me.saiintbrisson.minecraft.pipeline.Pipeline;
import me.saiintbrisson.minecraft.pipeline.PipelineContext;
import org.junit.jupiter.api.Test;

// TODO container properties inheritance
// TODO open context data inheritance to lifecycle view
public class OpenInterceptorTest {

    @Test
    void shouldFinishPipelineWhenAsyncJobFail() throws InterruptedException {
        Pipeline<VirtualView> pipeline = new Pipeline<>(OPEN);
        OpenInterceptor interceptor = new OpenInterceptor();

        // we need to set it here to skip post open process because the values needed for this
        // are not defined in the mock, so it will fail with NPE and make this test inconsistent
        interceptor.skipOpen = true;

        pipeline.intercept(OPEN, interceptor);
        pipeline.intercept(OPEN, ($, $$) -> fail("Pipeline must be finished"));

        OpenViewContext context = mock(OpenViewContext.class);

        CountDownLatch lock = new CountDownLatch(1);
        when(context.getAsyncOpenJob())
                .thenReturn(CompletableFuture.<Void>supplyAsync(() -> {
                            throw new IllegalStateException();
                        })
                        .whenComplete(($, $$) -> lock.countDown()));

        lock.await(2, TimeUnit.SECONDS);

        PipelineContext<?> pipelineContext = pipeline.execute(OPEN, context);
        assertTrue(
                pipelineContext.isFinished(),
                "Pipeline context must finish after exception thrown inside open interceptor job");
    }
}
