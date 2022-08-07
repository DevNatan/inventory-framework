package me.saiintbrisson.minecraft;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

class Test1 extends View {

    public Test1() {
        super(3, Test1.class.getName());
        setLayout("XXXXXXXXX", "XOOOOOOOX", "XXXXXXXXX");

        for (int i = 0; i < 5; i++)
            availableSlot(new ItemStack(Material.GOLD_INGOT)).onClick(ctx -> {
                ctx.getPlayer().sendMessage("clicked on gold ingot at " + ctx.getSlot());
            });

        availableSlot(new ItemStack(Material.IRON_INGOT)).onClick(ctx -> {
            ctx.getPlayer().sendMessage("clicked on iron ingot at " + ctx.getSlot());
        });
    }
}

class Test2 extends PaginatedView<Integer> {

    public Test2() {
        super(3, Test2.class.getName());
        setLayout("YYYYYYYYY", "XOOOOOOOX", "XXXXXXXXX");
        setLayout('Y', (item) -> item.withItem(new ItemStack(Material.ANVIL)));
        setSource(IntStream.rangeClosed(0, 100).boxed().collect(Collectors.toList()));
    }

    @Override
    protected void onItemRender(
            @NotNull PaginatedViewSlotContext<Integer> context, @NotNull ViewItem viewItem, @NotNull Integer value) {
        viewItem.withItem(new ItemStack(Material.IRON_INGOT));
    }
}

class Test3 extends View {

    public Test3() {
        super(3, Test3.class.getName());

        slot(3, new ItemStack(Material.DIAMOND));
        for (int i = 0; i < 5; i++) availableSlot(new ItemStack(Material.GOLD_INGOT));
    }
}

class Test4 extends View {

    public Test4() {
        super(3, Test4.class.getName());
        setLayout("XXXXXXXXX", "XOOOOOOOX", "XXXXXXXXX");

        for (int i = 0; i < 5; i++) availableSlot(new ItemStack(Material.GOLD_INGOT));
    }

    @Override
    protected void onRender(@NotNull ViewContext context) {
        context.availableSlot(new ItemStack(Material.IRON_INGOT));
    }
}

@SuppressWarnings("unused")
public final class InventoryFramework extends JavaPlugin {

    @EventHandler
    public void onEnable() {
        ViewFrame vf = ViewFrame.of(this, new Test1(), new Test2(), new Test3(), new Test4());
        vf.register();

        getServer()
                .getPluginManager()
                .registerEvents(
                        new Listener() {
                            @EventHandler
                            void on(AsyncPlayerChatEvent e) {
                                try {
                                    vf.open(
                                            (Class<? extends AbstractView>)
                                                    Class.forName("me.saiintbrisson.minecraft.Test" + e.getMessage()),
                                            e.getPlayer());
                                } catch (Throwable ex) {
                                    e.getPlayer().sendMessage("Failed to open Test" + e.getMessage());
                                    e.getPlayer().sendMessage(ex.toString());
                                }
                            }
                        },
                        this);
    }
}
