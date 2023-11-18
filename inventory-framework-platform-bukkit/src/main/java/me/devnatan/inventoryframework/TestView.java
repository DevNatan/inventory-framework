package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.context.RenderContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TestView extends View {

	@Override
	public void onFirstRender(@NotNull RenderContext render) {
		render.lastSlot(new ItemStack(Material.DIAMOND));
	}
}
