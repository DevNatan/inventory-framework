package me.saiintbrisson.minecraft.examples;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import me.saiintbrisson.minecraft.PaginatedView;
import me.saiintbrisson.minecraft.PaginatedViewSlotContext;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.ViewItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Adds an item to signal that there are no items available in pagination. Open the View with items
 * in your inventory and then without.
 */
public final class PaginatedViewEmptyState extends PaginatedView<ItemStack> {

    public PaginatedViewEmptyState() {
        super(3, "Empty state");
        setCancelOnClick(true);

        // "O"'s are items and "X"'s are empty slots
        // "<" and ">" are navigation items
        setLayout("XXXXXXXXX", "XOOOOOOOX", "XXXXXXXXX");
    }

    @Override
    protected void onRender(@NotNull ViewContext context) {
        final List<ItemStack> inventoryContents =
                Arrays.stream(context.getPlayer().getInventory().getContents())
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        context.paginated().setSource(inventoryContents);

        /*
        Note that I'm using conditional rendering, because our inventory items don't change while it's open,
        if that happened we'd have to use the item's render function for that. Something like this:

           context.slot(13).onRender(render -> {
               if (inventoryContents.isEmpty())
                   render.setItem(new ItemStack(Material.BEDROCK));
           });
        */
        if (inventoryContents.isEmpty()) context.slot(13, new ItemStack(Material.BEDROCK));
    }

    @Override
    protected void onItemRender(
            PaginatedViewSlotContext<ItemStack> render, ViewItem item, ItemStack value) {
        item.withItem(value.clone());
    }
}
