package me.devnatan.inventoryframework.example.counter;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.state.MutableIntState;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public final class CounterWithWatcherView extends View {

    private final MutableIntState counter = mutableInt();

    @Override
    public void onInit(ViewConfigBuilder config) {
        config.title("Counter");
    }

    @Override
    public void onFirstRender(RenderContext ctx) {
        ctx.slot(2, 3).item(Material.ACACIA_BUTTON).clicked(counter::increment);
        ctx.slot(2, 5).item(Material.ACACIA_BUTTON).clicked(counter::decrement);
        ctx.slot(2, 7)
                .renderedWith(() -> new ItemStack(Material.GOLD_INGOT, counter.get(ctx)))
                .updated(slotCtx -> slotCtx.updateTitle(String.format("Counter - %d", counter.get(slotCtx))))
                .watch(counter);
    }
}
