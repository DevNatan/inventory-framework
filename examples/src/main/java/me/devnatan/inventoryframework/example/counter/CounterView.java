package me.devnatan.inventoryframework.example.counter;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewRenderContext;
import me.devnatan.inventoryframework.state.MutableInt;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public final class CounterView extends View {

	private final MutableInt counter = mutableInt(0);

	@Override
	public void onInit(ViewConfigBuilder config) {
		config
			.title("Contador")
			.layout(
				"XXXXXXXXX",
				"X - V + X",
				"XXXXXXXXX"
			);
	}

	@Override
	public void onRender(ViewRenderContext ctx) {
		ctx.layoutSlot('+')
			.withItem(Material.ACACIA_BUTTON)
			.onClick(counter::increment);

		ctx.layoutSlot('-')
			.withItem(Material.ACACIA_BUTTON)
			.onClick(counter::decrement);

		ctx.layoutSlot('V')
			.rendered(() -> new ItemStack(Material.GOLD_INGOT, counter.getAsInt(ctx)));
	}

}