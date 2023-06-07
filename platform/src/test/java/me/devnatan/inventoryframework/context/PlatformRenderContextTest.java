package me.devnatan.inventoryframework.context;

import static me.devnatan.inventoryframework.TestUtils.createRootMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.UUID;
import java.util.function.BiFunction;
import me.devnatan.inventoryframework.ViewConfig;
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
                        UUID.randomUUID(),
                        createRootMock(),
                        mock(ViewContainer.class),
                        mock(Viewer.class),
                        mock(ViewConfig.class),
                        null) {
                    @Override
                    protected ItemComponentBuilder createBuilder() {
                        return itemBuilder;
                    }
                };

        context.availableSlot();

        BiFunction<Integer, Integer, ComponentFactory> factory = context.getAvailableSlotFactory();
        ComponentFactory value = factory.apply(0, 0);
        assertEquals(itemBuilder, value);
    }
}
