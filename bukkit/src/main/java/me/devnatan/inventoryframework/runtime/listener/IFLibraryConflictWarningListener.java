package me.devnatan.inventoryframework.runtime.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

// TODO Move to plugin package and make package-private
public final class IFLibraryConflictWarningListener implements Listener {

    private final String message;

    public IFLibraryConflictWarningListener(String message) {
        this.message = message;
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (!event.getPlayer().isOp()) return;

        event.getPlayer().sendMessage(ChatColor.YELLOW + message);
    }
}
