package me.saiintbrisson.minecraft.examples;

import me.saiintbrisson.minecraft.View;
import me.saiintbrisson.minecraft.ViewSlotContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class GlobalClickHandling extends View {

    @Override
    protected void onInit() {
        size(3);
        title("Global click handling");

        slot(1, new ItemStack(Material.DIAMOND));
        slot(2, new ItemStack(Material.GOLD_INGOT));
        slot(3, new ItemStack(Material.IRON_INGOT));
        slot(4, new ItemStack(Material.EMERALD));
    }

    @Override
    protected void onClick(@NotNull ViewSlotContext context) {
        context.getPlayer().sendMessage("Clicked on a " + context.getItem().getType());
    }
}
