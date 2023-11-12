package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.context.Context;

public abstract class BukkitComponent<B extends ComponentBuilder<B>>
	extends PlatformComponent<Context, B, BukkitItemComponentBuilder> {
}