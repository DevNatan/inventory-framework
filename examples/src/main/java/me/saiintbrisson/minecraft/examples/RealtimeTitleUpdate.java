package me.saiintbrisson.minecraft.examples;

import me.saiintbrisson.minecraft.View;
import me.saiintbrisson.minecraft.ViewContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/** Updates the current container title or resets it to default title. */
public final class RealtimeTitleUpdate extends View {

    @Override
    protected void onInit() {
        setContainerSize(3);
        setContainerTitle("Initial title");
        setCancelOnClick(true);

        slot(1, new ItemStack(Material.SUGAR_CANE))
                .onClick(click -> click.updateTitle("Hi " + click.getPlayer().getName() + "!"));
        slot(2, new ItemStack(Material.REDSTONE)).onClick(ViewContext::resetTitle);
    }
}
