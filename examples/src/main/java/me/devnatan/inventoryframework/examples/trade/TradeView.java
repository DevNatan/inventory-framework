package me.devnatan.inventoryframework.examples.trade;

import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.bukkit.View;
import me.devnatan.inventoryframework.state.State;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.ViewSlotMoveContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public final class TradeView extends View {

	private static final char LEFT_SIDE = 'A';
	private static final char RIGHT_SIDE = 'B';

	private final State<Trade> trade = initialState(Trade.class);

	@Override
	protected ViewConfig onInit() {
		return createConfig()
			.layout(
				"AAAAVBBBB",
				"AAAAVBBBB",
				"AAAAVBBBB",
				"AAAAVBBBB",
				"AAAAVBBBB",
				"AAAAVBBBB"
			)
			.layout('V', this::createGlassPane);
	}

	@Override
	protected void onRender(ViewContext context) {

	}

	@Override
	protected void onMoveOut(@NotNull ViewSlotMoveContext context) {

	}

	private ItemStack createGlassPane() {
		return new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
	}

}
