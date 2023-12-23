package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.context.CloseContext;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFContext;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BukkitCloseCancellationInterceptor implements PipelineInterceptor<IFContext> {

    @SuppressWarnings("ConstantValue")
    @Override
    public void intercept(PipelineContext<IFContext> pipeline, IFContext subject) {
        if (!(subject instanceof IFCloseContext)) return;

        final CloseContext context = (CloseContext) subject;
        if (!context.isCancelled()) return;

        final Player player = context.getPlayer();
        final ItemStack cursor = player.getItemOnCursor();

        context.getRoot().nextTick(() -> context.getViewer().open(context.getContainer()));

        // suppress cursor null check since it can be null in legacy versions
        if ((cursor != null) && cursor.getType() != Material.AIR) player.setItemOnCursor(cursor);
    }
}
