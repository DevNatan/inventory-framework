package me.saiintbrisson.minecraft;

import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Context implementation for Bukkit platform.
 */
public abstract class ViewContext extends BaseViewContext {
    protected ViewContext(@NotNull AbstractView root, @Nullable ViewContainer container) {
        super(root, container);
    }

    /**
     * The player linked to this context for the current event.
     * <p>
     * Contexts can be shared and contain multiple viewers, this function will
     * always return the player for the current event.
     *
     * @return A player in this context.
     */
    @NotNull
    public abstract Player getPlayer();
}
