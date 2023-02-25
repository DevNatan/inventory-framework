package me.devnatan.inventoryframework;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import me.devnatan.inventoryframework.context.IFConfinedContext;
import me.devnatan.inventoryframework.internal.MockElementFactory;

public class TestUtils {

    public static RootView createRootMock() {
        RootView root = mock(RootView.class);
        when(root.getElementFactory()).thenReturn(new MockElementFactory());
        return root;
    }

    public static IFConfinedContext createContextMock(RootView root) {
        IFConfinedContext context = mock(IFConfinedContext.class);
        when(context.getRoot()).thenReturn(root);
        return context;
    }
}
