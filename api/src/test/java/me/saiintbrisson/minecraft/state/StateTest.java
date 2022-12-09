package me.saiintbrisson.minecraft.state;

import static org.junit.jupiter.api.Assertions.assertEquals;

import me.saiintbrisson.minecraft.state.internal.DefaultStateOwner;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
public class StateTest {

    @Test
    void initialValueDefinition() {
        StateOwner owner = new DefaultStateOwner();
        StateValueHolder scopedValue = owner.createUnchecked(0);
        State<Integer> state = (State<Integer>) scopedValue.getState();

        assertEquals(0, state.get(owner));
    }
}
