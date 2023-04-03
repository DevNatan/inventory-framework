package me.devnatan.inventoryframework.runtime.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@RequiredArgsConstructor
public final class IFLibraryConflictWarningListener implements Listener {

    private final String message;

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (!event.getPlayer().isOp()) return;

        event.getPlayer().sendMessage(ChatColor.YELLOW + message);
    }
}
