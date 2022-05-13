package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

public interface ViewContainerFactory {

	@NotNull
	ViewContainer createContainer(@NotNull final View view, final int size, final String title);

}
