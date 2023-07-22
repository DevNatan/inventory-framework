package me.devnatan.inventoryframework.feature;

import java.util.function.UnaryOperator;
import me.devnatan.inventoryframework.IFViewFrame;
import org.jetbrains.annotations.NotNull;

/**
 * Layouts feature implementation.
 *
 * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/Layouts">Layouts on Wiki</a>
 */
@SuppressWarnings("unused")
public final class LayoutsFeature implements Feature<Void, Void, IFViewFrame<?>> {

    private final FeatureDescriptor descriptor = new FeatureDescriptor(Keys.LAYOUTS, "Layouts", "3.0.0");
    public static final Feature<Void, Void, IFViewFrame<?>> Layouts = new LayoutsFeature();

    private LayoutsFeature() {}

    @Override
    public @NotNull FeatureDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public @NotNull Void install(IFViewFrame<?> framework, UnaryOperator<Void> configure) {
        return null;
    }

    @Override
    public void uninstall(IFViewFrame<?> framework) {}
}
