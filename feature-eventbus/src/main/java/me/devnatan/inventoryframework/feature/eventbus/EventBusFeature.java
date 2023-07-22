package me.devnatan.inventoryframework.feature.eventbus;

import java.util.function.UnaryOperator;
import me.devnatan.inventoryframework.IFViewFrame;
import me.devnatan.inventoryframework.feature.Feature;
import org.jetbrains.annotations.NotNull;

/**
 * EventBus feature implementation.
 *
 * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/Event-Bus">Event Bus on Wiki</a>
 */
@SuppressWarnings("unused")
public final class EventBusFeature implements Feature<Void, Void, IFViewFrame<?>> {

    public static final Feature<Void, Void, IFViewFrame<?>> EventBus = new EventBusFeature();

    private EventBusFeature() {}

    @Override
    public @NotNull String name() {
        return "Event Bus";
    }

    @Override
    public @NotNull Void install(IFViewFrame<?> framework, UnaryOperator<Void> configure) {
        framework.checkNotRegisteredForFeatureInstall(name(), "EventBus");

        return null;
    }

    @Override
    public void uninstall(IFViewFrame<?> framework) {}
}
