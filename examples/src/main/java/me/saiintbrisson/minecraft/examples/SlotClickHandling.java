package me.saiintbrisson.minecraft.examples;

import me.saiintbrisson.minecraft.View;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class SlotClickHandling extends View {

    @Override
    protected void onInit() {
        setContainerSize(3);
        setContainerTitle("Slot click handling");

        slot(1, new ItemStack(Material.DIAMOND))
                .onClick(context -> context.getPlayer().sendMessage("Clicked on a Diamond."));
    }
}
