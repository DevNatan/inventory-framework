package me.devnatan.inventoryframework.internal.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.devnatan.inventoryframework.event.Event;
import me.devnatan.inventoryframework.event.EventBus;
import me.devnatan.inventoryframework.event.EventListener;
import me.devnatan.inventoryframework.event.EventSubscription;
import org.jetbrains.annotations.NotNull;

// TODO internalize this when class usage call-site become internal in the same module
public final class EventBusImpl implements EventBus {

    private final Map<Class<?>, List<EventSubscription>> byType = new HashMap<>();
    private final Map<String, List<EventSubscription>> byKey = new HashMap<>();

    @Override
    public void emit(@NotNull String event, Object value) {
        emit0(event, value);
    }

    @Override
    public <T extends Event> void emit(@NotNull T event, Object value) {
        emit0(event, value);
    }

    @Override
    public synchronized @NotNull EventSubscription listen(@NotNull Class<?> event, @NotNull EventListener<?> listener) {
        final EventSubscription sub = new EventSubscriptionImpl(listener);
        byType.computeIfAbsent(event, $ -> new ArrayList<>()).add(sub);
        return sub;
    }

    @Override
    public synchronized @NotNull EventSubscription listen(@NotNull String event, @NotNull EventListener<?> listener) {
        final EventSubscription sub = new EventSubscriptionImpl(listener);
        byKey.computeIfAbsent(event, $ -> new ArrayList<>()).add(sub);
        return sub;
    }

    private void emit0(Object event, Object value) {
        if (event.getClass().isPrimitive())
            throw new IllegalArgumentException("Primitive values cannot be used as event, use String instead.");

        if (event instanceof String) {
            emitToKeyed((String) event, value);
            return;
        }

        emitToTyped(event);
    }

    @SuppressWarnings("unchecked")
    private void emitToKeyed(String event, Object value) {
        final List<EventSubscription> subscriptions;
        synchronized (byKey) {
            subscriptions = byKey.get(event);
            if (subscriptions == null) return;
        }

        final Iterator<EventSubscription> iterator = subscriptions.iterator();
        while (iterator.hasNext()) {
            final EventSubscriptionImpl sub = (EventSubscriptionImpl) iterator.next();
            if (!sub.active) {
                iterator.remove();
                continue;
            }

            ((EventListener<Object>) sub.listener).call(value);
        }
    }

    @SuppressWarnings("unchecked")
    private void emitToTyped(Object event) {
        final List<EventSubscription> subscriptions;
        synchronized (byType) {
            subscriptions = deepSubscriptions(event.getClass());

            if (subscriptions == null) return;
        }

        final Iterator<EventSubscription> iterator = subscriptions.iterator();
        while (iterator.hasNext()) {
            final EventSubscriptionImpl sub = (EventSubscriptionImpl) iterator.next();
            if (!sub.active) {
                iterator.remove();
                continue;
            }

            ((EventListener<Object>) sub.listener).call(event);
        }
    }

    private List<EventSubscription> deepSubscriptions(Class<?> clazz) {
        List<EventSubscription> subs = byType.get(clazz);

        if (subs == null) {
            final Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && !superClass.equals(Object.class)) return deepSubscriptions(superClass);
        }

        return subs;
    }
}
