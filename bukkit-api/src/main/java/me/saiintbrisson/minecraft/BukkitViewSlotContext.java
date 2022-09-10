package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit platform ViewSlotContext implementation.
 */
@Getter
@Setter
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class BukkitViewSlotContext extends AbstractViewSlotContext {

    BukkitViewSlotContext(int slot, ViewItem backingItem, @NotNull ViewContext parent, ViewContainer container) {
        super(slot, backingItem, parent, container);
    }

    @Override
    @NotNull
    public final Player getPlayer() {
        return BukkitViewer.toPlayerOfContext(this);
    }
}
