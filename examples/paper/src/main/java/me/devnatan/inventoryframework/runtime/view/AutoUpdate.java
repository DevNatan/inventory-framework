package me.devnatan.inventoryframework.runtime.view;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.state.MutableIntState;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AutoUpdate extends View {

    private final MutableIntState countState = mutableState(0);

    @Override
    public void onInit(@NotNull ViewConfigBuilder config) {
        config.cancelOnClick()
                .title(Component.text("Auto update", NamedTextColor.RED))
                .scheduleUpdate(10);
    }

    @Override
    public void onFirstRender(@NotNull RenderContext render) {
        render.slot(1, new ItemStack(Material.DIAMOND)).onClick(click -> click.openForPlayer(SimplePagination.class));
    }

    @Override
    public void onUpdate(@NotNull Context update) {
        final int count = countState.increment(update);
        final Random random = ThreadLocalRandom.current();
        final TextColor titleColor = TextColor.color(
                random.nextInt(0, 255), // (r)gb
                random.nextInt(0, 255), // r(g)b
                random.nextInt(0, 255) // rg(b)
                );

        update.updateTitleForPlayer(Component.text("Auto update (" + count + ")", titleColor));
    }
}
