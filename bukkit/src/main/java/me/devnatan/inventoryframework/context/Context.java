package me.devnatan.inventoryframework.context;

import java.util.List;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public interface Context extends IFContext {

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

    /**
     * TODO explicit `isShared` doc
     *
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    @UnmodifiableView
    List<Player> getAllPlayers();
}
