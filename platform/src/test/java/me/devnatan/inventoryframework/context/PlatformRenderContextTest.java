package me.devnatan.inventoryframework.context;

import static me.devnatan.inventoryframework.TestUtils.createRootMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;

import java.util.function.BiFunction;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.ComponentFactory;
import me.devnatan.inventoryframework.component.ItemComponentBuilder;
import me.devnatan.inventoryframework.component.TestItemComponentBuilder;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PlatformRenderContextTest {

    @Test
    void emptyAvailableSlot() {
        ItemComponentBuilder itemBuilder = new TestItemComponentBuilder();
        PlatformRenderContext context =
                new PlatformRenderContext(
                        createRootMock(), mock(ViewContainer.class), mock(Viewer.class), mock(IFContext.class)) {
                    @Override
                    protected ItemComponentBuilder createBuilder() {
                        return itemBuilder;
                    }
                };

        context.availableSlot();

        BiFunction<Integer, Integer, ComponentFactory> factory = (BiFunction<Integer, Integer, ComponentFactory>)
                context.getAvailableSlotsFactories().get(0);

        ComponentFactory value = factory.apply(anyInt(), anyInt());
        assertEquals(itemBuilder, value);
    }
}
