package me.saiintbrisson.minecraft.examples;

import me.saiintbrisson.minecraft.View;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class StaticFilledView extends View {

    @Override
    protected void onInit() {
        setContainerSize(3);
        setContainerTitle("Scooter Turtle");

        slot(1, new ItemStack(Material.GOLD_INGOT));
        slot(3, 5, new ItemStack(Material.DIAMOND));
        firstSlot(new ItemStack(Material.PAPER));
        lastSlot(new ItemStack(Material.MAP));
    }
}
