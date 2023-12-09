package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.context.OpenContext;
import me.devnatan.inventoryframework.context.RenderContext;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Test extends JavaPlugin {

	@Override
	public void onEnable() {
		ViewFrame vf = ViewFrame.create(this)
			.with(new TestView())
			.register();

		getServer().getOnlinePlayers().forEach(player -> vf.open(TestView.class, player));
	}
}

class TestView extends View {

	@Override
	public void onOpen(@NotNull OpenContext open) {
		open.modifyConfig()
			.title("Piroca de foice")
			.cancelOnClick()
			.use(Proxy.createProxy(open.getPlayer().getInventory()));
	}

	@Override
	public void onFirstRender(@NotNull RenderContext render) {
		render.firstSlot(new ItemStack(Material.ARROW));
		render.lastSlot(new ItemStack(Material.ARROW));
	}
}
