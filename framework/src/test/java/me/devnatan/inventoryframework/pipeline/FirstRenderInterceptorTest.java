package me.devnatan.inventoryframework.pipeline;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.FakeComponent;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.internal.MockElementFactory;
import org.junit.jupiter.api.Test;

// TODO missing component builders registration for IFRenderContext test
public class FirstRenderInterceptorTest {

    @Test
    void renderComponents() {
        Pipeline<VirtualView> pipeline = new Pipeline<>(StandardPipelinePhases.FIRST_RENDER);
        pipeline.intercept(StandardPipelinePhases.FIRST_RENDER, new FirstRenderInterceptor());

        RootView root = mock(RootView.class);
        when(root.getElementFactory()).thenReturn(new MockElementFactory());

        IFRenderContext context = mock(IFRenderContext.class);
        when(context.getRoot()).thenReturn(root);

        ViewContainer container = mock(ViewContainer.class);
        when(context.getContainer()).thenReturn(container);

        FakeComponent component = new FakeComponent(root, 0);
        when(context.getComponents()).thenReturn(Collections.singletonList(component));
        when(root.getContexts()).thenReturn(Collections.singleton(context));

        pipeline.execute(StandardPipelinePhases.FIRST_RENDER, context);

        verify(container, times(1)).renderItem(eq(component.getPosition()), eq(component.item));
    }
}
