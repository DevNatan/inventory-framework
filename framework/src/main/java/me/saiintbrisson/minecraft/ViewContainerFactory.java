package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

public interface ViewContainerFactory {

	@NotNull
	ViewContainer create(
		@NotNull View view,
		int size,
		String title
	);

}
