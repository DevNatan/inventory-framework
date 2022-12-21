package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.saiintbrisson.minecraft.internal.platform.ViewContainer;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@ToString(callSuper = true)
class ViewContextImpl extends BaseViewContext {

    @Setter
    private boolean cancelled;

    ViewContextImpl(@NotNull final AbstractView root, @NotNull final ViewContainer container) {
        super(root, container);
    }
}
