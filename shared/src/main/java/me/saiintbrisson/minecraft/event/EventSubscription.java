package me.saiintbrisson.minecraft.event;

public final class EventSubscription {

    final EventListener<?> listener;

    /**
     * Becomes false when event bus unregister is called, used to prevent race conditions.
     * TODO double-check if we need `volatile` here
     */
    boolean active;

    EventSubscription(EventListener<?> listener) {
        this.listener = listener;
        active = true;
    }

    public void unregister() {
        this.active = false;
    }
}
