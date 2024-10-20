package me.devnatan.inventoryframework.pipeline;

import static me.devnatan.inventoryframework.TestUtils.createContextMock;
import static me.devnatan.inventoryframework.TestUtils.createRootMock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Collections;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.FakeComponent;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.MockRenderIFContext;
import me.devnatan.inventoryframework.state.State;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

// TODO missing component builders registration for IFRenderContext test
public class FirstRenderInterceptorTest {

    @Test
    void renderComponents() {
        FirstRenderInterceptor interceptor = new FirstRenderInterceptor();
        RootView root = createRootMock();
        IFRenderContext context = createContextMock(root, MockRenderIFContext.class);
        Mockito.doCallRealMethod().when(context).renderComponent(any());

        ViewContainer container = mock(ViewContainer.class);
        when(context.getContainer()).thenReturn(container);
        FakeComponent component = spy(new FakeComponent(root, 0));
        when(component.shouldRender(context)).thenReturn(true);
        when(context.getComponents()).thenReturn(Collections.singletonList(component));
        when(root.getInternalContexts()).thenReturn(Collections.singleton(context));

        interceptor.intercept(null, context);
        verify(container, times(1)).renderItem(eq(component.getPosition()), eq(component.item));
    }

    @Test
    void watchStates() {
        FirstRenderInterceptor interceptor = new FirstRenderInterceptor();
        RootView root = createRootMock();
        IFRenderContext context = createContextMock(root, IFRenderContext.class);

        ViewContainer container = mock(ViewContainer.class);
        when(context.getContainer()).thenReturn(container);

        Component component = mock(Component.class);
        State<?> state = mock(State.class);
        when(state.internalId()).thenReturn(4L);
        when(component.getWatchingStates()).thenReturn(Collections.singleton(state));
        when(context.getComponents()).thenReturn(Collections.singletonList(component));

        interceptor.intercept(null, context);
        verify(context).watchState(eq(4L), any());
    }
}
