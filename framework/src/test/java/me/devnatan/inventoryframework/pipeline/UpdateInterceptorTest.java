package me.devnatan.inventoryframework.pipeline;

import static me.devnatan.inventoryframework.TestUtils.createContextMock;
import static me.devnatan.inventoryframework.TestUtils.createRootMock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
import me.devnatan.inventoryframework.component.ItemComponent;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import org.junit.jupiter.api.Test;

public class UpdateInterceptorTest {

    @Test
    void clearWhenMarkedForRemoval() {
        Pipeline<IFContext> pipeline = new Pipeline<>(StandardPipelinePhases.UPDATE);
        pipeline.intercept(StandardPipelinePhases.UPDATE, new UpdateInterceptor());

        RootView root = createRootMock();
        IFContext context = createContextMock(root, IFRenderContext.class);
        ViewContainer container = mock(ViewContainer.class);
        when(context.getContainer()).thenReturn(container);

        Component component = mock(Component.class);
        when(context.isMarkedForRemoval(anyInt())).thenReturn(true);
        when(context.getComponents()).thenReturn(Collections.singletonList(component));
        when(root.getContexts()).thenReturn(Collections.singleton(context));

        pipeline.execute(StandardPipelinePhases.UPDATE, context);

        verify(component, times(1)).clear(eq(context));
        verify(component, never()).updated(any());
        verify(component, never()).render(any());
    }

    @Test
    void alwaysRenderIfItemHasRenderHandler() {
        Pipeline<IFContext> pipeline = new Pipeline<>(StandardPipelinePhases.UPDATE);
        pipeline.intercept(StandardPipelinePhases.UPDATE, new UpdateInterceptor());

        RootView root = createRootMock();
        IFContext context = createContextMock(root, IFRenderContext.class);
        ViewContainer container = mock(ViewContainer.class);
        when(context.getContainer()).thenReturn(container);

        ItemComponent component = mock(ItemComponent.class);
        when(component.getRenderHandler()).thenReturn($ -> {
            /* do nothing */
        });

        when(context.getComponents()).thenReturn(Collections.singletonList(component));
        when(root.getContexts()).thenReturn(Collections.singleton(context));

        pipeline.execute(StandardPipelinePhases.UPDATE, context);

        verify(component, never()).clear(eq(context));
        verify(component, times(1)).updated(any());
        verify(component, times(1)).render(any());
    }

    @Test
    void neverRenderIfItemDoNotHaveRenderHandler() {
        Pipeline<IFContext> pipeline = new Pipeline<>(StandardPipelinePhases.UPDATE);
        pipeline.intercept(StandardPipelinePhases.UPDATE, new UpdateInterceptor());

        RootView root = createRootMock();
        IFContext context = createContextMock(root, IFRenderContext.class);
        ViewContainer container = mock(ViewContainer.class);
        when(context.getContainer()).thenReturn(container);

        ItemComponent component = mock(ItemComponent.class);
        when(component.getRenderHandler()).thenReturn(null);

        when(context.getComponents()).thenReturn(Collections.singletonList(component));
        when(root.getContexts()).thenReturn(Collections.singleton(context));

        pipeline.execute(StandardPipelinePhases.UPDATE, context);

        verify(component, never()).clear(eq(context));
        verify(component, never()).updated(any());
        verify(component, never()).render(any());
    }

    @Test
    void skipRenderIfContextWasCancelledOnUpdateHandler() {
        Pipeline<IFContext> pipeline = new Pipeline<>(StandardPipelinePhases.UPDATE);
        pipeline.intercept(StandardPipelinePhases.UPDATE, new UpdateInterceptor());

        RootView root = mock(RootView.class);
        ElementFactory elementFactory = mock(ElementFactory.class);

        IFSlotRenderContext cancelledContext = mock(IFSlotRenderContext.class);
        when(cancelledContext.isCancelled()).thenReturn(true);
        when(elementFactory.createSlotContext(anyInt(), any(), any(), any(), any(), eq(IFSlotRenderContext.class)))
                .thenReturn(cancelledContext);
        when(root.getElementFactory()).thenReturn(elementFactory);

        IFContext context = createContextMock(root, IFRenderContext.class);
        ViewContainer container = mock(ViewContainer.class);
        when(context.getContainer()).thenReturn(container);

        Component component = mock(Component.class);
        when(context.getComponents()).thenReturn(Collections.singletonList(component));
        when(root.getContexts()).thenReturn(Collections.singleton(context));

        pipeline.execute(StandardPipelinePhases.UPDATE, context);

        verify(component, never()).clear(eq(context));
        verify(component, times(1)).updated(any());
        verify(component, never()).render(any());
    }
}
