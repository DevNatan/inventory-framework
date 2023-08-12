package me.devnatan.inventoryframework.state;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StateValueHostTest {

    @Test
    void getImmutableValue() {
        StateValueHost host = new DefaultStateValueHost();
        State<?> state = new BaseState<>(0, ($, valueState) -> new ImmutableValue(valueState, "abc"));

        assertEquals("abc", host.getState(state));
    }

    //    @Test
    //    void getComputedValue() {
    //        DefaultStateValueHost host = new DefaultStateValueHost();
    //        StateValue value = new ComputedValue(ThreadLocalRandom.current()::nextInt);
    //        StateImpl<?> state = (StateImpl<?>) stateValueFactory.createState($ -> value);
    //        host.init(state.getId(), value);
    //
    //        int first = (int) host.get(state.getId());
    //        int second = (int) host.get(state.getId());
    //
    //        Assertions.assertNotEquals(first, second);
    //    }
    //
    //    @Test
    //    void getLazyValue() {
    //        DefaultStateValueHost host = new DefaultStateValueHost();
    //        StateValue value = new LazyValue(ThreadLocalRandom.current()::nextInt);
    //        StateImpl<?> state = (StateImpl<?>) stateValueFactory.createState($ -> value);
    //        host.init(state.getId(), value);
    //
    //        int initial = (int) host.get(state.getId());
    //        int last = (int) host.get(state.getId());
    //
    //        assertEquals(initial, last);
    //    }
    //
    //    @Test
    //    void getMutableValue() {
    //        DefaultStateValueHost host = new DefaultStateValueHost();
    //        StateValue value = new MutableValue("test 1");
    //        StateImpl<?> state = (StateImpl<?>) stateValueFactory.createState($ -> value);
    //        host.init(state.getId(), value);
    //
    //        assertEquals("test 1", host.get(state.getId()));
    //    }
    //
    //    @Test
    //    void setImmutableValue() {
    //        DefaultStateValueHost host = new DefaultStateValueHost();
    //        StateValue value = new ImmutableValue("abc");
    //        StateImpl<?> state = (StateImpl<?>) stateValueFactory.createState($ -> value);
    //        host.init(state.getId(), value);
    //
    //        assertThrows(IllegalStateModificationException.class, () -> host.set(state.getId(), "test"));
    //    }
    //
    //    @Test
    //    void setComputedValue() {
    //        DefaultStateValueHost host = new DefaultStateValueHost();
    //        StateValue value = new ComputedValue(ThreadLocalRandom.current()::nextInt);
    //        StateImpl<?> state = (StateImpl<?>) stateValueFactory.createState($ -> value);
    //        host.init(state.getId(), value);
    //
    //        assertThrows(IllegalStateModificationException.class, () -> host.set(state.getId(), "test"));
    //    }
    //
    //    @Test
    //    void setLazyValue() {
    //        DefaultStateValueHost host = new DefaultStateValueHost();
    //        StateValue value = new LazyValue(ThreadLocalRandom.current()::nextInt);
    //        StateImpl<?> state = (StateImpl<?>) stateValueFactory.createState($ -> value);
    //        host.init(state.getId(), value);
    //
    //        assertThrows(IllegalStateModificationException.class, () -> host.set(state.getId(), "test"));
    //    }
    //
    //    @Test
    //    void setMutableValue() {
    //        DefaultStateValueHost host = new DefaultStateValueHost();
    //        StateValue value = new MutableValue("test 1");
    //        StateImpl<?> state = (StateImpl<?>) stateValueFactory.createState($ -> value);
    //        host.init(state.getId(), value);
    //
    //        assertEquals("test 1", host.get(state.getId()));
    //        host.set(state.getId(), "test 2");
    //
    //        assertEquals("test 2", host.get(state.getId()));
    //    }
}
