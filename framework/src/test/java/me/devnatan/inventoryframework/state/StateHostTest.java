package me.devnatan.inventoryframework.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StateHostTest {

    private final StateValueFactory stateValueFactory = new StateValueFactory();

    @Test
    void uninitializedState() {
        DefaultStateHost host = new DefaultStateHost();
        StateValue value = new ImmutableValue("abc");
        StateImpl<?> state = (StateImpl<?>) stateValueFactory.createState($ -> value);

        assertThrows(UninitializedStateException.class, () -> host.get(state.getId()));
    }

    @Test
    void getImmutableValue() {
        DefaultStateHost host = new DefaultStateHost();
        StateValue value = new ImmutableValue("abc");
        StateImpl<?> state = (StateImpl<?>) stateValueFactory.createState($ -> value);
        host.init(state.getId(), value);

        assertEquals("abc", host.get(state.getId()));
    }

    @Test
    void getComputedValue() {
        DefaultStateHost host = new DefaultStateHost();
        StateValue value = new ComputedValue(ThreadLocalRandom.current()::nextInt);
        StateImpl<?> state = (StateImpl<?>) stateValueFactory.createState($ -> value);
        host.init(state.getId(), value);

        int first = (int) host.get(state.getId());
        int second = (int) host.get(state.getId());

        Assertions.assertNotEquals(first, second);
    }

    @Test
    void getLazyValue() {
        DefaultStateHost host = new DefaultStateHost();
        StateValue value = new LazyValue(ThreadLocalRandom.current()::nextInt);
        StateImpl<?> state = (StateImpl<?>) stateValueFactory.createState($ -> value);
        host.init(state.getId(), value);

        int initial = (int) host.get(state.getId());
        int last = (int) host.get(state.getId());

        assertEquals(initial, last);
    }

    @Test
    void getMutableValue() {
        DefaultStateHost host = new DefaultStateHost();
        StateValue value = new MutableValue("test 1");
        StateImpl<?> state = (StateImpl<?>) stateValueFactory.createState($ -> value);
        host.init(state.getId(), value);

        assertEquals("test 1", host.get(state.getId()));
    }

    @Test
    void setImmutableValue() {
        DefaultStateHost host = new DefaultStateHost();
        StateValue value = new ImmutableValue("abc");
        StateImpl<?> state = (StateImpl<?>) stateValueFactory.createState($ -> value);
        host.init(state.getId(), value);

        assertThrows(IllegalStateModificationException.class, () -> host.set(state.getId(), "test"));
    }

    @Test
    void setComputedValue() {
        DefaultStateHost host = new DefaultStateHost();
        StateValue value = new ComputedValue(ThreadLocalRandom.current()::nextInt);
        StateImpl<?> state = (StateImpl<?>) stateValueFactory.createState($ -> value);
        host.init(state.getId(), value);

        assertThrows(IllegalStateModificationException.class, () -> host.set(state.getId(), "test"));
    }

    @Test
    void setLazyValue() {
        DefaultStateHost host = new DefaultStateHost();
        StateValue value = new LazyValue(ThreadLocalRandom.current()::nextInt);
        StateImpl<?> state = (StateImpl<?>) stateValueFactory.createState($ -> value);
        host.init(state.getId(), value);

        assertThrows(IllegalStateModificationException.class, () -> host.set(state.getId(), "test"));
    }

    @Test
    void setMutableValue() {
        DefaultStateHost host = new DefaultStateHost();
        StateValue value = new MutableValue("test 1");
        StateImpl<?> state = (StateImpl<?>) stateValueFactory.createState($ -> value);
        host.init(state.getId(), value);

        assertEquals("test 1", host.get(state.getId()));
        host.set(state.getId(), "test 2");

        assertEquals("test 2", host.get(state.getId()));
    }
}
