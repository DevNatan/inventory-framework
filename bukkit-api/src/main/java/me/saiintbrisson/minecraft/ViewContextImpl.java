package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@ToString(callSuper = true)
class ViewContextImpl extends BaseViewContext {

    @Setter
    private boolean cancelled;

    ViewContextImpl(@NotNull final AbstractView root, @NotNull final ViewContainer container) {
        super(root, container);
    }

    @Override
    public @NotNull Player getPlayer() {
        return BukkitViewer.toPlayerOfContext(this);
    }
}
