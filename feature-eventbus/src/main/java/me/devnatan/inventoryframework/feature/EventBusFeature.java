package me.devnatan.inventoryframework.feature;

import static me.devnatan.inventoryframework.feature.Feature.Keys.EVENT_BUS;

import java.util.function.UnaryOperator;
import me.devnatan.inventoryframework.IFViewFrame;
import org.jetbrains.annotations.NotNull;

/**
 * EventBus feature implementation.
 *
 * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/Event-Bus">Event Bus on Wiki</a>
 */
@SuppressWarnings("unused")
public final class EventBusFeature implements Feature<Void, Void, IFViewFrame<?>> {

    private final FeatureDescriptor descriptor = new FeatureDescriptor(EVENT_BUS, "Event Bus", "3.0.0");

    public static final Feature<Void, Void, IFViewFrame<?>> EventBus = new EventBusFeature();

    private EventBusFeature() {}

    @Override
    public @NotNull FeatureDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public @NotNull Void install(IFViewFrame<?> framework, UnaryOperator<Void> configure) {
        framework.checkNotRegisteredForFeatureInstall(getDescriptor().getName(), "EventBus");

        return null;
    }

    @Override
    public void uninstall(IFViewFrame<?> framework) {}
}
