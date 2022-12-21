package me.saiintbrisson.minecraft;

import lombok.ToString;
import me.saiintbrisson.minecraft.internal.platform.ViewContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ToString(callSuper = true)
final class PaginatedViewContextImpl<T> extends BasePaginatedViewContext<T> {

    PaginatedViewContextImpl(@NotNull AbstractView root, @Nullable ViewContainer container) {
        super(root, container);
    }
}
