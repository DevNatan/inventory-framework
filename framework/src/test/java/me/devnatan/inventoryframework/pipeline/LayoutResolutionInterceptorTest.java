package me.devnatan.inventoryframework.pipeline;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.context.IFConfinedContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.exception.InvalidLayoutException;
import me.devnatan.inventoryframework.internal.MockElementFactory;
import org.junit.jupiter.api.Test;

public class LayoutResolutionInterceptorTest {

    @Test
    void invalidLayoutLengthForContainer() {
        Pipeline<IFContext> pipeline = new Pipeline<>(StandardPipelinePhases.LAYOUT_RESOLUTION);
        pipeline.intercept(StandardPipelinePhases.LAYOUT_RESOLUTION, new LayoutResolutionInterceptor());

        RootView root = mock(RootView.class);
        ViewConfig config = mock(ViewConfig.class);

        String[] layout = new String[] {"XXXXXXX"};
        when(config.getLayout()).thenReturn(layout);
        when(root.getConfig()).thenReturn(config);
        when(root.getElementFactory()).thenReturn(new MockElementFactory());

        IFConfinedContext context = mock(IFConfinedContext.class);
        when(context.getConfig()).thenReturn(config);
        when(context.getRoot()).thenReturn(root);

        ViewContainer container = mock(ViewContainer.class);
        when(container.getRowsCount()).thenReturn(layout.length + 1);
        when(context.getContainer()).thenReturn(container);

        Throwable throwable = assertThrows(
                InvalidLayoutException.class,
                () -> pipeline.execute(StandardPipelinePhases.LAYOUT_RESOLUTION, context));

        assertEquals(
                format(
                        "Layout length (%d) must respect the rows count of the container (%d).",
                        layout.length, container.getRowsCount()),
                throwable.getMessage());
    }

    @Test
    void invalidLayoutLengthForLayer() {
        Pipeline<IFContext> pipeline = new Pipeline<>(StandardPipelinePhases.LAYOUT_RESOLUTION);
        pipeline.intercept(StandardPipelinePhases.LAYOUT_RESOLUTION, new LayoutResolutionInterceptor());

        RootView root = mock(RootView.class);
        ViewConfig config = mock(ViewConfig.class);

        String[] layout = new String[] {"XXX"};
        when(config.getLayout()).thenReturn(layout);
        when(root.getConfig()).thenReturn(config);
        when(root.getElementFactory()).thenReturn(new MockElementFactory());

        IFConfinedContext context = mock(IFConfinedContext.class);
        when(context.getConfig()).thenReturn(config);
        when(context.getRoot()).thenReturn(root);

        ViewContainer container = mock(ViewContainer.class);
        when(container.getRowsCount()).thenReturn(layout.length);
        when(container.getColumnsCount()).thenReturn(9);
        when(context.getContainer()).thenReturn(container);

        Throwable throwable = assertThrows(
                InvalidLayoutException.class,
                () -> pipeline.execute(StandardPipelinePhases.LAYOUT_RESOLUTION, context));

        assertEquals(
                format(
                        "Layout layer length located at %d must respect the columns count of the container (given: %d, expect: %d).",
                        0, layout[0].length(), container.getColumnsCount()),
                throwable.getMessage());
    }
}
