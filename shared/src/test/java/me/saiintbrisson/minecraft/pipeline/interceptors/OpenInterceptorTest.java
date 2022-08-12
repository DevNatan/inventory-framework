package me.saiintbrisson.minecraft.pipeline.interceptors;

import me.saiintbrisson.minecraft.OpenViewContext;
import me.saiintbrisson.minecraft.VirtualView;
import me.saiintbrisson.minecraft.pipeline.Pipeline;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static me.saiintbrisson.minecraft.AbstractView.OPEN;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// TODO container properties inheritance
// TODO open context data inheritance to lifecycle view
public class OpenInterceptorTest {

	@Test
	void shouldFinishPipelineWhenAsyncJobFail() {
		Pipeline<VirtualView> pipeline = new Pipeline<>(OPEN);
		OpenInterceptor interceptor = new OpenInterceptor();

		// we need to set it here to skip post open process because the values needed for this
		// are not defined in the mock, so it will fail with NPE and make this test inconsistent
		interceptor.skipOpen = true;

		pipeline.intercept(OPEN, interceptor);
		pipeline.intercept(OPEN, ($, $$) -> fail("Pipeline must be finished"));

		OpenViewContext context = mock(OpenViewContext.class);

		when(context.getAsyncOpenJob()).thenReturn(CompletableFuture.supplyAsync(() -> {
			throw new IllegalStateException();
		}));

		pipeline.execute(OPEN, context);
	}

}
