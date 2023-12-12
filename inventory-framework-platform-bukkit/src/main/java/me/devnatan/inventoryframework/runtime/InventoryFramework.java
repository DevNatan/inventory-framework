package me.devnatan.inventoryframework.runtime;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.context.SlotContext;
import me.devnatan.inventoryframework.state.State;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class InventoryFramework extends JavaPlugin {

    public static final String LIBRARY_VERSION = "3.0.6";

    @Override
    public void onEnable() {
        ViewFrame vf = ViewFrame.create(this).with(new TestView()).enableDebug().register();

        getServer()
                .getPluginManager()
                .registerEvents(
                        new Listener() {
                            @EventHandler
                            public void onChat(AsyncPlayerChatEvent event) {
                                getServer().getScheduler().runTask(InventoryFramework.this, () -> {
                                    vf.open(TestView.class, event.getPlayer());
                                });
                            }
                        },
                        this);
    }
}

class TestView extends View {

    final State<Pagination> paginationState = lazyPaginationState(
            (context) -> IntStream.range(0, 100).boxed().collect(Collectors.toList()),
            (context, builder, index, value) -> builder.renderWith(() -> new ItemStack(Material.GOLD_INGOT))
                    .onClick(click -> click.getPlayer().sendMessage("clicked on " + value)));

    @Override
    public void onInit(@NotNull ViewConfigBuilder config) {
        config.cancelOnClick().layout("AAAAAAAAA", "OOOOOOOOO", "UAAAAAAAA");
    }

    @Override
    public void onFirstRender(@NotNull RenderContext render) {
        final Pagination pagination = paginationState.get(render);
        render.layoutSlot('A', new ItemStack(Material.IRON_INGOT));
        render.slot(21, new ItemStack(Material.ARROW))
                .displayIf(pagination::canBack)
                .updateOnStateChange(paginationState)
                .onClick(pagination::back);

        render.slot(23, new ItemStack(Material.ARROW))
                .displayIf(pagination::canAdvance)
                .updateOnStateChange(paginationState)
                .onClick(pagination::advance);

        render.layoutSlot('U', new ItemStack(Material.IRON_AXE)).onClick(SlotContext::update);
    }
}
