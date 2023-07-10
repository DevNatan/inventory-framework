package me.devnatan.inventoryframework.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateValue;
import me.devnatan.inventoryframework.state.StateValueHost;
import me.devnatan.inventoryframework.state.StateWatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.jetbrains.annotations.VisibleForTesting;

// TODO add "key" to child pagination components and check if it needs to be updated based on it
@VisibleForTesting
public class PaginationImpl extends StateValue implements Pagination, InteractionHandler, StateWatcher {

    private final List<Component> components = new LinkedList<>();
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

    public @NotNull IFContext getHost() {
        return host;
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
    public void stateValueInitialized(@NotNull StateValueHost host, @NotNull StateValue value, Object initialValue) {
        init((IFRenderContext) host);
    }

    @Override
    public void stateRegistered(@NotNull State<?> state, Object caller) {}

    @Override
    public void stateUnregistered(@NotNull State<?> state, Object caller) {}

    @Override
    public void stateValueGet(
            @NotNull State<?> state,
            @NotNull StateValueHost host,
            @NotNull StateValue internalValue,
            Object rawValue) {}

    @Override
    public void stateValueSet(
            @NotNull StateValueHost host, @NotNull StateValue value, Object rawOldValue, Object rawNewValue) {}

    @Override
    public int getPosition() {
        final List<Component> components = getComponentsInternal();
        if (components.isEmpty()) return 0;

        final int first = components.get(0).getPosition();
        final int last = components.get(components.size() - 1).getPosition();

        return last - first;
    }

    private void init(IFRenderContext context) {
        if (context.getConfig().getLayout() != null) registerComponentsForLayeredPagination(context);
        else registerComponentsForUnconstrainedPagination(context);
    }

    @Override
    public void render(@NotNull IFSlotRenderContext context) {
        getComponentsInternal().forEach(child -> child.render(context));
    }

    @Override
    public void updated(@NotNull IFSlotRenderContext context) {
        // If page was changed all components will be removed, so don't trigger update on them
        if (pageWasChanged) {
            clear(context);
            init((IFRenderContext) context.getParent());
            pageWasChanged = false;
            return;
        }

        getComponentsInternal().forEach(child -> child.updated(context));
    }

    @Override
    public boolean shouldBeUpdated() {
        return pageWasChanged;
    }

    @Override
    public void clear(@NotNull IFContext context) {
        // Only clear components if page was changed to not make the clear operation inconsistent
        if (!pageWasChanged) {
            getComponentsInternal().forEach(child -> child.clear(context));
            return;
        }

        final Iterator<Component> childIterator = getComponentsInternal().iterator();
        while (childIterator.hasNext()) {
            Component child = childIterator.next();
            child.clear(context);
            childIterator.remove();
        }
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
        for (final Component component : getComponentsInternal()) {
            if (component.isContainedWithin(position)) return true;
        }
        return false;
    }

    @Override
    public InteractionHandler getInteractionHandler() {
        return this;
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
        host.update();
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

    @Override
    public char getLayoutTarget() {
        return layoutTarget;
    }

    @Override
    public boolean isDynamic() {
        return dynamic;
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
    private void registerComponentsForUnconstrainedPagination(@NotNull IFRenderContext context) {
        final ViewContainer container = context.getContainer();
        pageSize = container.getSize();

        final List<?> elements = getPageContents(currPageIndex);
        final int lastSlot = container.getLastSlot();

        for (int i = container.getFirstSlot(); i < Math.min(lastSlot + 1, elements.size()); i++) {
            final Object value = elements.get(i);
            final ComponentFactory factory = elementFactory.create(context, i, i, value);
            getComponentsInternal().add(factory.create());
        }
    }

    /**
     * Renders the pagination using the layout positions defined from the {@link #getLayoutTarget() target layout character}.
     * <p>
     * The first position, last position and number of items on the page must be exactly the same as
     * the layout.
     *
     * @param context The render context.
     */
    private void registerComponentsForLayeredPagination(@NotNull IFRenderContext context) {
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
            getComponentsInternal().add(factory.create());

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
        return !getComponentsInternal().isEmpty();
    }

    @Override
    public void clicked(@NotNull Component component, @NotNull IFSlotClickContext context) {
        final List<Component> components = getComponentsInternal();
        if (components.isEmpty()) return;
        if (components.size() == 1) {
            final Component child = components.get(0);
            if (child.getInteractionHandler() != null && child.getInteractionHandler() != null) {
                child.getInteractionHandler().clicked(component, context);
            }
            return;
        }

        for (final Component child : components) {
            if (child.getInteractionHandler() == null) continue;
            if (child.isContainedWithin(context.getClickedSlot())) {
                child.getInteractionHandler().clicked(component, context);
                break;
            }
        }
    }

    @VisibleForTesting
    List<Component> getComponentsInternal() {
        return components;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaginationImpl that = (PaginationImpl) o;
        return getLayoutTarget() == that.getLayoutTarget()
                && currPageIndex == that.currPageIndex
                && getPageSize() == that.getPageSize()
                && isDynamic() == that.isDynamic()
                && pageWasChanged == that.pageWasChanged
                && Objects.equals(sourceProvider, that.sourceProvider)
                && Objects.equals(pageSwitchHandler, that.pageSwitchHandler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getLayoutTarget(),
                sourceProvider,
                pageSwitchHandler,
                currPageIndex,
                getPageSize(),
                isDynamic(),
                pageWasChanged);
    }

    @Override
    public String toString() {
        return "PaginationImpl{" + ", host="
                + host + ", layoutTarget="
                + layoutTarget + ", sourceProvider="
                + sourceProvider + ", elementFactory="
                + elementFactory + ", pageSwitchHandler="
                + pageSwitchHandler + ", currPageIndex="
                + currPageIndex + ", pageSize="
                + pageSize + ", dynamic="
                + dynamic + ", pageWasChanged="
                + pageWasChanged + ", _srcFactory="
                + _srcFactory + ", currSource="
                + currSource + "} "
                + super.toString();
    }
}
