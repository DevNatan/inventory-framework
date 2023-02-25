package me.devnatan.inventoryframework.pipeline;

import static me.devnatan.inventoryframework.TestUtils.createContextMock;
import static me.devnatan.inventoryframework.TestUtils.createRootMock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.IFItem;
import me.devnatan.inventoryframework.context.IFContext;
import org.junit.jupiter.api.Test;

public class UpdateInterceptorTest {

    @Test
    void clearWhenMarkedForRemoval() {
        Pipeline<IFContext> pipeline = new Pipeline<>(StandardPipelinePhases.UPDATE);
        pipeline.intercept(StandardPipelinePhases.UPDATE, new UpdateInterceptor());

        RootView root = createRootMock();
        IFContext context = createContextMock(root);
        ViewContainer container = mock(ViewContainer.class);
        when(context.getContainer()).thenReturn(container);

        Component component = mock(Component.class);
        when(component.isMarkedForRemoval()).thenReturn(true);
        when(context.getComponents()).thenReturn(Collections.singletonList(component));
        when(root.getContexts()).thenReturn(Collections.singleton(context));

        pipeline.execute(StandardPipelinePhases.UPDATE, context);

        verify(component, times(1)).clear(eq(context));
        verify(component, never()).render(any());
    }

    @Test
    void alwaysRenderIfItemHasRenderHandler() {
        Pipeline<IFContext> pipeline = new Pipeline<>(StandardPipelinePhases.UPDATE);
        pipeline.intercept(StandardPipelinePhases.UPDATE, new UpdateInterceptor());

        RootView root = createRootMock();
        IFContext context = createContextMock(root);
        ViewContainer container = mock(ViewContainer.class);
        when(context.getContainer()).thenReturn(container);

        IFItem<?> component = mock(IFItem.class);
        when(component.isMarkedForRemoval()).thenReturn(false);
        when(component.getRenderHandler()).thenReturn($ -> {
            /* do nothing */
        });

        when(context.getComponents()).thenReturn(Collections.singletonList(component));
        when(root.getContexts()).thenReturn(Collections.singleton(context));

        pipeline.execute(StandardPipelinePhases.UPDATE, context);

        verify(component, never()).clear(eq(context));
        verify(component, times(1)).render(any());
    }

    @Test
    void neverRenderIfItemDoNotHaveRenderHandler() {
        Pipeline<IFContext> pipeline = new Pipeline<>(StandardPipelinePhases.UPDATE);
        pipeline.intercept(StandardPipelinePhases.UPDATE, new UpdateInterceptor());

        RootView root = createRootMock();
        IFContext context = createContextMock(root);
        ViewContainer container = mock(ViewContainer.class);
        when(context.getContainer()).thenReturn(container);

        IFItem<?> component = mock(IFItem.class);
        when(component.isMarkedForRemoval()).thenReturn(false);
        when(component.getRenderHandler()).thenReturn(null);

        when(context.getComponents()).thenReturn(Collections.singletonList(component));
        when(root.getContexts()).thenReturn(Collections.singleton(context));

        pipeline.execute(StandardPipelinePhases.UPDATE, context);

        verify(component, never()).clear(eq(context));
        verify(component, never()).render(any());
    }
}
