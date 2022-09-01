package me.saiintbrisson.minecraft.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * EventBus is a pub/sub feature that simplifies communication between contexts and handlers.
 */
public final class EventBus {

	private final Map<Class<?>, List<EventSubscription>> byType = new HashMap<>();
	private final Map<String, List<EventSubscription>> byKey = new HashMap<>();

	public synchronized EventSubscription listen(Class<?> event, EventListener<?> listener) {
		EventSubscription sub = new EventSubscription(listener);
		byType.computeIfAbsent(event, $ -> new ArrayList<>()).add(sub);
		return sub;
	}

	public synchronized EventSubscription listen(String event, EventListener<?> listener) {
		EventSubscription sub = new EventSubscription(listener);
		byKey.computeIfAbsent(event, $ -> new ArrayList<>()).add(new EventSubscription(listener));
		return sub;
	}

	public void emit(Object event, Object value) {
		if (event.getClass().isPrimitive())
			throw new IllegalArgumentException(
				"Primitive values cannot be used as event, use String instead."
			);

		if (event instanceof String) {
			emitToKeyed((String) event, value);
			return;
		}

		emitToTyped(event);
	}

	private void emitToKeyed(String event, Object value) {
		final List<EventSubscription> subscriptions;
		synchronized (byKey) {
			subscriptions = byKey.get(event);
			if (subscriptions == null)
				return;
		}

		final Iterator<EventSubscription> iterator = subscriptions.iterator();
		while (iterator.hasNext()) {
			final EventSubscription sub = iterator.next();
			if (!sub.active) {
				iterator.remove();
				continue;
			}

			((EventListener<Object>) sub.listener).call(value);
		}
	}

	private void emitToTyped(Object event) {
		final List<EventSubscription> subscriptions;
		synchronized (byType) {
			subscriptions = deepSubscriptions(event.getClass());

			if (subscriptions == null)
				return;
		}

		final Iterator<EventSubscription> iterator = subscriptions.iterator();
		while (iterator.hasNext()) {
			final EventSubscription sub = iterator.next();
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
			if (superClass != null && !superClass.equals(Object.class))
				return deepSubscriptions(superClass);
		}

		return subs;

	}

}
