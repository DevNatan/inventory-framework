package me.devnatan.inventoryframework.logging;

import org.jetbrains.annotations.Nullable;

public interface Logger {

    @Nullable
    String getPrefix();

    void debug(String message);

    void warn(String message);

    void error(String message);
}
