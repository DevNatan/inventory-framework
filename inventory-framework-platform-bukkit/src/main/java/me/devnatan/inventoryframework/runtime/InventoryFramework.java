package me.devnatan.inventoryframework.runtime;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.context.RenderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import test.TestComponent;

public final class InventoryFramework extends JavaPlugin {

    public static final String LIBRARY_VERSION = "3.0.7";
}

class TestView extends View {

    @Override
    public void onFirstRender(@NotNull RenderContext render) {
        render.firstSlot(new TestComponent())
			.cancelOnClick()
			.something()
			.onClick(click -> click.getPlayer().sendMessage("Piroca de foice"));
    }
}
