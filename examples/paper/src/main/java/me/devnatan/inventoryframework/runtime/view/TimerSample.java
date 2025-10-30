package me.devnatan.inventoryframework.runtime.view;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.context.SlotRenderContext;
import me.devnatan.inventoryframework.state.MutableIntState;
import me.devnatan.inventoryframework.state.timer.Timer;
import me.devnatan.inventoryframework.state.timer.TimerState;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class TimerSample extends View {

    private final MutableIntState countState = mutableState(0);
    private final TimerState timerState = timerState(20);

    @Override
    public void onInit(@NotNull ViewConfigBuilder config) {
        config.cancelOnClick().title("Timer (?)").scheduleUpdate(timerState);
    }

    @Override
    public void onFirstRender(@NotNull RenderContext render) {
		render.firstSlot()
			.onRender(this::onClockItemRender)
			.onClick(this::onClockItemClick)
			.updateOnStateChange(timerState);

		render.lastSlot()
			.renderWith(() -> createIntervalItem(timerState.get(render)))
			.onClick(this::onIntervalItemClicked)
			.updateOnStateChange(timerState);
    }

	@Override
	public void onUpdate(@NotNull Context update) {
		final int count = countState.increment(update);
		final String pause = timerState.get(update).isPaused() ? "paused" : "running";
		update.updateTitleForPlayer(String.format("Timer (%d) [%b]", count, pause));
	}

	private void onIntervalItemClicked(SlotClickContext click) {
		final Timer timer = timerState.get(click);
		final long newInterval = (timer.currentInterval() + 1) % 20;

		click.getPlayer().sendMessage(String.format(
			"Timer interval changed from %d to %d ticks",
			timer.currentInterval(), newInterval
		));
		timer.changeInterval(newInterval);
	}

	private static @NotNull ItemStack createIntervalItem(Timer timer) {
		final long intervalInSeconds = Math.max(timer.currentInterval(), 1) / 20L; // ticks to seconds
		final ItemStack item = new ItemStack(Material.ARROW, (int) intervalInSeconds);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("Current clock interval");
		meta.setLore(Arrays.asList("Initial: " + timer.initialInterval(), "Current: " + timer.currentInterval()));
		item.setItemMeta(meta);

		return item;
	}

	private void onClockItemRender(SlotRenderContext context) {
		final Timer timer = timerState.get(context);

		final ItemStack stack = new ItemStack(Material.CLOCK);
		final ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("Timer");
		meta.setLore(Arrays.asList(timer.isPaused() ? "Click to unpause" : "Click to pause"));
		stack.setItemMeta(meta);

		context.setItem(stack);
	}

	private void onClockItemClick(SlotClickContext context) {
		final Timer timer = timerState.get(context);
		if (timer.pause()) {
			context.getPlayer().sendMessage("Paused");
		} else {
			context.getPlayer().sendMessage("Unpaused");
		}
	}
}
