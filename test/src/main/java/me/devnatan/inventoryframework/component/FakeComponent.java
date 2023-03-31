package me.devnatan.inventoryframework.component;

import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class FakeComponent implements Component, InteractionHandler {

    private final VirtualView root;
    private int position = 0;
    private boolean markedForRemoval = false;
    public Object item = new Object();

    public FakeComponent(VirtualView root, int position) {
        this(root);
        this.position = position;
    }

    @Override
    public @NotNull VirtualView getRoot() {
        return root;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public boolean isContainedWithin(int position) {
        return this.position == position;
    }

    @Override
    public @NotNull InteractionHandler getInteractionHandler() {
        return this;
    }

    @Override
    public void render(@NotNull IFSlotRenderContext context) {
        context.getContainer().renderItem(position, item);
    }

    @Override
    public void updated(@NotNull IFSlotRenderContext context) {}

	@Override
	public boolean shouldBeUpdated() {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
    public void clear(@NotNull IFContext context) {
        context.getContainer().removeItem(position);
    }

    @Override
    public void clicked(@NotNull Component component, @NotNull IFSlotClickContext context) {}
}
