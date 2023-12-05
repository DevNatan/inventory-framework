package me.devnatan.inventoryframework.runtime;

import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.BukkitComponentHandle;
import me.devnatan.inventoryframework.component.BukkitItemComponentBuilder;
import me.devnatan.inventoryframework.component.ItemComponent;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;

class MyCustomComponent extends BukkitComponentHandle {

	private final State<String> someText = initialState();

	@Override
	public void render(@NotNull IFComponentRenderContext context) {
	}
}

class MyCustomComponentBuilder extends BukkitItemComponentBuilder<MyCustomComponentBuilder> {

	@Override
	public ItemComponent build(VirtualView root) {
		return new MyCustomComponent();
	}
}