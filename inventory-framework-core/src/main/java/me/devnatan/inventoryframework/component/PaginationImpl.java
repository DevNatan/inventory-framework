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
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.*;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import me.devnatan.inventoryframework.state.AbstractStateValue;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.jetbrains.annotations.VisibleForTesting;

// TODO add "key" to child pagination components and check if it needs to be updated based on it
@VisibleForTesting
public class PaginationImpl extends AbstractStateValue implements Pagination, InteractionHandler {

    private final List<Component> components = new LinkedList<>();
    private final @NotNull IFContext host;

    // --- User provided ---
    private final char layoutTarget;
    private final @NotNull Object sourceProvider;
    private final @NotNull PaginationElementFactory<IFContext, Object> elementFactory;
    private final BiConsumer<IFContext, Pagination> pageSwitchHandler;

    // --- Internal ---
    private int currPageIndex;
    private final boolean lazy;
    private boolean pageWasChanged;
    private boolean initialized;
    private int pagesCount;

    /**
     * The number of elements that each page can have. -1 means uninitialized.
     */
    private int pageSize = -1;

    // Changes when dynamic data source is used and being loaded
    private boolean isLoading;

    /**
     * Final source factory for dynamic or asynchronous pagination converted from {@link #sourceProvider}.
     * <p>
     * The return parameter of this source factory can be either {@code List} in dynamic pagination
     * or {@code CompletableFuture<List>} in asynchronous pagination.
     */
    private Function<IFContext, Object> _srcFactory;

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
        this.lazy = !(sourceProvider instanceof Collection);
    }

    /**
     * Tries to access and load the source to the current page.
     * <p>
     * If this pagination {@link #isLazy() is dynamic} it tries to get the current data source
     * dynamically or asynchronously and waits for its completion.
     * <p>
     * For static pagination it returns immediately with the source.
     * <p>
     * On asynchronous pagination the source update job will be inherited by the user provided one.
     * <p>
     * When job gets done the {@link #currSource} is updated with the result of the computation.
     *
     * @return A CompletableFuture with the current pagination source as result.
     * @throws IllegalStateException In static pagination when the current source wasn't yet defined.
     */
    @SuppressWarnings("unchecked")
    private CompletableFuture<List<?>> loadSourceForTheCurrentPage() {
        // When using static pagination we just get the current source here since it will be always
        // the same. When using dynamic pagination that was not initialized yet (page index is zero)
        // must use the current data source as source of truth to ensure that pagination switches do
        // not trigger pagination data factory since it will always return the source as a whole,
        // the original one, and not the source for the switched page.
        if (!isLazy() || !initialized) {
            if (initialized && currSource == null)
                throw new IllegalStateException("User provided pagination source cannot be null");
            if (!initialized) pagesCount = calculatePagesCount(currSource);

            return CompletableFuture.completedFuture(splitSourceForPage(currPageIndex, currSource));
        }

        CompletableFuture<List<?>> job = new CompletableFuture<>();
        isLoading = true;
        simulateStateUpdate();

        final Object source = _srcFactory.apply(host);
        if (source instanceof CompletableFuture) {
            job = (CompletableFuture<List<?>>) source;
        } else {
            // Here we are covering the dynamic rendering that's can be the usage of factories like
            // `Supplier<List>` and Function<..., List> so we just cast the result here
            job.complete((List<?>) source);
        }

        // TODO Do some error treatment here, even if we expect to the user to handle it
        job.thenAccept(this::updateSource).whenComplete((result, exception) -> {
            isLoading = false;
            simulateStateUpdate();
        });
        return job;
    }

    /**
     * Updates the current source and the number of availalbe pages count based on that source.
     *
     * @param newSource The new data source.
     */
    private void updateSource(@NotNull List<?> newSource) {
        currSource = newSource;
        pagesCount = calculatePagesCount(currSource);
    }

    /**
     * The total number of pages available.
     *
     * @return The number of pages based on the current source.
     */
    private int getPagesCount() {
        return pagesCount;
    }

    /**
     * Calculates the number of pages available based on a given source.
     *
     * @param source The source to check.
     * @return The number of pages that can have based on the specified source.
     */
    private int calculatePagesCount(@NotNull List<?> source) {
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
     * Gets all elements in a given page index based on the current data source.
     *
     * @param index The page index.
     * @param src   The source to split.
     * @return All elements in a page.
     * @throws IndexOutOfBoundsException If the specified index is {@code < 0} or
     *                                   exceeds the {@link #getPagesCount() pages count}.
     */
    private List<?> splitSourceForPage(int index, List<?> src) {
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
     * Loads pagination components using container boundaries, no constraints.
     * <p>
     * The position of the first paged item must be the first slot in the container, the last
     * position must be the last slot in the container, and {@link #pageSize} on the current page
     * must be the size of the container.
     *
     * @param context      The render context.
     * @param pageContents Elements of the current page.
     */
    private void loadComponentsForUnconstrainedPagination(IFRenderContext context, List<?> pageContents) {
        final ViewContainer container = context.getContainer();
        pageSize = container.getSize();

        final int lastSlot = container.getLastSlot();

        for (int i = container.getFirstSlot(); i < Math.min(lastSlot + 1, pageContents.size()); i++) {
            final Object value = pageContents.get(i);
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
     * @param context      The render context.
     * @param pageContents Elements of the current page.
     */
    private void loadComponentsForLayeredPagination(IFRenderContext context, List<?> pageContents) {
        if (pageContents.isEmpty()) return;

        final int elementsLen = pageContents.size();
        int iterationIndex = 0;
        for (final int position : getLayoutSlotForCurrentTarget(context).getPositions()) {
            final Object value = pageContents.get(iterationIndex++);
            final ComponentFactory factory = elementFactory.create(context, iterationIndex, position, value);
            final Component component = factory.create();

            getComponentsInternal().add(component);

            if (iterationIndex == elementsLen) break;
        }
    }

    /**
     * Updates the current page size.
     * <p>
     * Page size is based on the type of pagination data source; on the possible usage of layout
     * in context, if a layout is configured in the layout so this property must be the count of
     * {@link #getLayoutTarget() layout target} characters in the layout configured layout.
     * <p>
     * When without a configured layout in the root, the page size is the entire size of context's container.
     *
     * @param context The render context.
     */
    private void updatePageSize(IFRenderContext context) {
        if (context.getConfig().getLayout() != null)
            pageSize = getLayoutSlotForCurrentTarget(context).getPositions().length;
        else pageSize = context.getContainer().getSize();
    }

    private LayoutSlot getLayoutSlotForCurrentTarget(IFRenderContext context) {
        final Optional<LayoutSlot> layoutSlotOptional = context.getLayoutSlots().stream()
                .filter(layoutSlot -> layoutSlot.getCharacter() == getLayoutTarget())
                .findFirst();

        if (!layoutSlotOptional.isPresent())
            // TODO more detailed error message
            throw new IllegalArgumentException(String.format("Layout slot target not found: %c", getLayoutTarget()));

        return layoutSlotOptional.get();
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
            _srcFactory = (Function<IFContext, Object>) sourceProvider;
        } else if (sourceProvider instanceof Supplier) {
            _srcFactory = $ -> ((Supplier<List<?>>) sourceProvider).get();
        } else {
            throw new IllegalArgumentException(String.format(
                    "Unsupported pagination source provider: %s",
                    sourceProvider.getClass().getName()));
        }

        return currSource;
    }

    /**
     * Loads the current page contents.
     *
     * @param context The render context.
     * @return A CompletableFuture with the completion stage of the current page.
     */
    private CompletableFuture<?> loadCurrentPage(IFRenderContext context) {
        return loadSourceForTheCurrentPage().thenAccept(pageContents -> {
            if (context.getConfig().getLayout() != null) loadComponentsForLayeredPagination(context, pageContents);
            else loadComponentsForUnconstrainedPagination(context, pageContents);
        });
    }

    public @NotNull IFContext getHost() {
        return host;
    }

    @Override
    public Object get() {
        return this;
    }

    @Override
    public void set(Object value) {
        // do nothing since Pagination is not immutable but unmodifiable directly
    }

    @Override
    public @NotNull VirtualView getRoot() {
        return host;
    }

    @Override
    public int getPosition() {
        final List<Component> components = getComponentsInternal();
        if (components.isEmpty()) return 0;

        final int first = components.get(0).getPosition();
        final int last = components.get(components.size() - 1).getPosition();

        return last - first;
    }

    @Override
    public void render(@NotNull IFSlotRenderContext context) {
        if (!initialized) {
            final IFRenderContext root = context.getParent();
            updatePageSize(root);
            loadCurrentPage(root).thenRun(() -> renderChild(context));
            initialized = true;
            return;
        }

        renderChild(context);
    }

    private void renderChild(IFSlotRenderContext context) {
        getComponentsInternal().forEach(child -> child.render(context));
    }

    @Override
    public void updated(@NotNull IFSlotRenderContext context) {
        // If page was changed all components will be removed, so don't trigger update on them
        if (pageWasChanged) {
            final IFRenderContext renderContext = context.getParent();
            clearChild(renderContext, true);
            loadCurrentPage(renderContext).thenRun(() -> {
                render(context);
                simulateStateUpdate();
                pageWasChanged = false;
            });
            return;
        }

        getComponentsInternal().forEach(child -> child.updated(context));
    }

    /**
     * Simulate state update to call listeners thus calling watches in parent components.
     * <p>
     * Used when something changes in pagination. It allows the end user and developers to "listen"
     * for changes in {@link #isLoading()} and current page states.
     */
    private void simulateStateUpdate() {
        host.updateState(getState().internalId(), this);
    }

    @Override
    public void clear(@NotNull IFContext context) {
        // Only clear components if page was changed to not make the clear operation inconsistent
        if (!pageWasChanged) {
            getComponentsInternal().forEach(child -> child.clear(context));
            return;
        }

        clearChild(context, false);
    }

    private void clearChild(IFContext context, boolean bulk) {
        if (bulk) {
            getComponentsInternal().forEach(child -> child.clear(context));
            getComponentsInternal().clear();
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
    public boolean intersects(@NotNull Component other) {
        return Component.intersects(this, other);
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
        return pageIndex < getPagesCount();
    }

    @Override
    public void switchTo(int pageIndex) {
        if (!hasPage(pageIndex))
            throw new IndexOutOfBoundsException(
                    String.format("Page index not found (%d > %d)", pageIndex, getPagesCount()));

        if (isLoading()) return;
        if (pageSwitchHandler != null) pageSwitchHandler.accept(host, this);
        currPageIndex = pageIndex;
        pageWasChanged = true;
        host.updateComponent(this);
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
    public boolean isLazy() {
        return lazy;
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @NotNull
    @Override
    public Iterator<Component> iterator() {
        return getComponents().iterator();
    }

    @Override
    public boolean isVisible() {
        for (final Component children : this) {
            if (!children.isVisible()) return false;
        }

        return true;
    }

    @Override
    public void setVisible(boolean visible) {
        getComponentsInternal().forEach(component -> component.setVisible(visible));
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

    @Override
    public boolean isManagedExternally() {
        return true;
    }

    @Override
    public boolean shouldRender(IFContext context) {
        return true;
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
                && isLazy() == that.isLazy()
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
                isLazy(),
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
                + lazy + ", pageWasChanged="
                + pageWasChanged + ", _srcFactory="
                + _srcFactory + ", currSource="
                + currSource + "} "
                + super.toString();
    }
}
