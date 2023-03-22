package me.devnatan.inventoryframework.bukkit;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.state.State;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public final class InventoryFramework extends JavaPlugin {

    @Override
    public void onEnable() {
        ViewFrame vf = ViewFrame.create(this).with(new AwesomeView()).register();

        getServer()
                .getPluginManager()
                .registerEvents(
                        new Listener() {
                            @EventHandler
                            void onChat(AsyncPlayerChatEvent event) {
                                vf.open(AwesomeView.class, event.getPlayer());
                            }
                        },
                        this);
    }
}

class AwesomeView extends View {

    private final State<Pagination> paginationState = pagination(
            () -> IntStream.rangeClosed(0, 100).boxed().collect(Collectors.toList()),
            (item, value) -> item.withItem(new ItemStack(Material.DIAMOND)));

    @Override
    public void onInit(ViewConfigBuilder config) {
        config.type(ViewType.CHEST).layout("         ", "         ", "   F     ");
    }

    @Override
    public void onFirstRender(RenderContext render) {
        int index = 0;
        for (String name : Arrays.asList("Notch", "DevNatan", "Steve", "Github", "SpiderMan")) {
            final ItemStack item = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
            final SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwner(name);
            item.setItemMeta(meta);

            render.slot(index++, item);
        }

        //		final Pagination pagination = paginationState.get(render);
        //		render.layoutSlot('F', (index, item) -> item
        //			.withItem(new ItemStack(Material.IRON_INGOT))
        //			.onClick($ -> pagination.advance())
        //		);
    }
}
