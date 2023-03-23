package me.devnatan.inventoryframework.pipeline;

import static me.devnatan.inventoryframework.TestUtils.createContextMock;
import static me.devnatan.inventoryframework.TestUtils.createRootMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentFactory;
import me.devnatan.inventoryframework.component.FakeComponent;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import org.junit.jupiter.api.Test;

public class AvailableSlotInterceptorTest {

    private static final String[] defaultLayout = {"         ", " OOOOOOO ", "         "};
    private static final List<Integer> defaultLayoutSlotRange =
            IntStream.rangeClosed(11, 17).boxed().collect(Collectors.toList());

    @Test
    void resolveFromLayoutMustBeEmptyIfNoCharacterAvailable() {
        RootView root = createRootMock();
        IFRenderContext context = createContextMock(root, IFRenderContext.class);
        when(context.getConfig()).thenReturn(createLayoutConfig(defaultLayout));

        assertTrue(new AvailableSlotInterceptor().resolveFromLayoutSlot(context).isEmpty());
    }

    @Test
    void resolveFromLayoutMustBeEmptyIfPositionsIsNull() {
        RootView root = createRootMock();
        IFRenderContext context = createContextMock(root, IFRenderContext.class);
        when(context.getConfig()).thenReturn(createLayoutConfig(defaultLayout));
        when(context.getLayoutSlots())
                .thenReturn(Collections.singletonList(
                        new LayoutSlot(LayoutSlot.FILLED_RESERVED_CHAR, $ -> mock(ComponentFactory.class))));

        assertTrue(new AvailableSlotInterceptor().resolveFromLayoutSlot(context).isEmpty());
    }

    @Test
    void resolveFromLayoutMustBeEmptyIfPositionsIsEmpty() {
        RootView root = createRootMock();
        IFRenderContext context = createContextMock(root, IFRenderContext.class);
        when(context.getConfig()).thenReturn(createLayoutConfig(defaultLayout));

        LayoutSlot layoutSlot = new LayoutSlot(LayoutSlot.FILLED_RESERVED_CHAR, $ -> mock(ComponentFactory.class));
        layoutSlot.updatePositions(Collections.emptyList());

        when(context.getLayoutSlots()).thenReturn(Collections.singletonList(layoutSlot));

        assertTrue(new AvailableSlotInterceptor().resolveFromLayoutSlot(context).isEmpty());
    }

    @Test
    void resolveFromLayoutSlot() {
        RootView root = createRootMock();
        ViewContainer container = mock(ViewContainer.class);
        when(container.hasItem(anyInt())).thenReturn(false);

        IFRenderContext context = createContextMock(root, IFRenderContext.class);
        when(context.getContainer()).thenReturn(container);
        when(context.getConfig()).thenReturn(createLayoutConfig(defaultLayout));

        BiFunction<Integer, Integer, ComponentFactory> availableSlotFactory = mock(BiFunction.class);
        ComponentFactory componentFactory = mock(ComponentFactory.class);
        Component component = new FakeComponent(root);

        when(componentFactory.create()).thenReturn(component);
        when(availableSlotFactory.apply(anyInt(), anyInt())).thenReturn(componentFactory);
        when(context.getAvailableSlotsFactories()).thenReturn(Collections.singletonList(availableSlotFactory));

        LayoutSlot layoutSlot = new LayoutSlot(LayoutSlot.FILLED_RESERVED_CHAR, $ -> componentFactory);
        layoutSlot.updatePositions(defaultLayoutSlotRange);
        when(context.getLayoutSlots()).thenReturn(Collections.singletonList(layoutSlot));

        List<ComponentFactory> resolved = new AvailableSlotInterceptor().resolveFromLayoutSlot(context);
        assertEquals(componentFactory, resolved.get(0));
        verify(availableSlotFactory).apply(eq(0), eq(defaultLayoutSlotRange.get(0)));
    }

    @Test
    void interceptFromLayoutSlot() {
        RootView root = createRootMock();
        ViewContainer container = mock(ViewContainer.class);
        when(container.hasItem(anyInt())).thenReturn(false);

        IFRenderContext context = createContextMock(root, IFRenderContext.class);
        when(context.getContainer()).thenReturn(container);
        when(context.getConfig()).thenReturn(createLayoutConfig(defaultLayout));

        ComponentFactory componentFactory = mock(ComponentFactory.class);
        Component component = new FakeComponent(root);
        when(componentFactory.create()).thenReturn(component);

        BiFunction<Integer, Integer, ComponentFactory> availableSlotFactory = mock(BiFunction.class);
        when(availableSlotFactory.apply(anyInt(), anyInt())).thenReturn(componentFactory);
        when(context.getAvailableSlotsFactories()).thenReturn(Collections.singletonList(availableSlotFactory));

        LayoutSlot layoutSlot = new LayoutSlot(LayoutSlot.FILLED_RESERVED_CHAR, $ -> componentFactory);
        layoutSlot.updatePositions(defaultLayoutSlotRange);
        when(context.getLayoutSlots()).thenReturn(Collections.singletonList(layoutSlot));

        new AvailableSlotInterceptor().intercept(mock(PipelineContext.class), context);

        verify(availableSlotFactory).apply(eq(0), eq(defaultLayoutSlotRange.get(0)));
        verify(context).addComponent(eq(component));
    }

    private ViewConfig createLayoutConfig(String... layout) {
        return new ViewConfigBuilder()
                .type(ViewType.CHEST)
                .size(3)
                .layout(layout)
                .build();
    }
}
