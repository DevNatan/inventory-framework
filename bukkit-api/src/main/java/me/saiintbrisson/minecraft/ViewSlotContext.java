package me.saiintbrisson.minecraft;

import me.devnatan.inventoryframework.IFSlotContext;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ViewSlotContext extends IFSlotContext {

    @NotNull
    ItemStack getItem();
}
