package me.devnatan.inventoryframework.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

// TODO add "key" to child pagination components and check if it needs to be updated based on it
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class PaginationImpl extends StateValue implements Pagination {

    @EqualsAndHashCode.Exclude
    private final List<Component> components = new LinkedList<>();

    // --- State ---
    private final @NotNull IFContext host;

    // --- User provided ---
    private final char layoutTarget;
    private final @NotNull Object sourceProvider;
    private final @NotNull PaginationElementFactory<IFContext, Object> elementFactory;
    private final BiConsumer<IFContext, Pagination> pageSwitchHandler;

    // --- Internal ---
    private int currPageIndex;
    private int pageSize = -1;
    private final boolean dynamic;
    private boolean pageWasChanged;

    /**
     * Final source factory for dynamic pagination converted from {@link #sourceProvider}.
     */
    private Function<IFContext, List<?>> _srcFactory;

    /**
     * Current page source. Only {@code null} before first pagination render.
     */
    private List<?> currSource;

    public PaginationImpl(
            @NotNull State<?> state,
            @NotNull IFContext host,
            char layoutTarget,
            @NotNull Object sourceProvider,
            @NotNull PaginationElementFactory<IFContext, Object> elementFactory,
            BiConsumer<IFContext, Pagination> pageSwitchHandler) {
        super(state);
        this.host = host;
        this.layoutTarget = layoutTarget;
        this.sourceProvider = sourceProvider;
        this.elementFactory = elementFactory;
        this.pageSwitchHandler = pageSwitchHandler;
        this.currSource = convertSourceProvider();
        this.dynamic = !(sourceProvider instanceof Collection);
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
    public void render(@NotNull IFSlotRenderContext context) {
        final IFRenderContext renderContext = (IFRenderContext) context.getParent();
        if (renderContext.getConfig().getLayout() != null) renderLayeredPagination(renderContext);
        else renderUnconstrainedPagination(renderContext);

        getComponents().forEach(child -> child.render(context));
    }

    @Override
    public void updated(@NotNull IFSlotRenderContext context) {
        // If page was changed all components will be removed, so don't trigger update on them
        if (pageWasChanged) {
            clear(context);
            pageWasChanged = false;
            return;
        }

        getComponents().forEach(child -> child.updated(context));
    }

    @Override
    public boolean shouldBeUpdated() {
        return pageWasChanged;
    }

    @Override
    public void clear(@NotNull IFContext context) {
        // Only remove components if page was changed to not make the clear inconsistent
        if (pageWasChanged) {
            final Iterator<Component> childIterator = components.iterator();
            while (childIterator.hasNext()) {
                Component child = childIterator.next();
                child.clear(context);
                childIterator.remove();
            }
            return;
        }

        getComponents().forEach(child -> child.clear(context));
    }

    @Override
    public @UnmodifiableView Set<State<?>> getWatchingStates() {
        return Collections.emptySet();
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
    public InteractionHandler getInteractionHandler() {
        return null;
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
        return Math.min(getPagesCount(), currentPageIndex() + 1);
    }

    @Override
    public int nextPageIndex() {
        return Math.max(0, nextPage() - 1);
    }

    @Override
    public int lastPage() {
        return getPagesCount();
    }

    @Override
    public int lastPageIndex() {
        return Math.max(0, getPagesCount() - 1);
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
        if (pageIndex < 0) return false;
        if (pageIndex == 0) return true;

        return pageIndex < getPagesCount();
    }

    @Override
    public void switchTo(int pageIndex) {
        if (!hasPage(pageIndex))
            throw new IndexOutOfBoundsException(
                    String.format("Page index not found (%d > %d)", pageIndex, getPagesCount()));

        if (pageSwitchHandler != null) pageSwitchHandler.accept(host, this);
        currPageIndex = pageIndex;
        pageWasChanged = true;
        host.updateRoot();
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

    /**
     * The pagination source.
     *
     * @return The current pagination source.
     * @throws IllegalStateException If the current source wasn't yet defined.
     */
    private List<?> getSourceOrThrow() {
        if (currSource != null) return currSource;
        if (isDynamic()) {
            currSource = _srcFactory.apply(host);
            if (currSource == null)
                throw new IllegalStateException("Dynamic pagination must set current source before try to access it");
            return currSource;
        }

        throw new IllegalStateException("Pagination source cannot be null for static pagination");
    }

    /**
     * The number of pages.
     *
     * @return The number of pages based on the {@link #getSourceOrThrow() current source}.
     */
    // TODO needs caching
    private int getPagesCount() {
        List<?> source = getSourceOrThrow();
        return (int) Math.ceil((double) source.size() / getPageSize());
    }

    /**
     * The current page size.
     *
     * @return Number of available elements position for pagination in the current page.
     */
    public int getPageSize() {
        if (pageSize == -1) throw new IllegalStateException("Page size need to be updated before try to get it");

        return pageSize;
    }

    /**
     * Get all elements in a given page index based on the {@link #getSourceOrThrow() current source}.
     *
     * @param index The page index.
     * @return All elements in a page.
     * @throws IndexOutOfBoundsException If the specified index is {@code < 0} or
     *                                   exceeds the {@link #getPagesCount() pages count}.
     */
    private List<?> getPageContents(int index) {
        final List<?> src = getSourceOrThrow();
        if (src.isEmpty()) return Collections.emptyList();

        if (src.size() <= pageSize) return new ArrayList<>(src);
        if (index < 0 || index > getPagesCount())
            throw new IndexOutOfBoundsException(String.format(
                    "Page index must be between the range of 0 and %d. Given: %d", getPagesCount() - 1, index));

        final List<Object> contents = new LinkedList<>();
        final int base = index * pageSize;
        int until = base + pageSize;
        if (until > src.size()) until = src.size();

        for (int i = base; i < until; i++) contents.add(src.get(i));

        return contents;
    }

    /**
     * Renders pagination using container boundaries, no constraints.
     * <p>
     * The position of the first paged item must be the first slot in the container, the last
     * position must be the last slot in the container, and {@link #pageSize} on the current page
     * must be the size of the container.
     *
     * @param context The render context.
     */
    private void renderUnconstrainedPagination(@NotNull IFRenderContext context) {
        final ViewContainer container = context.getContainer();
        pageSize = container.getSize();

        final List<?> elements = getPageContents(currPageIndex);
        final int lastSlot = container.getLastSlot();

        for (int i = container.getFirstSlot(); i < Math.min(lastSlot + 1, elements.size()); i++) {
            final Object value = elements.get(i);
            final ComponentFactory factory = elementFactory.create(context, i, i, value);
            components.add(factory.create());
        }

        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Renders the pagination using the layout positions defined from the {@link #getLayoutTarget() target layout character}.
     * <p>
     * The first position, last position and number of items on the page must be exactly the same as
     * the layout.
     *
     * @param context The render context.
     */
    private void renderLayeredPagination(@NotNull IFRenderContext context) {
        final Optional<LayoutSlot> layoutSlotOptional = context.getLayoutSlots().stream()
                .filter(layoutSlot -> layoutSlot.getCharacter() == getLayoutTarget())
                .findFirst();

        if (!layoutSlotOptional.isPresent())
            // TODO more detailed error message
            throw new IllegalArgumentException(String.format("Layout slot target not found: %c", getLayoutTarget()));

        final LayoutSlot layoutSlot = layoutSlotOptional.get();
        pageSize = layoutSlot.getPositions().size();

        final List<?> elements = getPageContents(currPageIndex);
        final int elementsLen = elements.size();
        int iterationIndex = 0;
        for (final int position : layoutSlot.getPositions()) {
            final Object value = elements.get(iterationIndex++);
            final ComponentFactory factory = elementFactory.create(context, iterationIndex, position, value);
            components.add(factory.create());

            if (iterationIndex == elementsLen) break;
        }
    }

    /**
     * Converts the user provided source provider to a valid static source.
     * <p>
     * Also, assigns the {@link #_srcFactory} value if the provided source has dynamic capabilities.
     *
     * @return The current source.
     * @throws IllegalArgumentException If the provided source is not supported.
     */
    @SuppressWarnings("unchecked")
    private List<?> convertSourceProvider() {
        if (sourceProvider instanceof Collection) {
            currSource = new ArrayList<>((Collection<?>) sourceProvider);
        } else if (sourceProvider instanceof Function) {
            _srcFactory = (Function<IFContext, List<?>>) sourceProvider;
        } else if (sourceProvider instanceof Supplier) {
            _srcFactory = $ -> ((Supplier<List<?>>) sourceProvider).get();
        } else {
            throw new IllegalArgumentException(String.format(
                    "Unsupported pagination source provider: %s",
                    sourceProvider.getClass().getName()));
        }

        return currSource;
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
