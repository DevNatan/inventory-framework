package me.devnatan.inventoryframework.example.warpList;

import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewOpenContext;
import me.devnatan.inventoryframework.ViewRenderContext;
import me.devnatan.inventoryframework.state.Pagination;
import org.bukkit.Material;

@RequiredArgsConstructor
public final class WarpListView extends View {

	private final WarpsManager warpsManager;

	private final Pagination<Warp> pagination = pagination(
		warpsManager::getWarps,
		(item, warp) -> item.withItem(warp.getIcon().clone())
	);

	@Override
	public void onInit(ViewConfigBuilder config) {
		config.layout(
			"XXXXXXXXX",
			"XOOOOOOOX",
			"XOOOOOOOX",
			"XOOOOOOOX",
			"XXXXXXXXX",
			"XXX<X>XXX"
		);
	}

	@Override
	public void onOpen(ViewOpenContext ctx) {
		ctx.setTitle(String.format("Warps (%d)", pagination.count(ctx)));
	}

	@Override
	public void onRender(ViewRenderContext ctx) {
		ctx.layoutSlot(NAVIGATE_BACKWARDS)
			.withItem(Material.ARROW)
			.renderIf(pagination::canBack)
			.onClick(pagination::back);

		ctx.layoutSlot(NAVIGATE_FORWARD)
			.withItem(Material.ARROW)
			.renderIf(pagination::canAdvance)
			.onClick(pagination::advance);
	}

}
