package me.devnatan.inventoryframework.component;

import java.util.Objects;
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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * {@link ItemComponent} implementation for Bukkit platform.
 */
public final class BukkitItemComponentImpl extends PlatformComponent implements ItemComponent {

    private final ItemStack stack;

    BukkitItemComponentImpl(
            int position,
            ItemStack itemStack,
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
                key == null ? String.valueOf(itemStack.hashCode()) : key,
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
        this.stack = itemStack;
        setHandle(new BukkitItemComponentImplHandle());
    }

    public ItemStack getItemStack() {
        return stack;
    }

    @Override
    public Object getPlatformItem() {
        return getItemStack();
    }

    @Override
    public boolean intersects(@NotNull Component other) {
        if (other instanceof ItemComponent) return getPosition() == other.getPosition();
        else return other.intersects(this);
    }

    @Override
    public boolean isContainedWithin(int position) {
        return getPosition() == position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BukkitItemComponentImpl that = (BukkitItemComponentImpl) o;
        return getPosition() == that.getPosition() && Objects.equals(getItemStack(), that.getItemStack());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPosition(), getItemStack());
    }

    @Override
    public String toString() {
        return "BukkitItemComponentImpl{" + "itemStack=" + stack + "} " + super.toString();
    }
}
