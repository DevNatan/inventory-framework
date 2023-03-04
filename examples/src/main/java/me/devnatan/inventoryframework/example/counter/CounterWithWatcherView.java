package me.devnatan.inventoryframework.example.counter;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.state.MutableState;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public final class CounterWithWatcherView extends View {

    private final MutableState<Integer> counter = mutableInt();

    @Override
    public void onInit(ViewConfigBuilder config) {
        config.title("Counter");
    }

    @Override
    public void onFirstRender(RenderContext ctx) {
        ctx.slot(2, 3, new ItemStack(Material.ACACIA_BUTTON)).onClick(click -> counter.set(counter.get(click) + 1, click));
        ctx.slot(2, 5, new ItemStack(Material.ACACIA_BUTTON)).onClick(click -> counter.set(counter.get(click) - 1, click));
        ctx.slot(2, 7)
                .onRender(render -> render.setItem(new ItemStack(Material.GOLD_INGOT, counter.get(render))))
                .onUpdate(update -> update.updateTitle(String.format("Counter - %d", counter.get(update))));
    }
}
