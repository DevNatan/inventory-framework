package me.saiintbrisson.minecraft;

import me.devnatan.inventoryframework.IFContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ViewContext extends IFContext {

    /**
     * The FIRST Bukkit player linked to this context.
     * <p>
     * "First" because contexts can be shared and contain multiple viewers, this function will
     * always return the first player in the viewer list.
     *
     * @return A player in this context.
     */
    @NotNull
    Player getPlayer();
}
