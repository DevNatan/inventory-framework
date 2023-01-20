package me.saiintbrisson.minecraft.pipeline.interceptors;

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
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.internal.Job;
import me.devnatan.inventoryframework.internal.platform.Viewer;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import org.junit.jupiter.api.Test;

class ScheduledUpdateInterceptorTest {

    @Test
    public void givenUpdateJobStartWhenNoViewersAvailableThenSkipUpdateJobStart() {
        Pipeline<IFContext> pipeline = new Pipeline<>(RENDER);
        pipeline.intercept(RENDER, new ScheduledUpdateInterceptor.Render() {
            @Override
            void onIntercept() {
                fail("Cannot only be started on the first viewer");
            }
        });

        AbstractView view = new AbstractView() {};
        view.setUpdateJob(new Job.InternalJobImpl(() -> {}));

        IFContext context = mock(IFContext.class);
        when(context.getRoot()).thenReturn(view);
        when(context.getViewers()).thenReturn(Collections.emptyList());

        pipeline.execute(RENDER, context);
    }

    @Test
    public void givenUpdateJobStartWhenMoreThanOneViewerAvailableThenSkipUpdateJobStart() {
        Pipeline<IFContext> pipeline = new Pipeline<>(RENDER);
        pipeline.intercept(RENDER, new ScheduledUpdateInterceptor.Render() {
            @Override
            void onIntercept() {
                fail("Cannot only be started on the first viewer");
            }
        });

        AbstractView view = new AbstractView() {};
        view.setUpdateJob(new Job.InternalJobImpl(() -> {}));

        IFContext context = mock(IFContext.class);
        when(context.getRoot()).thenReturn(view);

        Viewer viewer = mock(Viewer.class);
        when(context.getViewers()).thenReturn(Arrays.asList(viewer, viewer));

        pipeline.execute(RENDER, context);
    }

    @Test
    public void givenUpdateJobStartWhenOneViewerIsAvailableThenJobIsStarted() {
        AtomicBoolean intercepted = new AtomicBoolean();
        Pipeline<IFContext> pipeline = new Pipeline<>(RENDER);
        pipeline.intercept(RENDER, new ScheduledUpdateInterceptor.Render() {
            @Override
            void onIntercept() {
                intercepted.set(true);
            }
        });

        AbstractView view = new AbstractView() {};
        view.setUpdateJob(new Job.InternalJobImpl(() -> {}));

        IFContext context = mock(IFContext.class);
        when(context.getRoot()).thenReturn(view);

        Viewer viewer = mock(Viewer.class);
        when(context.getViewers()).thenReturn(Collections.singletonList(viewer));

        pipeline.execute(RENDER, context);
        assertTrue(intercepted.get());
    }

    @Test
    public void whenPipelineStartExpectUpdateStartedAndEndedProperly() {
        AtomicBoolean started = new AtomicBoolean(false);
        Pipeline<IFContext> pipeline = new Pipeline<>(RENDER, CLOSE);
        pipeline.intercept(RENDER, new ScheduledUpdateInterceptor.Render() {
            @Override
            void onIntercept() {
                started.set(true);
            }
        });
        pipeline.intercept(CLOSE, new ScheduledUpdateInterceptor.Close() {
            @Override
            void onIntercept() {
                started.set(false);
            }
        });

        AbstractView view = new AbstractView() {};
        view.setUpdateJob(new Job.InternalJobImpl(() -> {}));

        IFContext context = mock(IFContext.class);
        when(context.getRoot()).thenReturn(view);

        Viewer viewer = mock(Viewer.class);
        when(context.getViewers()).thenReturn(Collections.singletonList(viewer));

        pipeline.execute(RENDER, context);
        assertTrue(started.get());

        pipeline.execute(CLOSE, context);
        assertFalse(started.get());
    }

    @Test
    public void givenMoreThanOneViewerWhenPipelinesCalledExpectUpdateNotInterrupted() {
        AtomicBoolean started = new AtomicBoolean(false);
        Pipeline<IFContext> pipeline = new Pipeline<>(RENDER, CLOSE);
        pipeline.intercept(RENDER, new ScheduledUpdateInterceptor.Render() {
            @Override
            void onIntercept() {
                started.set(true);
            }
        });
        pipeline.intercept(CLOSE, new ScheduledUpdateInterceptor.Close() {
            @Override
            void onIntercept() {
                started.set(false);
            }
        });

        AbstractView view = new AbstractView() {};
        view.setUpdateJob(new Job.InternalJobImpl(() -> {}));

        IFContext context = mock(IFContext.class);
        when(context.getRoot()).thenReturn(view);

        Viewer viewer = mock(Viewer.class);
        when(context.getViewers()).thenReturn(Collections.singletonList(viewer));

        pipeline.execute(RENDER, context);
        assertTrue(started.get());

        when(context.getViewers()).thenReturn(Arrays.asList(viewer, viewer));

        pipeline.execute(CLOSE, context);
        assertTrue(started.get());
    }
}
