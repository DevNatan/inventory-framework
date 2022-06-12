package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public abstract class ViewComponentFactory {

	private final Set<String> enabledFeatures = new HashSet<>();

	final void addFeature(@NotNull String feature) {
		enabledFeatures.add(feature);
	}

	final void removeFeature(@NotNull String feature) {
		enabledFeatures.remove(feature);
	}

	final boolean isFeatureEnabled(@NotNull String feature) {
		return enabledFeatures.contains(feature);
	}

	@NotNull
	public abstract AbstractView createView(
		int rows,
		String title,
		@NotNull ViewType type
	);

	public abstract void setupView(@NotNull AbstractView view);

	@NotNull
	public abstract ViewContainer createContainer(
		@NotNull final VirtualView view,
		final int size,
		final String title,
		final ViewType type
	);

	@NotNull
	public abstract Viewer createViewer(Object... parameters);

	@NotNull
	public abstract BaseViewContext createContext(
		@NotNull final AbstractView root,
		final ViewContainer container,
		final Class<? extends ViewContext> backingContext
	);

	public abstract Object createItem(
		@Nullable Object stack
	);

	public abstract boolean worksInCurrentPlatform();

}
