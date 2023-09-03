package me.devnatan.inventoryframework.component;

import java.util.Collections;
import java.util.Set;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public class FakeComponent implements Component, InteractionHandler {

    private final VirtualView root;
    private int position = 0;
    public Object item = new Object();

    public FakeComponent(VirtualView root) {
        this.root = root;
    }

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
    public boolean intersects(@NotNull Component other) {
        return false;
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
    public void clear(@NotNull IFContext context) {
        ((IFRenderContext) context).getContainer().removeItem(position);
    }

    @Override
    public @UnmodifiableView Set<State<?>> getWatchingStates() {
        return Collections.emptySet();
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void setVisible(boolean visible) {}

    @Override
    public boolean isManagedExternally() {
        return false;
    }

    @Override
    public boolean shouldRender(IFContext context) {
        return false;
    }

    @Override
    public void clicked(@NotNull Component component, @NotNull IFSlotClickContext context) {}
}
