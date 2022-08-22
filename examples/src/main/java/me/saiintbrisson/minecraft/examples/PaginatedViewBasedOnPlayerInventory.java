package me.saiintbrisson.minecraft.examples;

import java.util.Arrays;
import me.saiintbrisson.minecraft.PaginatedView;
import me.saiintbrisson.minecraft.PaginatedViewSlotContext;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.ViewItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/** Creates a paginated view based on items in the player inventory. */
public final class PaginatedViewBasedOnPlayerInventory extends PaginatedView<ItemStack> {

    public PaginatedViewBasedOnPlayerInventory() {
        super(3, "Your inventory");
        setCancelOnClick(true);
        setOffset(1);
        setLimit(26);

        firstSlot(new ItemStack(Material.ARROW))
                .onClick(click -> click.paginated().switchToPreviousPage());
        lastSlot(new ItemStack(Material.ARROW))
                .onClick(click -> click.paginated().switchToNextPage());
    }

    @Override
    protected void onRender(@NotNull ViewContext context) {
        context.paginated()
                .setSource(Arrays.asList(context.getPlayer().getInventory().getContents()));
    }

    @Override
    protected void onItemRender(@NotNull PaginatedViewSlotContext<ItemStack> render, @NotNull ViewItem item, @NotNull ItemStack value) {
        item.withItem(value.clone());
    }
}
