package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.devnatan.inventoryframework.IFContext;
import me.saiintbrisson.minecraft.internal.platform.ViewContainer;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit platform ViewSlotContext implementation.
 */
@Getter
@Setter
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class BukkitViewSlotContext extends AbstractViewSlotContext {

    BukkitViewSlotContext(int slot, ViewItem backingItem, @NotNull IFContext parent, ViewContainer container) {
        super(slot, backingItem, parent, container);
    }
}
