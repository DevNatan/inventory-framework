package me.saiintbrisson.minecraft;

import static me.saiintbrisson.minecraft.AbstractView.CLOSE;
import static me.saiintbrisson.minecraft.AbstractView.RENDER;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import me.saiintbrisson.minecraft.pipeline.Pipeline;
import me.saiintbrisson.minecraft.pipeline.interceptors.AutomaticUpdateInitiationInterceptor;
import org.junit.jupiter.api.Test;

class ScheduleUpdateTest {

    @Test
    public void givenUpdateJobStartWhenNoViewersAvailableThenSkipUpdateJobStart() {
        Pipeline<ViewContext> pipeline = new Pipeline<>(RENDER);
        pipeline.intercept(RENDER, new AutomaticUpdateInitiationInterceptor.Render() {
            @Override
            void calledSuccessfully() {
                fail("Cannot only be started on the first viewer");
            }
        });

        ViewContext context = mock(ViewContext.class);
        when(context.getViewers()).thenReturn(Collections.emptyList());

        ViewUpdateJob job = mock(ViewUpdateJob.class);
        when(context.getUpdateJob()).thenReturn(job);

        pipeline.execute(RENDER, context);
    }

    @Test
    public void givenUpdateJobStartWhenMoreThanOneViewerAvailableThenSkipUpdateJobStart() {
        Pipeline<ViewContext> pipeline = new Pipeline<>(RENDER);
        pipeline.intercept(RENDER, new AutomaticUpdateInitiationInterceptor.Render() {
            @Override
            void calledSuccessfully() {
                fail("Cannot only be started on the first viewer");
            }
        });

        ViewContext context = mock(ViewContext.class);
        Viewer viewer = mock(Viewer.class);
        when(context.getViewers()).thenReturn(Arrays.asList(viewer, viewer));

        ViewUpdateJob job = mock(ViewUpdateJob.class);
        when(context.getUpdateJob()).thenReturn(job);

        pipeline.execute(RENDER, context);
    }

    @Test
    public void givenUpdateJobStartWhenOneViewerIsAvailableThenJobIsStarted() {
        AtomicBoolean intercepted = new AtomicBoolean();
        Pipeline<ViewContext> pipeline = new Pipeline<>(RENDER);
        pipeline.intercept(RENDER, new AutomaticUpdateInitiationInterceptor.Render() {
            @Override
            void calledSuccessfully() {
                intercepted.set(true);
            }
        });

        ViewContext context = mock(ViewContext.class);
        Viewer viewer = mock(Viewer.class);
        when(context.getViewers()).thenReturn(Collections.singletonList(viewer));

        ViewUpdateJob job = mock(ViewUpdateJob.class);
        when(context.getUpdateJob()).thenReturn(job);

        pipeline.execute(RENDER, context);
        assertTrue(intercepted.get());
    }

    @Test
    public void whenPipelinesCalledExpectUpdateStartedAndEndedProperly() {
        AtomicBoolean started = new AtomicBoolean(false);
        Pipeline<ViewContext> pipeline = new Pipeline<>(RENDER, CLOSE);
        pipeline.intercept(RENDER, new AutomaticUpdateInitiationInterceptor.Render() {
            @Override
            void calledSuccessfully() {
                started.set(true);
            }
        });
        pipeline.intercept(CLOSE, new AutomaticUpdateInitiationInterceptor.Close() {
            @Override
            void calledSuccessfully() {
                started.set(false);
            }
        });

        ViewContext context = mock(ViewContext.class);
        Viewer viewer = mock(Viewer.class);
        when(context.getViewers()).thenReturn(Collections.singletonList(viewer));

        ViewUpdateJob job = mock(ViewUpdateJob.class);
        when(job.isStarted()).thenReturn(true);
        when(context.getUpdateJob()).thenReturn(job);

        pipeline.execute(RENDER, context);
        assertTrue(started.get());

        pipeline.execute(CLOSE, context);
        assertFalse(started.get());
    }

    @Test
    public void givenMoreThanOneViewerWhenPipelinesCalledExpectUpdateNotInterrupted() {
        AtomicBoolean started = new AtomicBoolean(false);
        Pipeline<ViewContext> pipeline = new Pipeline<>(RENDER, CLOSE);
        pipeline.intercept(RENDER, new AutomaticUpdateInitiationInterceptor.Render() {
            @Override
            void calledSuccessfully() {
                started.set(true);
            }
        });
        pipeline.intercept(CLOSE, new AutomaticUpdateInitiationInterceptor.Close() {
            @Override
            void calledSuccessfully() {
                started.set(false);
            }
        });

        ViewContext context = mock(ViewContext.class);
        Viewer viewer = mock(Viewer.class);
        when(context.getViewers()).thenReturn(Collections.singletonList(viewer));

        ViewUpdateJob job = mock(ViewUpdateJob.class);
        when(job.isStarted()).thenReturn(true);
        when(context.getUpdateJob()).thenReturn(job);

        pipeline.execute(RENDER, context);
        assertTrue(started.get());

        when(context.getViewers()).thenReturn(Arrays.asList(viewer, viewer));

        pipeline.execute(CLOSE, context);
        assertTrue(started.get());
    }
}
