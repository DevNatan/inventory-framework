package me.saiintbrisson.minecraft;

import me.devnatan.inventoryframework.IFContext;
import me.devnatan.inventoryframework.bukkit.BukkitIFContext;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ViewSlotContext extends AbstractViewSlotContext implements BukkitIFContext {

    private final Player player;

    ViewSlotContext(
            int slot,
            @NotNull ViewItem backingItem,
            @NotNull IFContext parent,
            @Nullable ViewContainer container,
            @NotNull Player player) {
        super(slot, backingItem, parent, container);
        this.player = player;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }
}
