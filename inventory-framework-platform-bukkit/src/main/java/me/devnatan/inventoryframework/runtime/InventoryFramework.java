package me.devnatan.inventoryframework.runtime;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.context.RenderContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class InventoryFramework extends JavaPlugin {

    public static final String LIBRARY_VERSION = "3.1.0-beta";

    @Override
    public void onEnable() {
        ViewFrame.create(this).enableDebug().with(new Test()).register();
    }
}

class Test extends View {

    @Override
    public void onFirstRender(@NotNull RenderContext render) {
        render.firstSlot().withItem(new ItemStack(Material.GOLD_INGOT));
    }
}
