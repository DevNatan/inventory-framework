package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
public interface ViewContextHistory {

	ViewContextStore getStore();

	void back();

	void back(@NotNull Viewer viewer);

	void advance();

	void advance(@NotNull Viewer viewer);

}
