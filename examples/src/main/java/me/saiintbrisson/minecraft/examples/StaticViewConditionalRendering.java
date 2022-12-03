package me.saiintbrisson.minecraft.examples;

import me.saiintbrisson.minecraft.View;
import me.saiintbrisson.minecraft.ViewContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * When the player clicks on the paper the item is hidden or displayed.
 */
public final class StaticViewConditionalRendering extends View {

    private static final String SHOULD_RENDER_KEY = "should-render";

    @Override
    protected void onInit() {
        setContainerSize(3);
        setContainerTitle("Conditional rendering");
        setCancelOnClick(true);

        firstSlot()
                .onRender(render -> render.setItem(shouldRenderPaper(render) ? new ItemStack(Material.PAPER) : null))
                .onClick(click -> click.set(SHOULD_RENDER_KEY, !shouldRenderPaper(click)));
    }

    private boolean shouldRenderPaper(ViewContext context) {
        return context.get(SHOULD_RENDER_KEY, () -> true);
    }
}
