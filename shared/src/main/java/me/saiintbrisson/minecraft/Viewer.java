package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

public interface Viewer {

    void open(@NotNull final ViewContainer container);

    void close();
}
