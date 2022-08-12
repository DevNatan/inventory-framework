package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class BukkitViewSlotContext extends AbstractViewSlotContext {

    BukkitViewSlotContext(ViewItem backingItem, @NotNull ViewContext parent) {
        super(backingItem, parent);
    }

    public int getSlot() {
        return getBackingItem().getSlot();
    }

    @Override
    public @NotNull Player getPlayer() {
        return BukkitViewer.toPlayerOfContext(this);
    }
}
