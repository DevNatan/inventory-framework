package me.saiintbrisson.minecraft.examples;

import me.saiintbrisson.minecraft.View;
import me.saiintbrisson.minecraft.ViewContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class RenderingFunction extends View {

	public RenderingFunction() {
		super(3, "Scooter Turtle");
	}

	@Override
	protected void onRender(@NotNull ViewContext context) {
		final Player player = context.getPlayer();
		player.sendMessage("Hi, " + player.getName() + "!");
	}
}
