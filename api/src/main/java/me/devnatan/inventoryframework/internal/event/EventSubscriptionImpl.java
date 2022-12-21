package me.devnatan.inventoryframework.internal.event;

import me.saiintbrisson.minecraft.event.EventListener;
import me.saiintbrisson.minecraft.event.EventSubscription;

final class EventSubscriptionImpl implements EventSubscription {

    final EventListener<?> listener;

    /**
     * Becomes false when event bus unregister is called, used to prevent race conditions.
     * TODO double-check if we need `volatile` here
     */
    boolean active;

    public EventSubscriptionImpl(EventListener<?> listener) {
        this.listener = listener;
        active = true;
    }

    @Override
    public void unregister() {
        this.active = false;
    }
}
