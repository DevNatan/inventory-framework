package me.saiintbrisson.minecraft.examples;

import static java.util.Objects.requireNonNull;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import me.saiintbrisson.minecraft.PaginatedView;
import me.saiintbrisson.minecraft.PaginatedViewSlotContext;
import me.saiintbrisson.minecraft.ViewItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class PersistentNavigablePaginatedView extends PaginatedView<Integer> {

    public PersistentNavigablePaginatedView() {
        super(6, "Paginated view");

        // pagination source
        setSource(IntStream.rangeClosed(0, 100).boxed().collect(Collectors.toList()));
        setOffset(10 /* slot where paging will start */);
        setLimit(27 /* slot where paging will end */);

        // navigation arrows
        firstSlot(new ItemStack(Material.ARROW))
                .onClick(click -> click.paginated().switchToPreviousPage());
        lastSlot(new ItemStack(Material.ARROW))
                .onClick(click -> click.paginated().switchToNextPage());
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
