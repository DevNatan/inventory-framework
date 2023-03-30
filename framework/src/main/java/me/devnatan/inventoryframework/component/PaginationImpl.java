package me.devnatan.inventoryframework.component;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class PaginationImpl extends StateValue implements Pagination, InteractionHandler {

    private final @NotNull IFContext host;
    private final List<Component> components = new LinkedList<>();

    // --- User provided ---
    private final String layoutTarget;
    private final @NotNull Function<?, ?> sourceProvider;
    private final @NotNull BiConsumer<? extends ItemComponentBuilder<?>, ?> itemFactory;

    // --- Data ---
    private int page;

    public PaginationImpl(
            @NotNull State<?> state,
            @NotNull IFContext host,
            String layoutTarget,
            @NotNull Function<?, ?> sourceProvider,
            @NotNull BiConsumer<? extends ItemComponentBuilder<?>, ?> itemFactory) {
        super(state);
        this.host = host;
        this.layoutTarget = layoutTarget;
        this.sourceProvider = sourceProvider;
        this.itemFactory = itemFactory;
    }

    @Override
    public Object get() {
        return this;
    }

    @Override
    public @NotNull VirtualView getRoot() {
        return host;
    }

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
    public void clicked(@NotNull Component component, @NotNull IFSlotClickContext context) {}
}
