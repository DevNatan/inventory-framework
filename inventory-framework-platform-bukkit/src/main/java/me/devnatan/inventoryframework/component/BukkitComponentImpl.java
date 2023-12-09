package me.devnatan.inventoryframework.component;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;

public final class BukkitComponentImpl<COMPONENT_BUILDER extends BukkitComponentBuilder<COMPONENT_BUILDER>>
        extends PlatformComponent<Context, COMPONENT_BUILDER> {

    BukkitComponentImpl(
            String key,
            VirtualView root,
            Ref<Component> reference,
            Set<State<?>> watchingStates,
            Predicate<? extends IFContext> displayCondition,
            Consumer<? super IFComponentRenderContext> renderHandler,
            Consumer<? super IFComponentUpdateContext> updateHandler,
            Consumer<? super IFSlotClickContext> clickHandler,
            boolean cancelOnClick,
            boolean closeOnClick,
            boolean updateOnClick) {
        super(
                key,
                root,
                reference,
                watchingStates,
                displayCondition,
                renderHandler,
                updateHandler,
                clickHandler,
                cancelOnClick,
                closeOnClick,
                updateOnClick);
    }

    @Override
    public boolean isContainedWithin(int position) {
        return false;
    }

    @Override
    public boolean intersects(@NotNull Component other) {
        return false;
    }
}
