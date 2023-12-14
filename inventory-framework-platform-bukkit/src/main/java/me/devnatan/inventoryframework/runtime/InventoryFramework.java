package me.devnatan.inventoryframework.runtime;

import java.util.Arrays;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.component.BukkitComponentBuilder;
import me.devnatan.inventoryframework.component.BukkitComponentHandle;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentHandle;
import me.devnatan.inventoryframework.context.ComponentUpdateContext;
import me.devnatan.inventoryframework.context.PublicComponentRenderContext;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class InventoryFramework extends JavaPlugin {

    public static final String LIBRARY_VERSION = "3.0.7";

    @Override
    public void onEnable() {
        ViewFrame vf = ViewFrame.create(this).enableDebug().with(new TestView()).register();

        getServer().getOnlinePlayers().forEach(player -> {
            vf.open(TestView.class, player);
        });
    }
}

class TestView extends View {

    {
        buildPaginationState(Arrays.asList("A", "B", "C", "D", "E"))
                .componentFactory((item, index, value) -> new HeadComponent.Builder()
                        .skullName(value)
                        .onClick(click -> click.getPlayer().sendMessage("Head " + value)))
                .build();
    }

    @Override
    public void onInit(@NotNull ViewConfigBuilder config) {
        config.cancelOnClick().layout("         ", "OOOOOOOOO", "         ");
    }

    @Override
    public void onFirstRender(@NotNull RenderContext render) {
        render.firstSlot()
                .withSlot(3)
                .withItem(new ItemStack(Material.IRON_AXE))
                .onClick(click -> click.getPlayer().sendMessage("Clicked on IRON_AXE"));
    }
}

class HeadComponent extends BukkitComponentHandle<HeadComponent.Builder> {

    private final String skullName;

    public HeadComponent(String skullName) {
        this.skullName = skullName;
    }

    @Override
    protected void rendered(PublicComponentRenderContext render) {
        final ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(skullName);
        item.setItemMeta(meta);
        render.setItem(item);
    }

    @Override
    protected void clicked(SlotClickContext click) {
        final Component component = click.getComponent();
        click.getPlayer().sendMessage(String.format("clicked: %b", component.isSelfManaged()));

        if (component.isVisible()) component.hide();
        else component.show();
    }

    @Override
    protected void updated(ComponentUpdateContext update) {
        final Component component = update.getComponent();
        update.getPlayer().sendMessage(String.format("updated: %b", component.isVisible()));
    }

    @Override
    public HeadComponent.Builder builder() {
        return new HeadComponent.Builder();
    }

    public static class Builder extends BukkitComponentBuilder<HeadComponent.Builder> {

        private String skullName;

        public HeadComponent.Builder skullName(String skullName) {
            this.skullName = skullName;
            return this;
        }

        @Override
        public ComponentHandle buildHandle() {
            return new HeadComponent(skullName);
        }
    }
}
