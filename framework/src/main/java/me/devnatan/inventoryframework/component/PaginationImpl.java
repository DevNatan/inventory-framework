package me.devnatan.inventoryframework.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class PaginationImpl extends StateValue implements Pagination, InteractionHandler {

    private final List<Component> components = new LinkedList<>();

    // --- State ---
    private final @NotNull IFContext host;

    // --- User provided ---
    private final char layoutTarget;
    private final @NotNull Object sourceProvider;
    private final @NotNull Function<Object, ComponentFactory> itemFactory;

    // --- Internal ---
    private int currPageIndex;
    private int pageSize;
    private final boolean dynamic;
    private boolean pageWasChanged;

    /**
     * Final source factory for dynamic pagination converted from {@link #sourceProvider}.
     */
    private Function<? extends IFContext, Collection<?>> _srcFactory;

    /**
     * Current page source. Only {@code null} before first pagination render.
     */
    private List<?> currSource;

    @SuppressWarnings("unchecked")
    public PaginationImpl(
            @NotNull State<?> state,
            @NotNull IFContext host,
            char layoutTarget,
            @NotNull Object sourceProvider,
            @NotNull Function<Object, ComponentFactory> itemFactory) {
        super(state);
        this.host = host;
        this.layoutTarget = layoutTarget;
        this.sourceProvider = sourceProvider;
        this.itemFactory = itemFactory;
        this.currSource = Objects.requireNonNull(convertSourceProvider(), "Converted source cannot never be null");
        this.dynamic = sourceProvider instanceof Collection;
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
        final IFRenderContext renderContext = (IFRenderContext) context.getParent();
        if (renderContext.getConfig().getLayout() != null) {
            final Optional<LayoutSlot> layoutSlotOptional = renderContext.getLayoutSlots().stream()
                    .filter(layoutSlot -> layoutSlot.getCharacter() == getLayoutTarget())
                    .findFirst();

            System.out.println(" ");
            System.out.println("renderContext.getLayoutSlots() = " + renderContext.getLayoutSlots());
            if (!layoutSlotOptional.isPresent())
                throw new IllegalArgumentException(
                        String.format("Layout slot target not found: %c", getLayoutTarget()));

            System.out.println(" ");
            final LayoutSlot layoutSlot = layoutSlotOptional.get();
            pageSize = layoutSlot.getPositions().size();
            final List<?> currItems = getPageContents(currPageIndex);

            System.out.println("Using layout as page size");
            System.out.println("pageSize = " + pageSize);

            int elementIndex = 0;
            final int itemsLen = currItems.size();
            for (final int position : layoutSlot.getPositions()) {
                if (elementIndex == itemsLen) break;

                final Object value = currItems.get(elementIndex++);
                final ItemComponentBuilder<?> builder = (ItemComponentBuilder<?>) itemFactory.apply(value);
                builder.withSlot(position);

                final Component component = ((ComponentFactory) builder).create();

                System.out.printf("[%d] %s%n", position, component);
                components.add(component);
            }
        } else {
            pageSize = context.getContainer().getSize();
            final List<?> currItems = getPageContents(currPageIndex);

            final int firstSlot = context.getContainer().getFirstSlot();
            final int iterationLimit = Math.min(context.getContainer().getLastSlot() + 1, currItems.size());

            System.out.println("Using container as page size");
            System.out.println("pageSize = " + pageSize);
            System.out.println("firstSlot = " + firstSlot);
            System.out.println("iterationLimit = " + iterationLimit);

            for (int i = firstSlot; i < iterationLimit; i++) {
                final Object value = currItems.get(i);
                final ItemComponentBuilder<?> builder = (ItemComponentBuilder<?>) itemFactory.apply(value);
                builder.withSlot(i);

                final Component component = ((ComponentFactory) builder).create();

                System.out.printf("[%d] %s%n", i, component);
                components.add(component);
            }
        }

        getComponents().forEach(child -> child.render(context));
    }

    @Override
    public void updated(@NotNull IFSlotRenderContext context) {
        if (pageWasChanged) {
            clear(context);
            pageWasChanged = false;
        }

        getComponents().forEach(child -> child.updated(context));
    }

    @Override
    public boolean shouldBeUpdated() {
        return pageWasChanged;
    }

    @Override
    public void clear(@NotNull IFContext context) {
        final Iterator<Component> childIterator = components.iterator();
        while (childIterator.hasNext()) {
            Component child = childIterator.next();
            child.clear(context);
            childIterator.remove();
        }
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

        currPageIndex = pageIndex;
        pageWasChanged = true;
        host.updateRoot();
        // TODO trigger update and page switch
    }

    @Override
    public void advance() {
        System.out.println("tried to advance: " + canAdvance());
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

    /**
     * The pagination source.
     *
     * @return The current pagination source.
     * @throws IllegalStateException If the current source wasn't yet defined.
     */
    private List<?> getSourceOrThrow() {
        if (currSource != null) return currSource;
        if (isDynamic())
            throw new IllegalStateException("Dynamic pagination must set current source before try to access it");

        throw new IllegalStateException("Pagination source cannot be null for static pagination");
    }

    /**
     * The number of pages.
     *
     * @return The number of pages based on the {@link #getSourceOrThrow() current source}.
     */
    // TODO needs caching
    private int getPagesCount() {
        return (int) Math.ceil((double) getSourceOrThrow().size() / getPageSize());
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
     */
    private void renderUnconstrainedPagination() {
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Renders the pagination using the layout positions defined from the {@link #getLayoutTarget() target layout character}.
     * <p>
     * The first position, last position and number of items on the page must be exactly the same as
     * the layout.
     */
    private void renderLayeredPagination() {
        throw new UnsupportedOperationException("TODO");
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
            _srcFactory = (Function<? extends IFContext, Collection<?>>) sourceProvider;
        } else {
            throw new IllegalArgumentException(String.format(
                    "Unsupported pagination source provider: %s",
                    sourceProvider.getClass().getName()));
        }

        return currSource;
    }
}
