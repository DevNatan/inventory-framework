package me.saiintbrisson.minecraft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class ContextDataInheritanceTest {

    @Test
    void shouldSlotContextInheritDataFromParent() {
        String key = "inherited";
        Map<String, Object> data = new HashMap<>();
        data.put(key, "value");

        ViewContext parentContext = mock(BaseViewContext.class);
        when(parentContext.getData()).thenReturn(data);

        AbstractViewSlotContext slotContext =
                new AbstractViewSlotContext(new ViewItem(), parentContext, parentContext.getContainer()) {
                    @Override
                    public int getSlot() {
                        return 0;
                    }
                };
        assertEquals(
                parentContext.get(key),
                (String) slotContext.get(key),
                "Slot context data must be inherited from parent");
    }

    @Test
    void shouldSlotContextPropagateDataToParent() {
        String key = "inherited";
        ViewContext parentContext = mock(BaseViewContext.class);
        AbstractViewSlotContext slotContext =
                new AbstractViewSlotContext(new ViewItem(), parentContext, parentContext.getContainer()) {
                    @Override
                    public int getSlot() {
                        return 0;
                    }
                };
        slotContext.set(key, "value");
        assertEquals(
                slotContext.get(key),
                (String) parentContext.get(key),
                "Slot context data must be propagated to parent");
    }
}
