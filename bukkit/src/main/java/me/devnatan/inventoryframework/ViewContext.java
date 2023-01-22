package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.context.IFContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Marker interface used for backwards compatibility.
 */
public interface ViewContext extends IFContext {

    /**
     * The player for the current interaction context.
     * <p>
     * Contexts can be shared and contain multiple viewers, this method will
     * always return the player for the current event.
     *
     * @return A player in this interaction context.
     */
    @NotNull
    Player getPlayer();
}
