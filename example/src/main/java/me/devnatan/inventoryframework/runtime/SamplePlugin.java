package me.devnatan.inventoryframework.runtime;

import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.runtime.commands.IFExampleCommandExecutor;
import me.devnatan.inventoryframework.runtime.view.Failing;
import me.devnatan.inventoryframework.runtime.view.SimplePagination;
import org.bukkit.plugin.java.JavaPlugin;

public class SamplePlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		ViewFrame viewFrame = ViewFrame.create(this)
			.with(new Failing(), new SimplePagination())
			.register();

		getCommand("ifexample").setExecutor(new IFExampleCommandExecutor(viewFrame));
	}
}
