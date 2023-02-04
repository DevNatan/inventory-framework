package me.devnatan.inventoryframework.example.warpList;

import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewOpenContext;
import me.devnatan.inventoryframework.ViewRenderContext;
import org.bukkit.Material;

@RequiredArgsConstructor
public final class WarpListView extends View {

    private final WarpsManager warpsManager;
    private final Pagination pagination = pagination(
            warpsManager::getWarps, (item, warp) -> item.withItem(warp.getIcon().clone()));

    @Override
    public void onInit(ViewConfigBuilder config) {
        config.layout("         ", " OOOOOOO ", " OOOOOOO ", " OOOOOOO ", "         ", "   < >   ");
    }

    @Override
    public void onOpen(ViewOpenContext ctx) {
        ctx.setTitle(String.format("Warps (%d)", pagination.count(ctx)));
    }

    @Override
    public void onFirstRender(ViewRenderContext ctx) {
        ctx.layoutSlot("<").withItem(Material.ARROW).clicked(pagination::back);
        ctx.layoutSlot(">").withItem(Material.ARROW).clicked(pagination::advance);
    }
}
