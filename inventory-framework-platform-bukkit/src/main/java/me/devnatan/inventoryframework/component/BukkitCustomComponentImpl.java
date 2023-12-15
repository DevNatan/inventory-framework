package me.devnatan.inventoryframework.component;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;

public final class BukkitCustomComponentImpl extends PlatformComponent {

    BukkitCustomComponentImpl(
            int position,
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
            boolean updateOnClick,
            boolean isSelfManaged) {
        super(
                position,
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
                updateOnClick,
                isSelfManaged);
        setHandle(new BukkitItemComponentImplHandle());
    }

    @Override
    public boolean isContainedWithin(int position) {
        return getPosition() == position;
    }

    @Override
    public boolean intersects(@NotNull Component other) {
        throw new UnsupportedOperationException("Missing #intersects(Component) implementation");
    }

    @Override
    public String toString() {
        return "BukkitComponentImpl{} " + super.toString();
    }
}
