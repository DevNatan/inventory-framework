package me.saiintbrisson.minecraft.event;

import org.jetbrains.annotations.NotNull;

/**
 * EventBus is a pub/sub feature that simplifies communication between contexts and handlers.
 */
// TODO need documentation
public interface EventBus {

    // TODO provide overload
    void emit(@NotNull String event, Object value);

    // TODO provide overload
    <T extends Event> void emit(@NotNull T event, Object value);

    @NotNull
    EventSubscription listen(@NotNull Class<?> event, @NotNull EventListener<?> listener);

    @NotNull
    EventSubscription listen(@NotNull String event, @NotNull EventListener<?> listener);
}
