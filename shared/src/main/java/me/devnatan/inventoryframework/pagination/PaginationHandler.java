package me.devnatan.inventoryframework.pagination;

import me.saiintbrisson.minecraft.ViewItem;

@FunctionalInterface
public interface PaginationHandler<C, T> {

    void render(C context, ViewItem item, T value);
}
