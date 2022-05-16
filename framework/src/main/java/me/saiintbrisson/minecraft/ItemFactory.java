package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

public interface ItemFactory<T> {

	@SuppressWarnings("ConstantConditions")
	default ViewItem item() {
		return item(null);
	}

	ViewItem item(@NotNull T stack);

}
