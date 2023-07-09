package me.devnatan.inventoryframework.logging;

import org.jetbrains.annotations.Nullable;

public final class NoopLogger implements Logger {

    @Override
    public @Nullable String getPrefix() {
        return null;
    }

    @Override
    public void debug(String message) {}

    @Override
    public void warn(String message) {}

    @Override
    public void error(String message) {}
}
