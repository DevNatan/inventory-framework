package me.devnatan.inventoryframework.example.warpList;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.context.OpenContext;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.state.Pagination;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public final class WarpListView extends View {

    private final Pagination pagination;

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
        ctx.setTitle(String.format("Warps (%d)", pagination.count(ctx)));
    }

    @Override
    public void onFirstRender(RenderContext ctx) {
        ctx.layoutSlot("<").withItem(Material.ARROW).clicked(pagination::back);
        ctx.layoutSlot(">").withItem(Material.ARROW).clicked(pagination::advance);
    }
}
