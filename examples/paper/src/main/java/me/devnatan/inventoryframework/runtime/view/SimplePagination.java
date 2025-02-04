package me.devnatan.inventoryframework.runtime.view;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.runtime.ExampleUtil;
import me.devnatan.inventoryframework.state.State;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SimplePagination extends View {

    private final State<Pagination> state =
            lazyPaginationState(() -> ExampleUtil.getRandomItems(123), (context, builder, index, value) -> {
                builder.withItem(value);
                builder.onClick((ctx) -> {
                    ctx.getPlayer().sendMessage("You clicked on item " + index);
                });
            });

    @Override
    public void onInit(@NotNull ViewConfigBuilder config) {
        config.cancelOnClick();
        config.size(3);
        config.title("Simple Pagination");
        config.layout("OOOOOOOOO", "OOOOOOOOO", "  P   N  ");
    }

    @Override
    public void onFirstRender(@NotNull RenderContext render) {
        ItemStack previousItem = ExampleUtil.displayItem(Material.ARROW, "Previous");
        ItemStack nextItem = ExampleUtil.displayItem(Material.ARROW, "Next");
        render.layoutSlot('P', previousItem)
                .displayIf(() -> state.get(render).canBack())
                .updateOnStateChange(state)
                .onClick((ctx) -> state.get(ctx).back());
        render.layoutSlot('N', nextItem)
                .displayIf(() -> state.get(render).canAdvance())
                .updateOnStateChange(state)
                .onClick((ctx) -> state.get(ctx).advance());
    }
}
