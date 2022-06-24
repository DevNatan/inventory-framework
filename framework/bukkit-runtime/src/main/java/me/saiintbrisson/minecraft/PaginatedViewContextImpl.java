package me.saiintbrisson.minecraft;

import lombok.ToString;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ToString(callSuper = true)
final class PaginatedViewContextImpl<T> extends BasePaginatedViewContext<T> {

    PaginatedViewContextImpl(@NotNull AbstractView root, @Nullable ViewContainer container) {
        super(root, container);
    }

    @Override
    public @NotNull Player getPlayer() {
        return BukkitViewer.toPlayerOfContext(this);
    }
}
