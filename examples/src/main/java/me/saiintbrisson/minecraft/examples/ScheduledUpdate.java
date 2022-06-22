package me.saiintbrisson.minecraft.examples;

import me.saiintbrisson.minecraft.View;
import me.saiintbrisson.minecraft.ViewContext;
import org.jetbrains.annotations.NotNull;

/**
 * Updates current view every 5 ticks.
 */
public final class ScheduledUpdate extends View {

	public ScheduledUpdate() {
		super(3, "Scooter Turtle");

		// 5 ticks
		scheduleUpdate(20L * 5);
	}

	@Override
	protected void onUpdate(@NotNull ViewContext context) {
		context.getPlayer().sendMessage("Updated.");
	}

}
