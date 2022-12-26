package me.saiintbrisson.minecraft;

import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ViewRenderContext extends BaseViewContext {
    protected ViewRenderContext(@NotNull AbstractView root, @Nullable ViewContainer container) {
        super(root, container);
    }

    @NotNull
    public abstract Player getPlayer();
}
