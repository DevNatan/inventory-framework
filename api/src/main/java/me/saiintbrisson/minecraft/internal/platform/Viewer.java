package me.saiintbrisson.minecraft.internal.platform;

import org.jetbrains.annotations.NotNull;

public interface Viewer {

    void open(@NotNull final ViewContainer container);

    void close();
}
