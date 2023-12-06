package test;

import me.devnatan.inventoryframework.component.BukkitComponentBuilder;
import me.devnatan.inventoryframework.component.BukkitComponentHandle;
import me.devnatan.inventoryframework.context.ComponentRenderContext;

public class TestComponent extends BukkitComponentHandle<TestComponent.Builder> {

	@Override
	public TestComponent.Builder builder() {
		return new Builder();
	}

	@Override
	protected void rendered(ComponentRenderContext context) {

	}

	public static class Builder extends BukkitComponentBuilder<Builder> {

		public Builder something() {
			return this;
		}
	}
}
