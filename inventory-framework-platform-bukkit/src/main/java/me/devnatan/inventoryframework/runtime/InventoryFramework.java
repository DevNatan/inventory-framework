package me.devnatan.inventoryframework.runtime;

import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.RenderContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class InventoryFramework extends JavaPlugin {

    public static final String LIBRARY_VERSION = "3.0.0-rc.3";

    @Override
    public void onEnable() {
        ViewFrame vf = ViewFrame.create(this).enableDebug().with(new A()).register();
        getServer().getOnlinePlayers().forEach(player -> vf.open(A.class, player));
    }
}

class A extends View {

    private final Ref<Component> diamondRef = ref();

    @Override
    public void onInit(@NotNull ViewConfigBuilder config) {
        config.title("S");
    }

    @Override
    public void onFirstRender(@NotNull RenderContext render) {
        render.firstSlot(new ItemStack(Material.DIAMOND))
                .referencedBy(diamondRef)
                .cancelOnClick();
    }
}
