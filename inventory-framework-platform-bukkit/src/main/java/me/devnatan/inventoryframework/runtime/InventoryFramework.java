package me.devnatan.inventoryframework.runtime;

import java.util.Arrays;
import me.devnatan.inventoryframework.IFDebug;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.component.BukkitComponentBuilder;
import me.devnatan.inventoryframework.component.BukkitComponentHandle;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentHandle;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.context.ComponentUpdateContext;
import me.devnatan.inventoryframework.context.PublicComponentRenderContext;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.state.State;
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

        render.lastSlot();
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
        IFDebug.debug("Set item %s", skullName);
    }

    @Override
    protected void clicked(SlotClickContext click) {
        final Component component = click.getComponent();
        click.getPlayer().sendMessage(String.format("clicked: %b", component.isVisible()));

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

class MyComponent extends BukkitComponentHandle<MyComponentBuilder> {

    private final State<Pagination> leftPaginationState = buildPaginationState(Arrays.asList("A", "B", "C", "D", "E"))
            .layoutTarget('L')
            .itemFactory((item, value) -> item.withItem(new ItemStack(Material.GOLD_INGOT)))
            .build();

    private final State<Pagination> rightPaginationState = buildPaginationState(Arrays.asList("F", "G", "H", "I", "J"))
            .layoutTarget('R')
            .itemFactory((item, value) -> item.withItem(new ItemStack(Material.IRON_INGOT)))
            .componentFactory((context, index, value) -> new HeadComponent.Builder().skullName("BADNOTICE"))
            .build();

    private final String text;

    public MyComponent(String text) {
        this.text = text;
    }

    @Override
    protected void rendered(PublicComponentRenderContext render) {
        Pagination leftPagination = leftPaginationState.get(render);
        Pagination rightPagination = rightPaginationState.get(render);

        // Manipulate "L" pagination navigation
        render.slot(0).updateOnStateChange(leftPaginationState).onClick(leftPagination::back);
        render.slot(1).updateOnStateChange(leftPaginationState).onClick(leftPagination::advance);

        // Manipulate "R" pagination navigation
        render.slot(2).updateOnStateChange(rightPaginationState).onClick(rightPagination::back);
        render.slot(3).updateOnStateChange(rightPaginationState).onClick(rightPagination::advance);
    }

    @Override
    public MyComponentBuilder builder() {
        return new MyComponentBuilder();
    }
}

class MyComponentBuilder extends BukkitComponentBuilder<MyComponentBuilder> {

    private String text;

    MyComponentBuilder text(String text) {
        this.text = text;
        return this;
    }

    @Override
    public ComponentHandle buildHandle() {
        return new MyComponent(text);
    }
}
