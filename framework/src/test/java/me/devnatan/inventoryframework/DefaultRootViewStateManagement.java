package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.context.BaseViewContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.state.ImmutableValue;
import me.devnatan.inventoryframework.state.InternalStateValue;
import me.devnatan.inventoryframework.state.State;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DefaultRootViewStateManagement {

    @Test
    void shouldChildInheritStates() {
        DefaultRootView root = new DefaultRootView();
        InternalStateValue value = new ImmutableValue("test");
        State<?> state = root.stateFactory.createState(value);
        IFContext context = new BaseViewContext(root, null);

        Assertions.assertEquals("test", state.get(context));
    }
}
