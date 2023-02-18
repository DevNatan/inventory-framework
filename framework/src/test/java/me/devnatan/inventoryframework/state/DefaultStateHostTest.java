package me.devnatan.inventoryframework.state;

import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultStateHostTest {

    private final StateFactory stateFactory = new StateFactory();

    @Test
    void getImmutableValue() {
        DefaultStateHost host = new DefaultStateHost();
        InternalStateValue value = new ImmutableValue("abc");
        StateImpl<?> state = (StateImpl<?>) stateFactory.createState(value);

        Assertions.assertEquals("abc", host.get(state.getId(), state, value));
    }

    @Test
    void getComputedValue() {
        DefaultStateHost host = new DefaultStateHost();
        InternalStateValue value = new ComputedValue(ThreadLocalRandom.current()::nextInt);
        StateImpl<?> state = (StateImpl<?>) stateFactory.createState(value);

        int first = (int) host.get(state.getId(), state, value);
        int second = (int) host.get(state.getId(), state, value);

        Assertions.assertNotEquals(first, second);
    }

    @Test
    void getLazyValue() {
        DefaultStateHost host = new DefaultStateHost();
        InternalStateValue value = new LazyValue(ThreadLocalRandom.current()::nextInt);
        StateImpl<?> state = (StateImpl<?>) stateFactory.createState(value);

        int initial = (int) host.get(state.getId(), state, value);
        int last = (int) host.get(state.getId(), state, value);

        Assertions.assertEquals(initial, last);
    }

    @Test
    void getMutableValue() {
        DefaultStateHost host = new DefaultStateHost();
        InternalStateValue value = new MutableValue("test 1");
        StateImpl<?> state = (StateImpl<?>) stateFactory.createState(value);

        Assertions.assertEquals("test 1", host.get(state.getId(), state, value));
    }

    @Test
    void setImmutableValue() {
        DefaultStateHost host = new DefaultStateHost();
        InternalStateValue value = new ImmutableValue("abc");
        StateImpl<?> state = (StateImpl<?>) stateFactory.createState(value);

        Assertions.assertThrows(IllegalStateModificationException.class, () -> host.set(state.getId(), value, "test"));
    }

    @Test
    void setComputedValue() {
        DefaultStateHost host = new DefaultStateHost();
        InternalStateValue value = new ComputedValue(ThreadLocalRandom.current()::nextInt);
        StateImpl<?> state = (StateImpl<?>) stateFactory.createState(value);

        Assertions.assertThrows(IllegalStateModificationException.class, () -> host.set(state.getId(), value, "test"));
    }

    @Test
    void setLazyValue() {
        DefaultStateHost host = new DefaultStateHost();
        InternalStateValue value = new LazyValue(ThreadLocalRandom.current()::nextInt);
        StateImpl<?> state = (StateImpl<?>) stateFactory.createState(value);

        Assertions.assertThrows(IllegalStateModificationException.class, () -> host.set(state.getId(), value, "test"));
    }

    @Test
    void setMutableValue() {
        DefaultStateHost host = new DefaultStateHost();
        InternalStateValue value = new MutableValue("test 1");
        StateImpl<?> state = (StateImpl<?>) stateFactory.createState(value);

        Assertions.assertEquals("test 1", host.get(state.getId(), state, value));
        host.set(state.getId(), value, "test 2");

        Assertions.assertEquals("test 2", host.get(state.getId(), state, value));
    }
}
