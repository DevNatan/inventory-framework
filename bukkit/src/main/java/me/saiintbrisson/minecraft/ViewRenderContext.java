package me.saiintbrisson.minecraft;

import lombok.Getter;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.bukkit.BukkitIFContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
public final class ViewRenderContext extends BaseViewContext implements IFRenderContext, BukkitIFContext {

    @NotNull
    private final Player player;

    ViewRenderContext(@NotNull ViewContext backingContext, @NotNull Player player) {
        super(backingContext.getRoot(), backingContext.getContainer());
        this.player = player;
    }
}
