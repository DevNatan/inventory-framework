package me.saiintbrisson.minecraft;

import me.devnatan.inventoryframework.IFContext;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import org.jetbrains.annotations.NotNull;

public class ViewSlotContext extends AbstractViewSlotContext {

    ViewSlotContext(int slot, ViewItem backingItem, @NotNull IFContext parent, ViewContainer container) {
        super(slot, backingItem, parent, container);
    }

    //	public final ItemStack getItem() {
    //		throw new UnsupportedOperationException();
    //	}
}
