package me.saiintbrisson.minecraft.examples;

import me.saiintbrisson.minecraft.View;
import me.saiintbrisson.minecraft.ViewContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class AmountSelectorView extends View {

	private static final String CURRENT_AMOUNT_KEY = "current-amount";

	public AmountSelectorView() {
		super(3, "Amount Selector");

		// item with current value
		slot(2, 5).onRender(render -> render.setItem(
			new ItemStack(Material.PAPER, render.get(CURRENT_AMOUNT_KEY, () -> 1))
		));

		// increment
		slot(2, 7, new ItemStack(Material.ARROW))
			.onClick(click -> updateAmount(click, +1));

		// decrement
		slot(2, 3, new ItemStack(Material.ARROW))
			.onClick(click -> updateAmount(click, -1));
	}

	private void updateAmount(ViewContext context, int mod) {
		context.set(CURRENT_AMOUNT_KEY, context.get(CURRENT_AMOUNT_KEY, () -> 1) + mod);
		context.update();
	}

}
