package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.function.UnaryOperator;

public class TestViewFrame implements PlatformViewFrame<Void, Void, TestViewFrame> {

	@Getter
	@Setter
	private ViewErrorHandler errorHandler;

	@Override
	public @NotNull Void getPlatform() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Feature<?, ?>> getInstalledFeatures() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <C, R> @NotNull R install(@NotNull Feature<C, R> feature, @NotNull UnaryOperator<C> configure) {
		throw new UnsupportedOperationException();
	}

	@Override
	public TestViewFrame with(@NotNull AbstractView... views) {
		throw new UnsupportedOperationException();
	}

	@Override
	public TestViewFrame remove(@NotNull AbstractView... views) {
		return null;
	}

	@Override
	public TestViewFrame register() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void unregister() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRegistered() {
		throw new UnsupportedOperationException();
	}

	@Override
	public @NotNull Void getOwner() {
		throw new UnsupportedOperationException();
	}

	@Override
	public @NotNull ViewComponentFactory getFactory() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <R extends AbstractView> R open(@NotNull Class<R> viewClass, @NotNull Void viewer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <R extends AbstractView> R open(@NotNull Class<R> viewClass, @NotNull Void viewer, @NotNull Map<String, Object> data) {
		throw new UnsupportedOperationException();
	}

}
