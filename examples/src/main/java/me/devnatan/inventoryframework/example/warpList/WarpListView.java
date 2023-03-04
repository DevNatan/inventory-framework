package me.devnatan.inventoryframework.example.warpList;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.context.OpenContext;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.state.State;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class WarpListView extends View {

    private final State<Pagination> pagination;

    public WarpListView(@NotNull WarpsManager warpsManager) {
        pagination = pagination(
                warpsManager::getWarps,
                (item, warp) -> item.withItem(warp.getIcon().clone()));
    }

    @Override
    public void onInit(ViewConfigBuilder config) {
        config.layout("         ", " OOOOOOO ", " OOOOOOO ", " OOOOOOO ", "         ", "   < >   ");
    }

    @Override
    public void onOpen(OpenContext ctx) {
        ctx.setTitle("Warps");
    }

    @Override
    public void onFirstRender(RenderContext ctx) {
        final Pagination localPagination = pagination.get(ctx);
        ctx.layoutSlot("<").withItem(new ItemStack(Material.ARROW)).onClick($ -> localPagination.back());
        ctx.layoutSlot(">").withItem(new ItemStack(Material.ARROW)).onClick($ -> localPagination.advance());
    }
}
