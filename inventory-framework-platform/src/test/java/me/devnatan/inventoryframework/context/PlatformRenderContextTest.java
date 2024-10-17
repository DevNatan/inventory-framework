package me.devnatan.inventoryframework.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.ComponentFactory;
import me.devnatan.inventoryframework.component.ItemComponentBuilder;
import me.devnatan.inventoryframework.component.TestItemComponentBuilder;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PlatformRenderContextTest {

    @Test
    void emptyAvailableSlot() {
        ItemComponentBuilder itemBuilder = new TestItemComponentBuilder();
        PlatformRenderContext context =
                new PlatformRenderContext(
                        UUID.randomUUID(),
                        mock(PlatformView.class),
                        mock(ViewConfig.class),
                        mock(ViewContainer.class),
                        mock(Map.class),
                        mock(Viewer.class),
                        null) {
                    @Override
                    public @NotNull PlatformView getRoot() {
                        return root;
                    }

                    @Override
                    protected ItemComponentBuilder createBuilder() {
                        return itemBuilder;
                    }
                };

        context.availableSlot();

        List<BiFunction<Integer, Integer, ComponentFactory>> factory = context.getAvailableSlotFactories();
        BiFunction<Integer, Integer, ComponentFactory> first = factory.get(0);
        ComponentFactory value = first.apply(0, 0);
        assertEquals(itemBuilder, value);
    }
}
