package me.saiintbrisson.minecraft.examples;

import java.util.Random;
import me.saiintbrisson.minecraft.View;
import me.saiintbrisson.minecraft.ViewContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Updates current view every 5 ticks. On each update, the type of the item in the View is changed.
 */
public final class ItemUpdateOnAutomaticUpdate extends View {

    public ItemUpdateOnAutomaticUpdate() {
        super(3, "Scooter Turtle");

        // 5 ticks
        scheduleUpdate(20L * 5);

        slot(3).onUpdate(update -> update.setItem(new ItemStack(getRandomMaterial())));
    }

    @Override
    protected void onUpdate(@NotNull ViewContext context) {
        context.getPlayer().sendMessage("Updated.");
    }

    private Material getRandomMaterial() {
        // it's not optimized, don't use this!!!
        return Material.values()[new Random().nextInt(Material.values().length)];
    }
}
