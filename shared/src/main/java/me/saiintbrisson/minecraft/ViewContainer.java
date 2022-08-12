package me.saiintbrisson.minecraft;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

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

    void renderItem(int slot, Object item);

    void removeItem(int slot);

    boolean matchesItem(int slot, Object item, boolean exactly);

    Object convertItem(Object source);

    boolean isSupportedItem(Object item);

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

    @NotNull
    @Unmodifiable
    List<Viewer> getViewers();

    void open(@NotNull Viewer viewer);

    void close();

    void changeTitle(@Nullable String title);

    boolean isEntityContainer();
}
