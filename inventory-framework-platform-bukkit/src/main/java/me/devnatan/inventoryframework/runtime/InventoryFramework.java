package me.devnatan.inventoryframework.runtime;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.context.RenderContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public final class InventoryFramework extends JavaPlugin {

    public static final String LIBRARY_VERSION = "3.0.0-rc.2";

    @Override
    public void onEnable() {
        ViewFrame vf = ViewFrame.create(this).with(new Test()).register();

        getServer().getOnlinePlayers().forEach(player -> vf.open(Test.class, player));
    }
}

class Test extends View {

    @Override
    public void onInit(ViewConfigBuilder config) {
        config.title("Textando");
    }

    @Override
    public void onFirstRender(RenderContext render) {
        render.slot(3, new ItemStack(Material.GOLD_INGOT));
    }
}
