package me.saiintbrisson.minecraft.examples;

import static java.util.Objects.requireNonNull;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import me.saiintbrisson.minecraft.PaginatedView;
import me.saiintbrisson.minecraft.PaginatedViewContext;
import me.saiintbrisson.minecraft.PaginatedViewSlotContext;
import me.saiintbrisson.minecraft.ViewItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class PersistentLayeredNavigablePaginatedView extends PaginatedView<Integer> {

    public PersistentLayeredNavigablePaginatedView() {
        super(6, "Paginated view");

        // pagination source
        setSource(IntStream.rangeClosed(0, 100).boxed().collect(Collectors.toList()));

        // "O"'s are items and "X"'s are empty slots
        // "<" and ">" are navigation items
        setLayout("XXXXXXXXX", "XOOOOOOOX", "XOOOOOOOX", "XOOOOOOOX", "XOOOOOOOX", "XXX<X>XXX");
    }

    @Override
    public ViewItem getPreviousPageItem(@NotNull PaginatedViewContext<Integer> context) {
        return item(new ItemStack(Material.ARROW));
    }

    @Override
    public ViewItem getNextPageItem(@NotNull PaginatedViewContext<Integer> context) {
        return item(new ItemStack(Material.ARROW));
    }

    @Override
    protected void onItemRender(PaginatedViewSlotContext<Integer> render, ViewItem item, Integer value) {
        item.withItem(createPaginationItemStack(value));
    }

    private ItemStack createPaginationItemStack(int value) {
        ItemStack stack = new ItemStack(Material.PAPER);
        ItemMeta meta = requireNonNull(stack.getItemMeta());
        meta.setDisplayName("Item " + value);
        stack.setItemMeta(meta);
        return stack;
    }
}
