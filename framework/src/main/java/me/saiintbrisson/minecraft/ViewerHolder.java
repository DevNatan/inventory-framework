package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface ViewerHolder<P> {

	void open(@NotNull final Viewer viewer, @NotNull final Map<String, Object> data);

	void open(@NotNull final P viewer, @NotNull final Map<String, Object> data);

}
