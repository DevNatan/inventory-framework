package me.saiintbrisson.minecraft.eventbus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import me.devnatan.inventoryframework.event.Event;
import me.devnatan.inventoryframework.event.EventBus;
import me.devnatan.inventoryframework.event.EventSubscription;
import me.devnatan.inventoryframework.internal.event.EventBusImpl;
import org.junit.jupiter.api.Test;

public class EventBusTest {

    @Test
    void keyedEmit() {
        EventBus eventBus = new EventBusImpl();
        AtomicInteger value = new AtomicInteger();
        int expectedValue = 9;
        eventBus.listen("test", eventValue -> {
            value.set((Integer) eventValue);
        });

        eventBus.emit("test", expectedValue);
        assertEquals(expectedValue, value.get());
    }

    @Test
    void keyedEmitNullValue() {
        EventBus eventBus = new EventBusImpl();
        AtomicBoolean called = new AtomicBoolean(false);
        eventBus.listen("test", $ -> {
            called.set(true);
        });

        eventBus.emit("test", null);
        assertTrue(called.get());
    }

    @Test
    void typedEmit() {
        class MyEvent implements Event {}

        EventBus eventBus = new EventBusImpl();
        AtomicBoolean called = new AtomicBoolean();
        eventBus.listen(MyEvent.class, event -> {
            called.set(true);
        });

        eventBus.emit(new MyEvent(), null);
        assertTrue(called.get());
    }

    @Test
    void typedEmitInvalidType() {
        class MyEvent1 implements Event {}
        class MyEvent2 implements Event {}

        EventBus eventBus = new EventBusImpl();
        AtomicBoolean called = new AtomicBoolean();
        eventBus.listen(MyEvent1.class, event -> {
            called.set(true);
        });

        eventBus.emit(new MyEvent2(), null);
        assertFalse(called.get());
    }

    @Test
    void typedEmitPolymorphism() {
        class MyEvent1 implements Event {}
        class MyEvent2 extends MyEvent1 {}

        EventBus eventBus = new EventBusImpl();
        AtomicBoolean called = new AtomicBoolean();
        eventBus.listen(MyEvent1.class, event -> {
            called.set(true);
        });

        eventBus.emit(new MyEvent2(), null);
        assertTrue(called.get());
    }

    @Test
    void typedEmitInversedPolymorphism() {
        class MyEvent1 implements Event {}
        class MyEvent2 extends MyEvent1 {}

        EventBus eventBus = new EventBusImpl();
        AtomicBoolean called = new AtomicBoolean();
        eventBus.listen(MyEvent2.class, event -> {
            called.set(true);
        });

        eventBus.emit(new MyEvent1(), null);
        assertFalse(called.get());
    }

    @Test
    void subscriptionUnregistration() {
        class MyEvent implements Event {}

        EventBus eventBus = new EventBusImpl();
        AtomicInteger calls = new AtomicInteger();
        EventSubscription sub = eventBus.listen(MyEvent.class, event -> calls.incrementAndGet());

        eventBus.emit(new MyEvent(), null);
        assertEquals(1, calls.get());

        sub.unregister();
        eventBus.emit(new MyEvent(), null);

        assertEquals(1, calls.get());
    }

    @Test
    void multipleSubscriptionsUnregistration() {
        class MyEvent implements Event {}

        EventBus eventBus = new EventBusImpl();
        AtomicBoolean sub1Call = new AtomicBoolean();
        AtomicBoolean sub2Call = new AtomicBoolean();
        eventBus.listen(MyEvent.class, event -> sub1Call.set(!sub1Call.get()));
        EventSubscription sub2 = eventBus.listen(MyEvent.class, event -> sub2Call.set(!sub2Call.get()));

        eventBus.emit(new MyEvent(), null);
        assertTrue(sub1Call.get());
        assertTrue(sub2Call.get());

        sub2.unregister();
        eventBus.emit(new MyEvent(), null);

        assertFalse(sub1Call.get());
        assertTrue(sub2Call.get());
    }
}
