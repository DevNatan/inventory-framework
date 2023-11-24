package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.component.BukkitPlatformComponent;
import me.devnatan.inventoryframework.component.BukkitItemComponentBuilder;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
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
	public void render(@NotNull IFComponentRenderContext context) {

	}

	@Override
	public void updated(@NotNull IFComponentUpdateContext context) {

	}

	@Override
	public void cleared(@NotNull IFRenderContext context) {

	}

	public static class Builder extends BukkitItemComponentBuilder<Builder> {

		@Override
		public Component build(VirtualView root) {
			return new TestComponent();
		}
	}
}
