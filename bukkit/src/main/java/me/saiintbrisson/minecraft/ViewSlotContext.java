package me.saiintbrisson.minecraft;

import me.devnatan.inventoryframework.IFContext;
import me.devnatan.inventoryframework.bukkit.BukkitIFContext;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ViewSlotContext extends AbstractViewSlotContext implements BukkitIFContext {

    ViewSlotContext(int slot, @NotNull IFContext parent, @Nullable ViewContainer container) {
        super(slot, parent, container);
    }
}
