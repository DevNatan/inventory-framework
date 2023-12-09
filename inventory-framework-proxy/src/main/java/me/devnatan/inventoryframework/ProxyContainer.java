package me.devnatan.inventoryframework;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ProxyContainer implements ViewContainer {

    private final ViewContainer top;
    private final ViewContainer bottom;

    public ProxyContainer(ViewContainer top, ViewContainer bottom) {
        this.top = top;
        this.bottom = bottom;
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
        if (slot <= top.getLastSlot())
			top.removeItem(slot);
        else
			bottom.removeItem(slot);
    }

	@Override
	public void renderItem(int slot, Object platformItem) {
		if (slot <= top.getLastSlot())
			top.renderItem(slot, platformItem);
		else
			bottom.renderItem(slot, platformItem);
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
        return top.getRowsCount() + bottom.getRowsCount();
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

	@Override
	public ViewContainer unproxied() {
		return top;
	}

	@Override
	public boolean isExternal() {
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProxyContainer that = (ProxyContainer) o;
		return Objects.equals(top, that.top) && Objects.equals(bottom, that.bottom);
	}

	@Override
	public int hashCode() {
		return Objects.hash(top, bottom);
	}

	@Override
	public String toString() {
		return "ProxyContainer{" +
			"top=" + top +
			", bottom=" + bottom +
			'}';
	}
}
