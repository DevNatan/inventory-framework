package me.devnatan.inventoryframework.component;

import java.util.Objects;
import me.devnatan.inventoryframework.IFDebug;
import me.devnatan.inventoryframework.context.IFComponentContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public abstract class ComponentHandle implements PipelineInterceptor<IFComponentContext> {

    private Component component;

    /**
     * The component that this handle is linked to.
     *
     * @return The component that this handle is linked to.
     */
    public final @NotNull Component getComponent() {
        if (component == null) throw new IllegalStateException("Component is not accessible");

        return component;
    }

    /**
     * Links this handle to a component.
     *
     * @param component The component to link to.
     */
    @ApiStatus.Internal
    public void setComponent(Component component) {
        if (this.component != null) throw new IllegalStateException("Cannot reassign a ComponentHandle component");

        if (!Objects.equals(component.getHandle(), this))
            throw new IllegalArgumentException("Call #setHandle(this) before calling #setComponent(...)");

        this.component = component;
        IFDebug.debug(
                "Handle %s assigned to component %s",
                getClass().getName(), component.getClass().getName());
    }

    /**
     * Checks if this component is in a specific position.
     *
     * @param position The position.
     * @return If this component is contained in the given position.
     */
    public boolean isContainedWithin(int position) {
        return component.getPosition() == position;
    }

    /**
     * If this component are intersects with other component.
     *
     * @param other The other component.
     * @return If both this and other component intersects in area.
     */
    public boolean intersects(@NotNull Component other) {
        return isContainedWithin(other.getPosition());
    }
}
