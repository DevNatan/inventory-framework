package me.saiintbrisson.minecraft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.context.IFContext;
import org.junit.jupiter.api.Test;

public class ContextDataInheritanceTest {

    @Test
    void shouldSlotContextInheritDataFromParent() {
        String key = "inherited";
        Map<String, Object> data = new HashMap<>();
        data.put(key, "value");

        IFContext parentContext = mock(BaseViewContext.class);
        when(parentContext.getData()).thenReturn(data);

        AbstractViewSlotContext slotContext =
                new AbstractViewSlotContext(0, new IFItem(), parentContext, parentContext.getContainer()) {};
        assertEquals(
                parentContext.get(key),
                (String) slotContext.get(key),
                "Slot context data must be inherited from parent");
    }

    @Test
    void shouldSlotContextPropagateDataToParent() {
        String key = "inherited";
        IFContext parentContext = mock(BaseViewContext.class);
        AbstractViewSlotContext slotContext =
                new AbstractViewSlotContext(0, new IFItem(), parentContext, parentContext.getContainer()) {};
        slotContext.set(key, "value");
        assertEquals(
                slotContext.get(key),
                (String) parentContext.get(key),
                "Slot context data must be propagated to parent");
    }
}
