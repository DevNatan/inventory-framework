package me.saiintbrisson.minecraft.examples;

import me.saiintbrisson.minecraft.View;
import me.saiintbrisson.minecraft.ViewContext;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Renders a paper if the name of the player viewing the View is "JohnDoe". When the player clicks
 * on the paper the item is hidden or displayed.
 */
public final class ContextAwareConditionalRendering extends View {

    private static final String SHOULD_RENDER_KEY = "should-render";

    @Override
    protected void onInit() {
        setContainerSize(3);
        setContainerTitle("Conditional rendering");
        setCancelOnClick(true);
    }

    @Override
    protected void onRender(@NotNull ViewContext context) {
        final Player player = context.getPlayer();

        // item will render only if player name is "JohnDoe"
        if (!player.getName().equals("JohnDoe")) return;

        // NOTE the `context.firstSlot` and not just `firstSlot`
        //           ^^^^^^^
        context.firstSlot()
                .onRender(render -> render.setItem(shouldRenderPaper(render) ? new ItemStack(Material.PAPER) : null))
                .onClick(click -> click.set(SHOULD_RENDER_KEY, !shouldRenderPaper(click)))
                .onUpdate(update -> {
                    final boolean visible = shouldRenderPaper(update);
                    update.getPlayer().sendMessage("Paper is now " + (visible ? "visible" : "hidden"));
                });
    }

    private boolean shouldRenderPaper(ViewContext context) {
        return context.get(SHOULD_RENDER_KEY, () -> true);
    }
}
