package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.IFComponentContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ViewContainer {

    /**
     * The type of this container.
     *
     * @return The type of this container.
     */
    @NotNull
    ViewType getType();

    /**
     * Returns the position of the first slot of this container.
     *
     * @return The first slot of this container.
     */
    int getFirstSlot();

    /**
     * Returns the position of the last slot of this container.
     *
     * @return The last slot of this container.
     */
    int getLastSlot();

    /**
     * Returns whether a container slot is filled by an item.
     *
     * @param slot The item slot.
     * @return Whether there is an item in the specified slot.
     */
    boolean hasItem(int slot);

    void removeItem(int slot);

    void renderItem(int slot, Object platformItem);

    /**
     * The number of slots in this container.
     *
     * @return The number of slots in this container.
     */
    int getSize();

    /**
     * The number of slot indexes in this container (available or not).
     *
     * @return All slot indexes in this container.
     */
    int getSlotsCount();

    /**
     * The amount of horizontal lines present in the container.
     *
     * @return The amount of horizontal lines present in the container.
     */
    int getRowsCount();

    /**
     * The amount of vertical lines present in the container.
     *
     * @return The amount of vertical lines present in the container.
     */
    int getColumnsCount();

    void open(@NotNull Viewer viewer);

    void close();

    void close(@NotNull Viewer viewer);

    void changeTitle(@Nullable String title, @NotNull Viewer target);

    boolean isEntityContainer();

    boolean isProxied();

    ViewContainer unproxied();

    boolean isExternal();

    static ViewContainer from(VirtualView view) {
        if (view instanceof IFRenderContext) return ((IFRenderContext) view).getContainer();
        if (view instanceof IFSlotContext) return ((IFSlotContext) view).getContainer();
        if (view instanceof IFComponentContext)
            return ((IFComponentContext) view).getComponent().getContainer();
        if (view instanceof Component) return ((Component) view).getContainer();

        throw new IllegalArgumentException(
                "Unable to get ViewContainer from root: " + view.getClass().getName());
    }
}
