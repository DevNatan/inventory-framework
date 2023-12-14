package me.devnatan.inventoryframework.component;

import static me.devnatan.inventoryframework.IFDebug.debug;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.*;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateValue;
import me.devnatan.inventoryframework.state.StateValueHost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.jetbrains.annotations.VisibleForTesting;

// TODO add "key" to child pagination components and check if it needs to be updated based on it
@VisibleForTesting
public class PaginationImpl extends AbstractComponent implements Pagination, StateValue {

    List<Component> components = new ArrayList<>();

    // --- User provided ---
    private final char layoutTarget;
    private final Object sourceProvider;
    private final PaginationElementFactory<Object> elementFactory;
    private final BiConsumer<VirtualView, Pagination> pageSwitchHandler;

    // --- Internal ---
    private final long internalStateId;
    private int currPageIndex;
    private final boolean isLazy, isStatic, isComputed, isAsync;
    boolean pageWasChanged;
    boolean initialized;
    private int pagesCount;
    private LayoutSlot currentLayoutSlot;

    // Number of elements that each page can have. -1 means uninitialized.
    private int pageSize = -1;

    // Changes when dynamic data source is used and being loaded
    private boolean isLoading;

    /**
     * Final source factory for dynamic or asynchronous pagination converted from {@link #sourceProvider}.
     * <p>
     * The return parameter of this source factory can be either {@code List} in dynamic pagination
     * or {@code CompletableFuture<List>} in asynchronous pagination.
     */
    private Function<VirtualView, Object> _srcFactory;

    // Current page source, null before first pagination render.
    private List<?> currSource;

    public PaginationImpl(
            String key,
            VirtualView root,
            Ref<Component> reference,
            Set<State<?>> watchingStates,
            Predicate<? extends IFContext> displayCondition,
            long internalStateId,
            char layoutTarget,
            Object sourceProvider,
            PaginationElementFactory<Object> elementFactory,
            BiConsumer<VirtualView, Pagination> pageSwitchHandler,
            boolean isAsync,
            boolean isComputed,
            boolean isSelfManaged) {
        super(key, root, reference, watchingStates, displayCondition, isSelfManaged);
        this.internalStateId = internalStateId;
        this.layoutTarget = layoutTarget;
        this.sourceProvider = sourceProvider;
        this.elementFactory = elementFactory;
        this.pageSwitchHandler = pageSwitchHandler;
        this.currSource = convertSourceProvider();
        this.isComputed = isComputed;
        this.isAsync = isAsync;
        this.isStatic = sourceProvider instanceof Collection;
        this.isLazy =
                !isStatic && !isComputed && (sourceProvider instanceof Function || sourceProvider instanceof Supplier);
        setHandle(new PaginationHandle(this));
    }

    // region Source Retrieval & Assignment
    /**
     * Tries to access and load the source to the current page.
     * <p>
     * If this pagination {@link #isLazy() is lazy} it tries to get the current data source
     * dynamically or asynchronously and waits for its completion. For static pagination it returns
     * immediately with the source.
     * <p>
     * On asynchronous pagination the source update job will be inherited by the user provided one
     * and when job gets done the {@link #currSource} is updated with the result of the computation.
     *
     * @return A CompletableFuture with the current pagination source as result.
     * @throws IllegalStateException In static pagination when the current source wasn't yet defined.
     */
    private CompletableFuture<List<?>> loadSourceForTheCurrentPage() {
        /*
         * In lazy pagination **that was already initialized (already rendered before)** we must
         * use the current data source as source of truth to ensure that page switching do not
         * re-trigger pagination data factory since it will always return the source as a whole,
         * the original one, and not the source for the switched page.
         */
        final boolean reuseLazy = isLazy() && initialized;
        debug(
                "[Pagination] Loading page %d (reuseLazy = %b, isStatic = %b, isComputed = %b, forceUpdated = %b)",
                currentPageIndex(), reuseLazy, isStatic(), isComputed(), wasForceUpdated());

        if ((isStatic() || reuseLazy) && !isComputed() && !wasForceUpdated()) {
            // For unknown reasons already initialized but source is null, external modification?
            if (initialized && currSource == null)
                throw new IllegalStateException("User provided pagination source cannot be null");
            else {
                // Lazy pagination have pages count calculated on first render as a computed flow
                if (!isLazy()) pagesCount = calculatePagesCount(currSource);
            }

            final List<?> result =
                    Pagination.splitSourceForPage(currentPageIndex(), getPageSize(), getPagesCount(), currSource);
            debug(
                    "[Pagination] Split source of %d elements (page = %d, pageSize = %d, pagesCount = %d)",
                    result.size(), currentPageIndex(), getPageSize(), getPagesCount());
            int index = 0;
            for (final Object el : result) {
                debug("  | (%d): %s", index++, el);
            }

            return CompletableFuture.completedFuture(result);
        }

        isLoading = true;
        simulateStateUpdate();

        // TODO Do some error treatment here, even if we expect to the user to handle it
        return createNewProvidedSource().handle((result, exception) -> {
            if (exception != null) {
                debug("[Pagination] An error occurred on data source computation: %s", exception.getMessage());
                exception.printStackTrace();
                return Collections.emptyList();
            }

            updateSource(result);
            isLoading = false;
            simulateStateUpdate();

            if (isLazy())
                return Pagination.splitSourceForPage(currentPageIndex(), getPageSize(), getPagesCount(), result);
            else return result;
        });
    }

    @SuppressWarnings("unchecked")
    private CompletableFuture<List<?>> createNewProvidedSource() {
        CompletableFuture<List<?>> job = new CompletableFuture<>();

        System.out.println("getRoot() = " + getRoot());

        final Object source = _srcFactory.apply(getRoot());
        if (isAsync()) job = (CompletableFuture<List<?>>) source;
        else if (isComputed() || isLazy()) job.complete((List<?>) source);
        else throw new IllegalArgumentException("Unhandled pagination source");

        return job;
    }

    /**
     * Updates the current source and the number of available pages count based on that source.
     *
     * @param newSource The new data source.
     */
    private void updateSource(@NotNull List<?> newSource) {
        currSource = newSource;
        pagesCount = calculatePagesCount(currSource);
        debug("[Pagination] Source updated with %d elements and pages count set to %d", newSource.size(), pagesCount);
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
            _srcFactory = (Function<VirtualView, Object>) sourceProvider;
        } else if (sourceProvider instanceof Supplier) {
            _srcFactory = $ -> ((Supplier<List<?>>) sourceProvider).get();
        } else {
            throw new IllegalArgumentException(String.format(
                    "Unsupported pagination source provider: %s",
                    sourceProvider.getClass().getName()));
        }

        return currSource;
    }
    // endregion

    // region Page Rendering & Update
    /**
     * The current page size.
     *
     * @return Number of available elements position for pagination in the current page.
     */
    private int getPageSize() {
        if (pageSize == -1) throw new IllegalStateException("Page size need to be updated before try to get it");

        return pageSize;
    }

    /**
     * The total number of pages available.
     *
     * @return The number of pages based on the current source.
     */
    private int getPagesCount() {
        return pagesCount;
    }

    private LayoutSlot getLayoutSlotForCurrentTarget(IFRenderContext context) {
        if (currentLayoutSlot != null) return currentLayoutSlot;

        final Optional<LayoutSlot> layoutSlotOptional = context.getLayoutSlots().stream()
                .filter(layoutSlot -> layoutSlot.getCharacter() == getLayoutTarget())
                .findFirst();

        if (!layoutSlotOptional.isPresent())
            // TODO more detailed error message
            throw new IllegalArgumentException(String.format("Layout slot target not found: %c", getLayoutTarget()));

        return (currentLayoutSlot = layoutSlotOptional.get());
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

    void renderChild(IFRenderContext context) {
        getInternalComponents().forEach(context::renderComponent);
    }

    /**
     * Loads the current page contents.
     *
     * @param context The render context.
     * @return A CompletableFuture with the completion stage of the current page.
     */
    CompletableFuture<?> loadCurrentPage(IFRenderContext context) {
        return loadSourceForTheCurrentPage()
                .thenAccept(pageContents -> {
                    if (pageContents.isEmpty()) {
                        debug("[Pagination] Empty page contents (page %d of %d)", currentPageIndex(), getPagesCount());
                        return;
                    }

                    final boolean useLayout = context.getConfig().getLayout() != null;
                    debug("[Pagination] Adding components.. (useLayout = %b)", useLayout);

                    if (useLayout) addComponentsForLayeredPagination(context, pageContents);
                    else addComponentsForUnconstrainedPagination(context, pageContents);
                })
                .exceptionally(error -> {
                    debug("[Pagination] An error occurred while loading the current page");
                    error.printStackTrace();
                    return null;
                });
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
    private void addComponentsForUnconstrainedPagination(IFRenderContext context, List<?> pageContents) {
        final ViewContainer container = context.getContainer();

        // TODO Investigate why page size is being updated here
        if (pageSize == -1) updatePageSize(context);

        final int lastSlot = Math.min(container.getLastSlot() + 1 /* inclusive */, pageContents.size());
        for (int i = container.getFirstSlot(); i < lastSlot; i++) {
            final Object value = pageContents.get(i);
            final Component component = elementFactory.create(this, i, i, value);
            getInternalComponents().add(component);
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
    private void addComponentsForLayeredPagination(IFRenderContext context, List<?> pageContents) {
        final LayoutSlot targetLayoutSlot = getLayoutSlotForCurrentTarget(context);
        final int elementsLen = pageContents.size();
        debug("[Pagination] Elements count: %d elements", elementsLen);
        debug("[Pagination] Iterating over '%c' layout target", targetLayoutSlot.getCharacter());

        int iterationIndex = 0;
        for (final int position : targetLayoutSlot.getPositions()) {
            final Object value = pageContents.get(iterationIndex++);

            try {
                final Component component = elementFactory.create(this, iterationIndex, position, value);

                debug(
                        () -> "  @ added %d (index %d) = %s",
                        position,
                        iterationIndex,
                        component.getClass().getSimpleName());
                getInternalComponents().add(component);
            } catch (final Exception exception) {
                debug(() -> "  @ failed to add %d (index %d) = %s", position, iterationIndex, exception.getMessage());
                exception.printStackTrace();
            }

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
    void updatePageSize(IFRenderContext context) {
        final boolean useLayout = context.getConfig().getLayout() != null;
        if (useLayout) pageSize = getLayoutSlotForCurrentTarget(context).getPositions().length;
        else pageSize = context.getContainer().getSize();

        debug(
                "[Pagination] Page size updated to %d (page = %d, useLayout = %b)",
                pageSize, currentPageIndex(), useLayout);
    }
    // endregion

    // region State Management
    @Override
    public long internalId() {
        return internalStateId;
    }

    @Override
    public Object get() {
        return this;
    }

    @Override
    public void set(Object value) {
        // do nothing since Pagination is not immutable but unmodifiable directly
    }

    /**
     * Simulate state update to call listeners thus calling watches in parent components.
     * <p>
     * Used when something changes in pagination. It allows the end user and developers to "listen"
     * for changes in {@link #isLoading()} and current page states.
     */
    void simulateStateUpdate() {
        debug("[Pagination] State update simulation triggered on %d", internalId());
        ((StateValueHost) getRoot()).updateState(internalId(), this);
    }
    // endregion

    @Override
    public @UnmodifiableView List<Component> getComponents() {
        return Collections.unmodifiableList(getInternalComponents());
    }

    @Override
    public List<Component> getInternalComponents() {
        return components;
    }

    @Override
    public boolean isContainedWithin(int position) {
        if (currentLayoutSlot != null) {
            for (int slot : currentLayoutSlot.getPositions()) {
                if (slot == position) return true;
            }
            return false;
        }

        for (final Component component : getInternalComponents()) {
            if (component.isContainedWithin(position)) return true;
        }
        return false;
    }

    @Override
    public boolean intersects(@NotNull Component other) {
        throw new UnsupportedOperationException("Missing #intersects(Component) implementation.");
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
        if (isComputed()) return true;
        if (pageIndex < 0) return false;
        return pageIndex < getPagesCount();
    }

    @Override
    public void switchTo(int pageIndex) {
        debug("[Pagination] #switchTo(int) called (pageIndex = %d, isLoading = %b)", pageIndex, isLoading());
        if (!hasPage(pageIndex))
            throw new IndexOutOfBoundsException(
                    String.format("Page index not found (%d > %d)", pageIndex, getPagesCount()));

        if (isLoading()) return;

        currPageIndex = pageIndex;
        pageWasChanged = true;

        if (pageSwitchHandler != null) {
            pageSwitchHandler.accept(getRoot(), this);
        }

        update();
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
        return isLazy;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public boolean isComputed() {
        return isComputed;
    }

    @Override
    public boolean isAsync() {
        return isAsync;
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
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        getInternalComponents().forEach(component -> component.setVisible(isVisible()));
    }

    @Override
    public int getPosition() {
        throw new UnsupportedOperationException("Pagination position cannot be accessed");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaginationImpl that = (PaginationImpl) o;
        return getLayoutTarget() == that.getLayoutTarget()
                && currPageIndex == that.currPageIndex
                && pageSize == that.pageSize
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
                pageSize,
                isLazy(),
                pageWasChanged);
    }

    @Override
    public String toString() {
        return "PaginationImpl{root="
                + getRoot() + ", layoutTarget="
                + layoutTarget + ", sourceProvider="
                + sourceProvider + ", elementFactory="
                + elementFactory + ", pageSwitchHandler="
                + pageSwitchHandler + ", currPageIndex="
                + currPageIndex + ", pageSize="
                + pageSize + ", dynamic="
                + isLazy + ", pageWasChanged="
                + pageWasChanged + ", _srcFactory="
                + _srcFactory + ", currSource="
                + currSource + "} "
                + super.toString();
    }
}

class PaginationHandle extends ComponentHandle {

    private final PaginationImpl pagination;

    public PaginationHandle(PaginationImpl pagination) {
        this.pagination = pagination;
    }

    void render(@NotNull IFComponentRenderContext context) {
        final IFRenderContext root = (IFRenderContext) context.getTopLevelContext();
        if (!pagination.initialized || pagination.pageWasChanged) {
            if (!pagination.initialized) pagination.updatePageSize(root);
            pagination.loadCurrentPage(root).thenRun(() -> {
                pagination.renderChild(root);
                pagination.simulateStateUpdate();
            });
            pagination.setVisible(true);
            pagination.initialized = true;
            return;
        }

        pagination.renderChild(root);
    }

    void updated(@NotNull IFComponentUpdateContext context) {
        final IFRenderContext root = (IFRenderContext) context.getTopLevelContext();

        debug(
                "[Pagination] #updated(IFSlotRenderContext) called (forceUpdated = %b, pageWasChanged = %b)",
                pagination.wasForceUpdated(), pagination.pageWasChanged);

        // If page was changed all components will be removed, so don't trigger update on them
        if (pagination.wasForceUpdated() || pagination.pageWasChanged) {
            cleared(root);
            pagination.components = new ArrayList<>();
            root.renderComponent(pagination);
            pagination.pageWasChanged = false;
            return;
        }

        if (!pagination.isVisible()) return;
        pagination.getInternalComponents().forEach(child -> root.updateComponent(child, context.isForceUpdate(), null));
    }

    void clicked(@NotNull IFSlotClickContext context) {
        // Lock child interactions while page is changing (specially for async pagination cases)
        if (pagination.pageWasChanged) {
            context.setCancelled(true);
            return;
        }

        for (final Component child : pagination.getInternalComponents()) {
            if (!child.isContainedWithin(context.getClickedSlot())) continue;

            // Hidden components that are not self-managed e.g.: managed by user-provided handle
            // must handle visibility issues by its own to ensure that clicks are not superimposed
            // by the root component. This ensures that when a child component that is hidden
            // and is self-managed is clicked the click will be propagated to handlers of the
            // child component itself and not to its root
            if (!child.isVisible() && !child.isSelfManaged()) continue;

            context.getParent()
                    .performClickInComponent(
                            child,
                            context.getViewer(),
                            context.getClickedContainer(),
                            context.getPlatformEvent(),
                            context.getClickedSlot(),
                            true);
        }
    }

    void cleared(@NotNull IFRenderContext context) {
        debug("[Pagination] #clear(IFRenderContext) called (pageWasChanged = %b)", pagination.pageWasChanged);
        if (!pagination.pageWasChanged) {
            pagination.getInternalComponents().forEach(context::clearComponent);
            return;
        }

        final Iterator<Component> childIterator =
                pagination.getInternalComponents().iterator();
        while (childIterator.hasNext()) {
            Component child = childIterator.next();
            context.clearComponent(child);
            childIterator.remove();
        }
    }

    @Override
    public void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
        final PipelinePhase phase = Objects.requireNonNull(pipeline.getPhase());
        if (phase.equals(Component.RENDER)) render((IFComponentRenderContext) subject);
        if (phase.equals(Component.UPDATE)) updated((IFComponentUpdateContext) subject);
        if (phase.equals(Component.CLEAR)) cleared(((IFComponentClearContext) subject).getParent());
        if (phase.equals(Component.CLICK)) clicked((IFSlotClickContext) subject);
    }
}
