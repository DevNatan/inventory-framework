package me.devnatan.inventoryframework.component;

import java.util.ArrayList;
import java.util.Collection;
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
    private final char layoutTarget;
    private final @NotNull Object sourceProvider;
    private final @NotNull BiConsumer<? extends ItemComponentBuilder<?>, ?> itemFactory;

    // --- Data ---
    private int currPageIndex;

    // --- Pagination ---
    private int pageSize;
    private int pagesCount;
    private List<?> currSource;
    private final boolean dynamic;
    private Function<? extends IFContext, Collection<?>> _srcFactory;

    @SuppressWarnings("unchecked")
    public PaginationImpl(
            @NotNull State<?> state,
            @NotNull IFContext host,
            char layoutTarget,
            @NotNull Object sourceProvider,
            @NotNull BiConsumer<? extends ItemComponentBuilder<?>, ?> itemFactory) {
        super(state);
        this.host = host;
        this.layoutTarget = layoutTarget;
        this.sourceProvider = sourceProvider;
        this.itemFactory = itemFactory;

        if (sourceProvider instanceof Collection) {
            currSource = new ArrayList<>((Collection<?>) sourceProvider);
            dynamic = false;
        } else if (sourceProvider instanceof Function) {
            _srcFactory = (Function<? extends IFContext, Collection<?>>) sourceProvider;
            dynamic = true;
        } else {
            throw new IllegalArgumentException(String.format(
                    "Unsupported pagination source provider: %s",
                    sourceProvider.getClass().getName()));
        }
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
        if (components.isEmpty()) return 0;

        final int first = components.get(0).getPosition();
        final int last = components.get(components.size() - 1).getPosition();

        return last - first;
    }

    @Override
    public @NotNull InteractionHandler getInteractionHandler() {
        return this;
    }

    @Override
    public void render(@NotNull IFSlotRenderContext context) {
        System.out.println("lets render pagination :)");
    }

    @Override
    public void updated(@NotNull IFSlotRenderContext context) {
        getComponents().forEach(child -> child.updated(context));
    }

    @Override
    public void clear(@NotNull IFContext context) {
        getComponents().forEach(child -> child.clear(context));
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
        return currPageIndex;
    }

    @Override
    public int nextPage() {
        return Math.min(pagesCount, currentPageIndex() + 1);
    }

    @Override
    public int nextPageIndex() {
        return Math.max(0, nextPage() - 1);
    }

    @Override
    public int lastPage() {
        return pagesCount;
    }

    @Override
    public int lastPageIndex() {
        return Math.max(0, pagesCount - 1);
    }

    @Override
    public boolean isFirstPage() {
        return currentPageIndex() == 0;
    }

    @Override
    public boolean isLastPage() {
        return !canAdvance();
    }

    @Override
    public boolean hasPage(int pageIndex) {
        if (pageIndex <= 0) return true;
        return pageIndex < getPagesCount();
    }

    @Override
    public void switchTo(int pageIndex) {
        if (!hasPage(pageIndex)) throw new IllegalArgumentException(String.format("Page %d not found", pageIndex));

        currPageIndex = pageIndex;
        // TODO trigger update and page switch
    }

    @Override
    public void advance() {
        if (!canAdvance()) return;
        switchTo(currentPageIndex() + 1);
    }

    @Override
    public boolean canAdvance() {
        return hasPage(currentPageIndex() + 1);
    }

    @Override
    public void back() {
        if (!canBack()) return;
        switchTo(currentPageIndex() - 1);
    }

    @Override
    public boolean canBack() {
        return hasPage(currentPageIndex() - 1);
    }

    @NotNull
    @Override
    public Iterator<Component> iterator() {
        return getComponents().iterator();
    }

    @Override
    public void clicked(@NotNull Component component, @NotNull IFSlotClickContext context) {}

    private List<?> getSourceOrThrow() {
        if (currSource != null) return currSource;
        if (isDynamic())
            throw new IllegalStateException("Dynamic pagination must set current source before try to access it");

        throw new IllegalStateException("Pagination source cannot be null for static pagination");
    }

    private List<?> getStaticPageContents(int index) {
        final List<?> src = getSourceOrThrow();
        if (src.isEmpty()) return Collections.emptyList();

        if (src.size() <= pageSize) return new ArrayList<>(src);
        if (index < 0 || index >= pagesCount)
            throw new IndexOutOfBoundsException(String.format(
                    "Page index must be between the range of 0 and %d. Given: %d", pagesCount - 1, index));

        final List<Object> contents = new LinkedList<>();
        final int base = index * pageSize;
        int until = base + pageSize;
        if (until > src.size()) until = src.size();

        for (int i = base; i < until; i++) contents.add(src.get(i));

        return contents;
    }
}
