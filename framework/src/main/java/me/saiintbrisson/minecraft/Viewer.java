package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

interface Viewer {

	void open(@NotNull final ViewContainer container);

	void close();

}