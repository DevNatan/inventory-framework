package me.devnatan.inventoryframework.runtime.view;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.state.MutableIntState;
import me.devnatan.inventoryframework.state.TimerState;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AutoUpdateAdvanced extends View {

    private final MutableIntState countState = mutableState(0);
    private final TimerState autoUpdateState = timerState(10);

    @Override
    public void onInit(@NotNull ViewConfigBuilder config) {
        config.cancelOnClick().title("Auto update (?)").scheduleUpdate(autoUpdateState);
    }

    @Override
    public void onFirstRender(@NotNull RenderContext render) {
        render.slot(1, new ItemStack(Material.DIAMOND)).onClick(click -> click.openForPlayer(SimplePagination.class));

        render.slot(2, new ItemStack(Material.CLOCK)).onClick(click -> {
            final var timer = autoUpdateState.get(click);
            timer.togglePause();
        });
    }

    @Override
    public void onUpdate(@NotNull Context update) {
        final int count = countState.increment(update);
        final String pause = autoUpdateState.get(update).isPaused() ? "paused" : "running";
        update.updateTitleForPlayer(String.format("Auto update (%d) [%b]", count, pause));
    }
}
