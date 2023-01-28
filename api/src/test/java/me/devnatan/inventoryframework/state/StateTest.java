package me.devnatan.inventoryframework.state;

import static org.junit.jupiter.api.Assertions.assertEquals;

import me.devnatan.inventoryframework.state.internal.DefaultStateHolder;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
public class StateTest {

    @Test
    void initialValueDefinition() {
        StateHolder owner = new DefaultStateHolder();
        StateValueHolder scopedValue = owner.createUnchecked(0);
        StateMarker<Integer> state = (StateMarker<Integer>) scopedValue.getState();

        assertEquals(0, state.get(owner));
    }
}
