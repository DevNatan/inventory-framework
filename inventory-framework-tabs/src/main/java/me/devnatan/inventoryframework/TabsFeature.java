package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.feature.Feature;
import org.jetbrains.annotations.NotNull;

import java.util.function.UnaryOperator;

@SuppressWarnings("ClassEscapesDefinedScope")
public final class TabsFeature implements Feature<Void, Void, IFViewFrame<?, ?>> {

    public static final Feature<Void, Void, IFViewFrame<?, ?>> Tabs = new TabsFeature();

    private TabsFeature() {}

    @Override
    public @NotNull String name() {
        return "Tabs";
    }

	@Override
	public @NotNull Void install(IFViewFrame<?, ?> framework, UnaryOperator<Void> configure) {
		return null;
	}

	@Override
	public void uninstall(IFViewFrame<?, ?> framework) {

	}
}
