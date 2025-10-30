package me.devnatan.inventoryframework.runtime.view;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.state.MutableIntState;
import me.devnatan.inventoryframework.state.timer.Timer;
import me.devnatan.inventoryframework.state.timer.TimerState;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TimerView extends View {

    private final MutableIntState countState = mutableState(0);
    private final TimerState timerState = timerState(10);

    @Override
    public void onInit(@NotNull ViewConfigBuilder config) {
        config.cancelOnClick().title("Timer (?)").scheduleUpdate(timerState);
    }

    @Override
    public void onFirstRender(@NotNull RenderContext render) {
		// Item to pause/unpause
        render.slot(1, new ItemStack(Material.CLOCK)).onClick(click -> {
            final Timer timer = timerState.get(click);
            timer.pause();
        });

		// Tracking item that reacts to timer state
		render.slot(2, new ItemStack(Material.REDSTONE))
			.updateOnStateChange(timerState)
			.onClick(click -> {
				final Timer timer = timerState.get(click);
				timer.pause();
			});
    }

    @Override
    public void onUpdate(@NotNull Context update) {
        final int count = countState.increment(update);
        final String pause = timerState.get(update).isPaused() ? "paused" : "running";
        update.updateTitleForPlayer(String.format("Timer (%d) [%b]", count, pause));
    }
}
