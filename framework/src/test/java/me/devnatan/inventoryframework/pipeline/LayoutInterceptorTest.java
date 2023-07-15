package me.devnatan.inventoryframework.pipeline;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.exception.InvalidLayoutException;
import org.junit.jupiter.api.Test;

public class LayoutInterceptorTest {

    @Test
    void invalidLayoutLengthForContainer() {
        Pipeline<VirtualView> pipeline = new Pipeline<>(StandardPipelinePhases.FIRST_RENDER);
        pipeline.intercept(StandardPipelinePhases.FIRST_RENDER, new LayoutRenderInterceptor());

        ViewConfig config = mock(ViewConfig.class);
        String[] layout = new String[] {"XXXXXXX" /* rows count = 1 */};
        when(config.getLayout()).thenReturn(layout);

        IFRenderContext context = mock(IFRenderContext.class);
        when(context.getConfig()).thenReturn(config);

        ViewContainer container = mock(ViewContainer.class);
        when(container.getRowsCount()).thenReturn(layout.length + 1 /* rows count = 2 */);
        when(context.getContainer()).thenReturn(container);

        Throwable throwable = assertThrows(
                InvalidLayoutException.class, () -> pipeline.execute(StandardPipelinePhases.FIRST_RENDER, context));

        assertEquals(
                format(
                        "Layout length (%d) must respect the rows count of the container (%d).",
                        layout.length, container.getRowsCount()),
                throwable.getMessage());
    }

    @Test
    void invalidLayoutLengthForLayer() {
        Pipeline<VirtualView> pipeline = new Pipeline<>(StandardPipelinePhases.FIRST_RENDER);
        pipeline.intercept(StandardPipelinePhases.FIRST_RENDER, new LayoutRenderInterceptor());

        ViewConfig config = mock(ViewConfig.class);

        String[] layout = new String[] {"XXX" /* columns count = 3 */};
        when(config.getLayout()).thenReturn(layout);

        IFRenderContext context = mock(IFRenderContext.class);
        when(context.getConfig()).thenReturn(config);

        ViewContainer container = mock(ViewContainer.class);
        when(container.getRowsCount()).thenReturn(layout.length);
        when(container.getColumnsCount()).thenReturn(9 /* columns count = 9 */);
        when(context.getContainer()).thenReturn(container);

        Throwable throwable = assertThrows(
                InvalidLayoutException.class, () -> pipeline.execute(StandardPipelinePhases.FIRST_RENDER, context));

        assertEquals(
                format(
                        "Layout layer length located at %d must respect the columns count of the container (given: %d, expect: %d).",
                        0, layout[0].length(), container.getColumnsCount()),
                throwable.getMessage());
    }
}
