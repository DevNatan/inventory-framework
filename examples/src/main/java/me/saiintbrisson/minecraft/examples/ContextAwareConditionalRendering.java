package me.saiintbrisson.minecraft.examples;

import me.devnatan.inventoryframework.config.ViewConfig;
import me.devnatan.inventoryframework.state.State;
import me.saiintbrisson.minecraft.View;
import me.saiintbrisson.minecraft.ViewContext;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Renders a paper if the name of the player viewing the View is "JohnDoe". When the player clicks
 * on the paper the item is hidden or displayed.
 */
public final class ContextAwareConditionalRendering extends View {

	private final State<Boolean> paperVisibilityState = state(() -> false);

	@Override
	protected ViewConfig configure() {
		// TODO cancel on click
		return ViewConfig.create().size(3).title("Conditional rendering");
	}

	@Override
    protected void onRender(ViewContext context) {
        final Player player = context.getPlayer();

        // item will render only if player name is "JohnDoe"
        if (!player.getName().equals("JohnDoe"))
			return;

        // NOTE the `context.firstSlot` and not just `firstSlot`
        //           ^^^^^^^
        context.firstSlot()
                .rendered(() -> isPaperVisible() ? new ItemStack(Material.PAPER) : null)
                .onClick(click -> paperVisibilityState.update(click, !isPaperVisible()))
                .onUpdate(update -> {
                    final boolean visible = isPaperVisible();
                    update.getPlayer().sendMessage("Paper is now " + (visible ? "visible" : "hidden"));
                });
    }

	private boolean isPaperVisible() {
		return paperVisibilityState.get();
	}

}
