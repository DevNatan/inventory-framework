package me.devnatan.inventoryframework.example;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.context.SlotRenderContext;
import me.devnatan.inventoryframework.state.MutableIntState;
import me.devnatan.inventoryframework.state.MutableState;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

enum Direction {
	UP, DOWN, LEFT, RIGHT,
}

public class SnakeGameView extends View {

	private final MutableIntState applePosition = mutableState(0);
	private final MutableIntState applesEaten = mutableState(0);
	private final MutableState<Direction> directionState = mutableState(Direction.DOWN);
	private final MutableState<List<Integer>> snakePosition = mutableState(new ArrayList<Integer>() {{
		add(0);
		add(9);
		add(18);
	}});

	@Override
	public void onInit(@NotNull ViewConfigBuilder config) {
		config.cancelOnClick()
			.scheduleUpdate(20L)
			.title(createTitle(0))
			.layout(
				"XXXXXXXXX",
				"XOOOOOOOX",
				"XOOOOOOOX",
				"XOOOOOOOX",
				"XOOOOOOOX",
				"XXUDXLRXX"
			);
	}

	@Override
	public void onFirstRender(@NotNull RenderContext render) {
		render.layoutSlot('X', new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
		render.layoutSlot('O').onRender(slotRender -> slotRender.setItem(createSnakeItemAt(slotRender)));

		render.unsetSlot().onRender(appleRender -> {
			appleRender.setItem(new ItemStack(Material.APPLE));
			appleRender.setSlot(applePosition.get(appleRender));
		});

		for (final Direction direction : Direction.values())
			renderDirectionItem(direction, render);
	}

	@Override
	public void onUpdate(@NotNull Context update) {
		final RenderContext render = (RenderContext) update.getViewer().getActiveContext();
		if (isInSnakePosition(applePosition.get(render), update))
			onAppleEaten(render);
		onSnakePositionUpdate(render);
	}

	private void renderDirectionItem(Direction direction, RenderContext render) {
		final ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		final SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setDisplayName(String.format("Click to go %s", direction.name()));
		item.setItemMeta(meta);
		render.layoutSlot(direction.name().charAt(0), item)
			.onClick(click -> {
				directionState.set(direction, click);
				click.getPlayer().playSound(click.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
			});
	}

	private ItemStack createSnakeItemAt(SlotRenderContext context) {
		final int slot = context.getSlot();
		final ItemStack item;

		if (isInSnakePosition(slot, context)) {
			if (slot == getSnakeHeadPosition(context)) {
				// is head
				item = new ItemStack(Material.PINK_DYE);
			} else if (slot == getSnakeTailPosition(context)) {
				// is tail
				item = new ItemStack(Material.BLACK_DYE);
			} else {
				// is body
				item = new ItemStack(Material.GRAY_DYE);
			}
		} else {
			// renders nothing
			item = null;
		}
		return item;
	}

	private void onAppleEaten(RenderContext context) {
		int apples = applesEaten.increment(context);
		context.updateTitleForPlayer(createTitle(apples));
		updateApplePosition(context);
	}

	private void onSnakePositionUpdate(RenderContext context) {
		final Direction direction = directionState.get(context);
		final int positionDiff = getPositionDifference(direction);
		final int headPosition = getSnakeHeadPosition(context);
		final int tailPosition = getSnakeTailPosition(context);


	}

	private int getPositionDifference(Direction direction) {
		switch (direction) {
			case UP: return -9;
			case DOWN: return +9;
			case LEFT: return -1;
			case RIGHT: return +1;
		}
        return 0;
    }

	private void updateApplePosition(RenderContext context) {
		do {
			int position = ThreadLocalRandom.current().nextInt(0, context.getContainer().getLastSlot());
			applePosition.set(position, context);
		} while (!isInSnakePosition(applePosition.get(context), context));
	}

	private String createTitle(int applesEaten) {
		return String.format("Jogo do Cobrudo - %d maçãs", applesEaten);
	}

	private boolean isInSnakePosition(int position, Context context) {
		return snakePosition.get(context).contains(position);
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	private int getSnakeHeadPosition(Context context) {
		return snakePosition.get(context).stream().max(Comparator.naturalOrder()).get();
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	private int getSnakeTailPosition(Context context) {
		return snakePosition.get(context).stream().min(Comparator.naturalOrder()).get();
	}
}
