package me.devnatan.inventoryframework.runtime;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.state.History;
import me.devnatan.inventoryframework.state.HistoryState;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class InventoryFramework extends JavaPlugin {

    public static final String LIBRARY_VERSION = "3.0.0-rc.2";
}

class TestView extends View {

    private final HistoryState historyState = historyState();

    @Override
    public void onFirstRender(RenderContext render) {
        final History history = historyState.get(render);
        render.firstSlot(new ItemStack(Material.GOLD_INGOT)).onClick(history::pop);
    }
}
