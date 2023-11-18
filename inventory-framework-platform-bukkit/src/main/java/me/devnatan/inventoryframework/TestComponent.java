package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.component.BukkitPlatformComponent;
import me.devnatan.inventoryframework.component.BukkitItemComponentBuilder;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFContext;
import org.jetbrains.annotations.NotNull;

public class TestComponent extends BukkitPlatformComponent<TestComponent.Builder> {

	@Override
	public Builder createBuilder() {
		return null;
	}

	@Override
	public boolean isContainedWithin(int position) {
		return false;
	}

	@Override
	public void rendered(@NotNull IFComponentRenderContext context) {

	}

	@Override
	public void updated(@NotNull IFComponentUpdateContext context) {

	}

	@Override
	public void cleared(@NotNull IFContext context) {

	}

	public static class Builder extends BukkitItemComponentBuilder<Builder> {

		@Override
		public Component build(VirtualView root) {
			return new TestComponent();
		}
	}
}
