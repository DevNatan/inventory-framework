package me.saiintbrisson.minecraft.examples;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import me.saiintbrisson.minecraft.PaginatedView;
import me.saiintbrisson.minecraft.PaginatedViewContext;
import me.saiintbrisson.minecraft.PaginatedViewSlotContext;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.ViewItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/** Creates a paginated view based on items in the player inventory. */
public final class LayeredNavigablePaginatedViewBasedOnPlayerInventory extends PaginatedView<ItemStack> {

    public LayeredNavigablePaginatedViewBasedOnPlayerInventory() {
        super(3, "Your inventory");
        setCancelOnClick(true);

        // "O"'s are items and "X"'s are empty slots
        // "<" and ">" are navigation items
        setLayout("XXXXXXXXX", "XOOOOOOOX", "XXX<X>XXX");

        firstSlot(new ItemStack(Material.ARROW))
                .onClick(click -> click.paginated().switchToPreviousPage());
        lastSlot(new ItemStack(Material.ARROW))
                .onClick(click -> click.paginated().switchToNextPage());
    }

    @Override
    public ViewItem getPreviousPageItem(@NotNull PaginatedViewContext<ItemStack> context) {
        return item(new ItemStack(Material.ARROW));
    }

    @Override
    public ViewItem getNextPageItem(@NotNull PaginatedViewContext<ItemStack> context) {
        return item(new ItemStack(Material.ARROW));
    }

    @Override
    protected void onRender(@NotNull ViewContext context) {
        context.paginated()
                .setSource(Arrays.stream(context.getPlayer().getInventory().getContents())
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));
    }

    @Override
    protected void onItemRender(PaginatedViewSlotContext<ItemStack> render, ViewItem item, ItemStack value) {
        item.withItem(value.clone());
    }
}
