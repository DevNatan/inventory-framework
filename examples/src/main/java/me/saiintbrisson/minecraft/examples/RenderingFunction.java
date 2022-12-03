package me.saiintbrisson.minecraft.examples;

import me.saiintbrisson.minecraft.View;
import me.saiintbrisson.minecraft.ViewContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class RenderingFunction extends View {

    @Override
    protected void onInit() {
        setContainerSize(3);
        setContainerTitle("Scooter Turtle");
    }

    @Override
    protected void onRender(@NotNull ViewContext context) {
        final Player player = context.getPlayer();
        player.sendMessage("Hi, " + player.getName() + "!");
    }
}
