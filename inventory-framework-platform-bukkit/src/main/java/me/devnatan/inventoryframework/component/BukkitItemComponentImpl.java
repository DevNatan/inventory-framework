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

    private int position;
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
        this.position = position;
        this.stack = itemStack;
		setHandle(new BukkitItemComponentImplHandle());
    }

    @Override
    public int getPosition() {
        return position;
    }

    void setPosition(int position) {
        this.position = position;
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
        if (other == this) return true;
        if (other instanceof ItemComponent) return getPosition() == ((ItemComponent) other).getPosition();

        return other.intersects(this);
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
        return "BukkitItemComponentImpl{" + "position=" + position + ", itemStack=" + stack + "} " + super.toString();
    }
}
