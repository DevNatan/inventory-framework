package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

public interface Pagination<T> extends AindaNaoTenhoNomePraIsso {

    int count(@NotNull StateHolder holder);

    boolean canBack(@NotNull StateHolder holder);

    boolean canAdvance(@NotNull StateHolder holder);

    void back(@NotNull StateHolder holder);

    void advance(@NotNull StateHolder holder);
}
