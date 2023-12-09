package me.devnatan.inventoryframework;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProxyContainer implements ViewContainer {

    private final ViewContainer top;
    private final ViewContainer bottom;

    public ProxyContainer(ViewContainer top, ViewContainer bottom) {
        this.top = top;
        this.bottom = bottom;
    }

    @Override
    public String getTitle() {
        return top.getTitle();
    }

    @Override
    public @NotNull ViewType getType() {
        return top.getType();
    }

    @Override
    public int getFirstSlot() {
        return top.getFirstSlot();
    }

    @Override
    public int getLastSlot() {
        return bottom.getLastSlot();
    }

    @Override
    public boolean hasItem(int slot) {
        return top.hasItem(slot) || bottom.hasItem(slot);
    }

    @Override
    public void removeItem(int slot) {
        if (slot <= top.getLastSlot()) top.removeItem(slot);
        if (slot <= bottom.getLastSlot()) bottom.removeItem(slot);
    }

    @Override
    public int getSize() {
        return top.getSize() + bottom.getSize();
    }

    @Override
    public int getSlotsCount() {
        return top.getSlotsCount() + bottom.getSlotsCount();
    }

    @Override
    public int getRowsCount() {
        return top.getRowsCount();
    }

    @Override
    public int getColumnsCount() {
        return top.getColumnsCount();
    }

    @Override
    public void open(@NotNull Viewer viewer) {
        top.open(viewer);
    }

    @Override
    public void close() {
        top.close();
    }

    @Override
    public void close(@NotNull Viewer viewer) {
        top.close(viewer);
    }

    @Override
    public void changeTitle(@Nullable String title, @NotNull Viewer target) {
        top.changeTitle(title, target);
    }

    @Override
    public boolean isEntityContainer() {
        return false;
    }

    @Override
    public boolean isProxied() {
        return true;
    }
}
