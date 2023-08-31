package me.devnatan.inventoryframework;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import me.devnatan.inventoryframework.context.IFContext;

public class TestUtils {

    public static RootView createRootMock() {
        RootView root = mock(RootView.class);
        return root;
    }

    public static <T extends IFContext> T createContextMock(RootView root, Class<T> type) {
        T context = mock(type);
        when(context.getIndexedViewers()).thenReturn(new HashMap<>());
        when(context.getRoot()).thenReturn(root);
        return context;
    }
}
