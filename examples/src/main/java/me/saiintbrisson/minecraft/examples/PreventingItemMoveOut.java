package me.saiintbrisson.minecraft.examples;

import me.saiintbrisson.minecraft.View;
import me.saiintbrisson.minecraft.ViewSlotMoveContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/** Prevents players from moving items out of the view's inventory. */
public final class PreventingItemMoveOut extends View {

    @Override
    protected void onInit() {
        setContainerSize(3);
        setContainerTitle("Conditional rendering");

        // Move out only works on item that player can move!
        slot(3, new ItemStack(Material.PAPER)).setCancelOnClick(false);
    }

    @Override
    protected void onMoveOut(@NotNull ViewSlotMoveContext context) {
        context.setCancelled(true);
        context.getPlayer().sendMessage("You can't move items out!!");
    }
}
