package me.devnatan.inventoryframework.example.counter;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewRenderContext;
import me.devnatan.inventoryframework.state.MutableIntState;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public final class CounterWithReferenceView extends View {

	private final MutableIntState counter = mutableInt();

	@Override
	public void onInit(ViewConfigBuilder config) {
		config.title("Counter");
	}

	@Override
	public void onInitialRender(ViewRenderContext ctx) {
		ctx.slot(2, 3).item(Material.ACACIA_BUTTON).clicked(counter::increment);
		ctx.slot(2, 5).item(Material.ACACIA_BUTTON).clicked(counter::decrement);

		watchUpdate(counter, (oldValue, newValue) -> {
			ctx.updateTitle(String.format(
				"Counter - %d",
				counter.getAsInt(ctx)
			));
		});

		ctx.slot(2, 7)
			// re-render this item update every time [counter] state is updated
			.watch(counter)
			// note the [rendered] here, state-access need dynamic render
			.rendered(() -> new ItemStack(Material.GOLD_INGOT, counter.getAsInt(ctx)));
	}

}