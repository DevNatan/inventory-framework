package me.saiintbrisson.minecraft.examples;

import me.saiintbrisson.minecraft.OpenViewContext;
import me.saiintbrisson.minecraft.View;
import org.jetbrains.annotations.NotNull;

public final class DynamicEmptyView extends View {

	@Override
	protected void onOpen(@NotNull OpenViewContext context) {
		context.setInventorySize(3);
		context.setInventoryTitle("Scooter Turtle");
	}

}
