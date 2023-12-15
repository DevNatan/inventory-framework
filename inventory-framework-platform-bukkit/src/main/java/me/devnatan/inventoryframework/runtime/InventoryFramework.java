package me.devnatan.inventoryframework.runtime;

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
import me.devnatan.inventoryframework.state.MutableIntState;
import me.devnatan.inventoryframework.state.MutableState;
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

    //    {
    //        buildPaginationState(Arrays.asList("A", "B", "C", "D", "E"))
    //                .componentFactory((item, index, value) -> new HeadComponent.Builder()
    //                        .skullName(value)
    //                        .onClick(click -> click.getPlayer().sendMessage("Head " + value)))
    //                .build();
    //    }

    private final MutableState<Boolean> showGoldAxeState = mutableState(false);

    @Override
    public void onInit(@NotNull ViewConfigBuilder config) {
        config.cancelOnClick().layout("         ", "OOOOOOOOO", "         ");
    }

    @Override
    public void onFirstRender(@NotNull RenderContext render) {
        render.firstSlot()
                .withItem(new ItemStack(Material.IRON_AXE))
                .onClick(click -> {
                    click.getPlayer().sendMessage("Clicked on IRON_AXE");
                    showGoldAxeState.set(true, click);
                })
                .hideIf(showGoldAxeState::get)
                .updateOnStateChange(showGoldAxeState);

        render.firstSlot()
                .withItem(new ItemStack(Material.GOLDEN_AXE))
                .onClick(click -> {
                    click.getPlayer().sendMessage("Clicked on GOLDEN_AXE");
                    showGoldAxeState.set(false, click);
                })
                .displayIf(showGoldAxeState::get)
                .updateOnStateChange(showGoldAxeState);
    }
}

class HeadComponent extends BukkitComponentHandle<HeadComponent.Builder> {

    // region Init & Properties
    private final String skullName;

    public HeadComponent(String skullName) {
        this.skullName = skullName;
    }
    // endregion

    // region States
    private final MutableIntState clicksState = mutableState(0);
    // endregion

    // region Handlers
    @Override
    protected void rendered(PublicComponentRenderContext render) {
        final ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(String.format("%s (%d clicks)", skullName, clicksState.get(render)));
        item.setItemMeta(meta);
        render.setItem(item);
    }

    @Override
    protected void clicked(SlotClickContext click) {
        clicksState.increment(click);
        click.getComponent().update();
    }

    @Override
    protected void updated(ComponentUpdateContext update) {
        final Component component = update.getComponent();
        update.getPlayer().sendMessage(String.format("updated: %b", component.isVisible()));
    }
    // endregion

    // region Builder
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
    // endregion
}
