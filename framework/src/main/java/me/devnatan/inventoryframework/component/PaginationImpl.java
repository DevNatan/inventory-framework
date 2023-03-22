package me.devnatan.inventoryframework.component;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.Data;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import me.devnatan.inventoryframework.state.StateHost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

@Data
public final class PaginationImpl implements Pagination, InteractionHandler {

    private final @NotNull RootView root;
    private final @Nullable String layoutTarget;
    private final List<Component> components = new LinkedList<>();
    private boolean markedForRemoval;

    // --- User provided ---
    private final @NotNull Function<?, ?> sourceProvider;
    private final @NotNull BiConsumer<? extends ItemComponentBuilder<?>, Object> itemFactory;

    // --- Data ---
    private int page;

    @Override
    public int getPosition() {
        return components.isEmpty() ? 0 : components.get(0).getPosition();
    }

    @Override
    public @NotNull InteractionHandler getInteractionHandler() {
        return this;
    }

    @Override
    public void render(@NotNull IFSlotRenderContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void updated(@NotNull IFSlotRenderContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void clear(@NotNull IFContext context) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public @UnmodifiableView List<Component> getComponents() {
        return Collections.unmodifiableList(components);
    }

    @Override
    public boolean isContainedWithin(int position) {
        for (final Component component : getComponents()) {
            if (component.isContainedWithin(position)) return true;
        }
        return false;
    }

    @Override
    public int currentPage() {
        return currentPageIndex() + 1;
    }

    @Override
    public int currentPageIndex() {
        return page;
    }

    @Override
    public int lastPage() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public int lastPageIndex() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean isFirstPage() {
        return currentPageIndex() == 0;
    }

    @Override
    public boolean isLastPage() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean hasPreviousPage() {
        return currentPageIndex() > 0;
    }

    @Override
    public boolean hasNextPage() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void advance() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean canAdvance() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void back() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean canBack() {
        throw new UnsupportedOperationException("TODO");
    }

    @NotNull
    @Override
    public Iterator<Component> iterator() {
        return getComponents().iterator();
    }

    @Override
    public void attached(long id, @NotNull StateHost holder) {}

    @Override
    public void clicked(@NotNull Component component, @NotNull IFSlotClickContext context) {}

    @Override
    public boolean isLayoutComponent() {
        return false;
    }
}
