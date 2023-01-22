package me.saiintbrisson.minecraft;

import lombok.Getter;
import me.devnatan.inventoryframework.ViewContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
public final class ViewRenderContext extends BaseViewContext implements IFRenderContext, IFContext {

    @NotNull
    private final Player player;

    ViewRenderContext(@NotNull ViewContext backingContext, @NotNull Player player) {
        super(backingContext.getRoot(), backingContext.getContainer());
        this.player = player;
    }
}
