package me.devnatan.inventoryframework.bukkit;

import me.devnatan.inventoryframework.context.IFContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface BukkitIFContext extends IFContext {

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
