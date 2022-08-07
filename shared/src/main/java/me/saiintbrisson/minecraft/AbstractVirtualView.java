package me.saiintbrisson.minecraft;

import static me.saiintbrisson.minecraft.AbstractPaginatedView.NAVIGATE_LEFT;
import static me.saiintbrisson.minecraft.AbstractPaginatedView.NAVIGATE_RIGHT;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class AbstractVirtualView implements VirtualView {

    @ToString.Exclude
    private ViewItem[] items;

    private ViewErrorHandler errorHandler;
    private ViewUpdateJob updateJob;
    private final List<LayoutPattern> layoutPatterns = new ArrayList<>();
    private String[] layout;
    private Stack<Integer> layoutItemsLayer;
    private boolean layoutSignatureChecked;
    private Deque<ViewItem> reservedItems;

    protected ViewItem[] getItems() {
        return items;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ViewItem getItem(int index) {
        return items[index];
    }

    final void setItems(ViewItem[] items) {
        this.items = items;
    }

    /**
     * {@inheritDoc}
     */
    public final ViewErrorHandler getErrorHandler() {
        return errorHandler;
    }

    /**
     * {@inheritDoc}
     */
    public final void setErrorHandler(ViewErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getFirstSlot() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getLastSlot() {
        return items.length - 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
    public final ViewItem item() {
        return new ViewItem();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
    public final ViewItem item(@NotNull ItemStack item) {
        return new ViewItem().withItem(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
    public final ViewItem item(@NotNull Material material) {
        return new ViewItem().withItem(new ItemStack(material));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
    public final ViewItem item(@NotNull Material material, int amount) {
        return new ViewItem().withItem(new ItemStack(material, amount));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
    public final ViewItem item(@NotNull Material material, short durability) {
        return new ViewItem().withItem(new ItemStack(material, 1, durability));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
    public final ViewItem item(@NotNull Material material, int amount, short durability) {
        return new ViewItem().withItem(new ItemStack(material, amount, durability));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public final ViewItem slot(int slot) {
        inventoryModificationTriggered();
        if (getItems() == null) throw new IllegalStateException("VirtualView was not initialized yet");

        final ViewItem item = new ViewItem(slot);
        getItems()[slot] = item;
        System.out.println("Registered @ " + slot + ": " + item);
        return item;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public final ViewItem slot(int slot, Object item) {
        return slot(slot).withItem(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public final ViewItem slot(int row, int column) {
        return slot(convertSlot(row, column), null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public final ViewItem slot(int row, int column, Object item) {
        return slot(convertSlot(row, column), item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final @NotNull ViewItem firstSlot() {
        return slot(getFirstSlot());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final @NotNull ViewItem firstSlot(Object item) {
        return slot(getFirstSlot(), item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final @NotNull ViewItem lastSlot() {
        return slot(getLastSlot());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final @NotNull ViewItem lastSlot(Object item) {
        return slot(getLastSlot(), item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull ViewItem availableSlot() {
        return availableSlot(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull ViewItem availableSlot(Object item) {
        final int slot = getLayout() != null ? ViewItem.AVAILABLE_SLOT : getNextAvailableSlot();

        // item slot will be resolved after layout resolution
        if (slot == ViewItem.AVAILABLE_SLOT) {
            final ViewItem viewItem = new ViewItem(slot).withItem(item);
            if (reservedItems == null) reservedItems = new ArrayDeque<>();

            reservedItems.add(viewItem);
            return viewItem;
        }

        return slot(slot, item);
    }

    /**
     * Determines the next available slot.
     *
     * @return The next available slot.
     */
    int getNextAvailableSlot() {
        for (int i = 0; i < getItems().length; i++) {
            final ViewItem item = items[i];
            if (item == null) return i;
        }

        return ViewItem.AVAILABLE_SLOT;
    }

    @SuppressWarnings("NullableProblems")
    void render(@NotNull ViewContext context) {
        if (context.getLayout() != null) {
            // force layout resolution if it was not yet resolved
            if (!context.isLayoutSignatureChecked()) resolveLayout(context, context.getLayout());

            renderLayout(context, context);
            return;
        }

        if (getLayout() != null) {
            if (!isLayoutSignatureChecked())
                throw new IllegalStateException(
                        "Regular view layout resolution must be realized on initialization before render");

            renderLayout(this, context);
            return;
        }

        for (int i = 0; i < getItems().length; i++) {
            render(context, i);
        }
    }

    private void renderLayout(@NotNull VirtualView view, @NotNull ViewContext context) {
        if (!view.isLayoutSignatureChecked()) throw new IllegalStateException("Layout must be resolved before render");

        System.out.println("rendering layout of " + view);

        // fast path -- skip if reserved items defined by auto-slot-filling was already set
        if (view.getReservedItems() == null || view.getReservedItems().isEmpty()) return;

        final Stack<Integer> itemsLayer = view.getLayoutItemsLayer();
        final int layerSize = itemsLayer.size();
        final int lastSlot = itemsLayer.peek();

        System.out.println("last  slot = " + lastSlot + ", layer size = " + layerSize);
        for (int i = 0; i <= lastSlot; i++) {
            // check for items overflow
            if (i >= layerSize) break;

            final int targetSlot = itemsLayer.elementAt(i);
            System.out.println("rendering slot at " + targetSlot + " of " + view);

            final ViewItem next;
            try {
                // remove first to preserve insertion order
                next = view.getReservedItems().removeFirst();
            } catch (NoSuchElementException ignored) {
                break;
            }

            // register item on view since dynamically rendered items are not registered, so we need
            // to register then on the first time that it's get rendered
            view.slot(targetSlot, next);

            render(context, next, targetSlot);
        }
    }

    protected final void render(@NotNull ViewContext context, int slot) {
        final ViewItem item = context.resolve(slot, true);
        if (item == null) return;

        render(context, item, slot);
    }

    protected final void render(@NotNull ViewContext context, @NotNull ViewItem item, int slot) {
        inventoryModificationTriggered();

        // the item's slot has not yet been determined because of using the auto-set slot function
        // and must be applied during rendering.
        System.out.println("rendering item at " + slot);
        if (item.getSlot() == ViewItem.AVAILABLE_SLOT) item.setSlot(slot);

        final Object fallbackItem = item.getItem();

        if (item.getRenderHandler() != null) {
            final ViewSlotContext renderContext =
                    PlatformUtils.getFactory().createSlotContext(item, (BaseViewContext) context, 0, null);

            runCatching(context, () -> item.getRenderHandler().handle(renderContext));
            if (renderContext.hasChanged()) {
                context.getContainer().renderItem(slot, unwrap(renderContext.getItemWrapper()));
                renderContext.setChanged(false);
                return;
            }
        }

        if (fallbackItem == null)
            throw new IllegalArgumentException(String.format(
                    "No item were provided and the rendering function was not defined at slot %d."
                            + "You must use a rendering function #slot(...).onRender(...)"
                            + " or a fallback item #slot(fallbackItem)",
                    slot));

        context.getContainer().renderItem(slot, unwrap(fallbackItem));
    }

    private Object unwrap(Object item) {
        if (item instanceof ItemWrapper) return unwrap(((ItemWrapper) item).getValue());

        return item;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        throw new UnsupportedOperationException("Update aren't supported in this view");
    }

    void update(@NotNull ViewContext context) {
        for (int i = 0; i < getItems().length; i++) update(context, i);
    }

    final void update(@NotNull ViewContext context, int slot) {
        inventoryModificationTriggered();

        final ViewItem item = context.resolve(slot, true);
        if (item == null) {
            context.getContainer().removeItem(slot);
            return;
        }

        update(context, item, slot);
    }

    final void update(@NotNull ViewContext context, ViewItem item, int slot) {
        inventoryModificationTriggered();

        if (item.getUpdateHandler() != null) {
            final ViewSlotContext updateContext =
                    PlatformUtils.getFactory().createSlotContext(item, (BaseViewContext) context, 0, null);

            runCatching(context, () -> item.getUpdateHandler().handle(updateContext));
            if (updateContext.hasChanged()) {
                context.getContainer().renderItem(slot, unwrap(updateContext.getItemWrapper()));
                updateContext.setChanged(false);
                return;
            }
        }

        // update handler can be used as an empty function, so we fall back to the render handler to
        // update the fallback item properly
        render(context, item, slot);
    }

    /**
     * {@inheritDoc}
     */
    @ApiStatus.Internal
    ViewItem resolve(int index) {
        // fast path -- skip -999 index on some platforms
        if (index < 0) return null;

        final int len = getItems().length;
        if (index >= len) return null;

        return getItems()[index];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void clear(int slot) {
        getItems()[slot] = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewUpdateJob getUpdateJob() {
        return updateJob;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setUpdateJob(ViewUpdateJob updateJob) {
        this.updateJob = updateJob;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void scheduleUpdate(long intervalInTicks) {
        scheduleUpdate(-1, intervalInTicks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scheduleUpdate(long delayInTicks, long intervalInTicks) {
        inventoryModificationTriggered();
        PlatformUtils.getFactory().scheduleUpdate(this, delayInTicks, intervalInTicks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void scheduleUpdate(@NotNull Duration duration) {
        scheduleUpdate(-1, Math.floorDiv(duration.getSeconds(), 20));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isScheduledToUpdate() {
        return updateJob != null;
    }

    /**
     * {@inheritDoc}
     */
    @ApiStatus.Internal
    @Override
    public void inventoryModificationTriggered() {}

    final void runCatching(final ViewContext context, @NotNull final Runnable runnable) {
        if (context != null && context.getErrorHandler() != null) {
            tryRunOrFail(context, runnable);
            return;
        }

        if (getErrorHandler() == null) {
            runnable.run();
            return;
        }

        tryRunOrFail(context, runnable);
    }

    boolean throwException(final ViewContext context, @NotNull final Exception exception) {
        if (context != null && context.getErrorHandler() != null) {
            context.getErrorHandler().error(context, exception);
            if (!context.isPropagateErrors()) return false;
        }

        launchError(getErrorHandler(), context, exception);
        return true;
    }

    protected final void launchError(
            final ViewErrorHandler errorHandler, final ViewContext context, @NotNull final Exception exception) {
        if (errorHandler == null) return;

        errorHandler.error(context, exception);
    }

    private void tryRunOrFail(final ViewContext context, @NotNull final Runnable runnable) {
        try {
            runnable.run();
        } catch (final Exception e) {
            throwException(context, e);
        }
    }

    /**
     * Returns the slot associated with the specified row and column.
     *
     * @param row    The rows count.
     * @param column The columns count.
     * @return The slot position based in specified row and column.
     */
    int convertSlot(int row, int column) {
        throw new IllegalArgumentException("Slot conversion not supported");
    }

    protected final int convertSlot(int row, int column, int maxRowsCount, int maxColumnsCount) {
        if (row > maxRowsCount)
            throw new IllegalArgumentException(
                    String.format("Row cannot be greater than %d (given %d)", maxRowsCount, row));

        if (column > maxColumnsCount)
            throw new IllegalArgumentException(
                    String.format("Column cannot be greater than %d (given %d)", maxColumnsCount, column));

        return Math.max(row - 1, 0) * maxColumnsCount + Math.max(column - 1, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ApiStatus.Internal
    public List<LayoutPattern> getLayoutPatterns() {
        return layoutPatterns;
    }

    @Override
    @ApiStatus.Internal
    public String[] getLayout() {
        return layout;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void setLayout(@Nullable String... layout) {
        this.layout = layout;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void setLayout(char character, @Nullable Supplier<ViewItem> factory) {
        checkReservedLayoutCharacter(character);
        if (factory == null) {
            layoutPatterns.removeIf(pattern -> pattern.getCharacter() == character);
            return;
        }

        layoutPatterns.add(new LayoutPattern(character, factory));
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void setLayout(char identifier, @Nullable Consumer<ViewItem> layout) {
        setLayout(identifier, () -> {
            final ViewItem item = new ViewItem();
            Objects.requireNonNull(layout, "Layout pattern consumer cannot be null")
                    .accept(item);
            return item;
        });
    }

    void checkReservedLayoutCharacter(char character) {
        if (character == LAYOUT_EMPTY_SLOT
                || character == LAYOUT_FILLED_SLOT
                || character == LAYOUT_PREVIOUS_PAGE
                || character == LAYOUT_NEXT_PAGE)
            throw new IllegalArgumentException(String.format(
                    "The \"%c\" character is reserved in layouts and cannot be used due to backwards compatibility.",
                    character));
    }

    /**
     * {@inheritDoc}
     **/
    @ApiStatus.Internal
    @Override
    public Stack<Integer> getLayoutItemsLayer() {
        return layoutItemsLayer;
    }

    /**
     * {@inheritDoc}
     **/
    @ApiStatus.Internal
    @Override
    public void setLayoutItemsLayer(Stack<Integer> layoutItemsLayer) {
        this.layoutItemsLayer = layoutItemsLayer;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public boolean isLayoutSignatureChecked() {
        return layoutSignatureChecked;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void setLayoutSignatureChecked(boolean layoutSignatureChecked) {
        this.layoutSignatureChecked = layoutSignatureChecked;
    }

    /**
     * Determines the number of rows for the specified view.
     * <p>
     * If the view is a context it uses the number of rows of the {@link ViewContext#getContainer() context's container},
     * if it is a regular view it uses the number of rows of the {@link AbstractView#getType() view's type}.
     *
     * @param view The view.
     * @return The columns count for the given view.
     * @throws IllegalStateException If it is not possible to determine the number of rows for
     *                               the specified view implementation.
     */
    private int determineRowsCount(@NotNull VirtualView view) {
        if (view instanceof ViewContext)
            return ((ViewContext) view).getContainer().getRowsCount();
        if (view instanceof AbstractView) return view.getRows();

        throw new IllegalStateException(String.format(
                "Unsupported view implementation, cannot determine rows count: %s",
                view.getClass().getName()));
    }

    /**
     * Determines the number of columns for the specified view.
     * <p>
     * If the view is a context it uses the number of columns of the {@link ViewContext#getContainer() context's container},
     * if it is a regular view it uses the number of columns of the {@link AbstractView#getType() view's type}.
     *
     * @param view The view.
     * @return The columns count for the given view.
     * @throws IllegalStateException If it is not possible to determine the number of columns for
     *                               the specified view implementation.
     */
    private int determineColumnsCount(@NotNull VirtualView view) {
        if (view instanceof ViewContext)
            return ((ViewContext) view).getContainer().getColumnsCount();
        if (view instanceof AbstractView) return view.getColumns();

        throw new IllegalStateException(String.format(
                "Unsupported view implementation, cannot determine columns count: %s",
                view.getClass().getName()));
    }

    /**
     * Resolves the given layout to the given view.
     * <p>
     * Reads the specified layout, checks if it is within the view size constraints, it defines the
     * layout items layer and determines the page size if the specified view is paginated.
     *
     * @param view   The target view.
     * @param layout The layout to be resolved.
     * @throws IllegalArgumentException  If during resolution a page navigation item is found for a
     *                                   view that is not paginated.
     * @throws IndexOutOfBoundsException If the layout doesn't fit the view's container constraints.
     */
    final void resolveLayout(@NotNull VirtualView view, @NotNull String[] layout) {
        // since the layout is only defined once, we cache it
        // to avoid unnecessary processing every time we update the context.
        final int rows = layout.length;
        final int containerRowsCount = determineRowsCount(view);

        if (rows != containerRowsCount)
            throw new IndexOutOfBoundsException(String.format(
                    "Layout columns must respect the rows count of the container" + " (given: %d, expect: %d)",
                    rows, containerRowsCount));

        final int containerColumnsCount = determineColumnsCount(view);
        final Stack<Integer> itemsLayer = new Stack<>();

        for (int row = 0; row < rows; row++) {
            final String layer = layout[row];

            final int layerLength = layer.length();
            if (layerLength != containerColumnsCount)
                throw new IndexOutOfBoundsException(String.format(
                        "Layout layer length located at %d must respect the columns count of the"
                                + " container (given: %d, expect: %d).",
                        row, layerLength, containerColumnsCount));

            for (int column = 0; column < containerColumnsCount; column++) {
                final int targetSlot = column + (row * containerColumnsCount);
                final char character = layer.charAt(column);
                switch (character) {
                    case LAYOUT_EMPTY_SLOT:
                        break;
                    case LAYOUT_FILLED_SLOT: {
                        itemsLayer.push(targetSlot);
                        break;
                    }
                    case LAYOUT_PREVIOUS_PAGE: {
                        if (!(view instanceof PaginatedViewContext))
                            throw new IllegalArgumentException(String.format(
                                    "Navigation characters (%s) on layout are reserved to paginated views and cannot be used on regular views.",
                                    LAYOUT_PREVIOUS_PAGE + ", " + LAYOUT_NEXT_PAGE));

                        final PaginatedViewContext<?> paginatedContext = (PaginatedViewContext<?>) view;
                        paginatedContext.getRoot().resolveNavigationItem(paginatedContext, NAVIGATE_LEFT);
                        paginatedContext.setPreviousPageItemSlot(targetSlot);
                        break;
                    }
                    case LAYOUT_NEXT_PAGE: {
                        if (!(view instanceof PaginatedViewContext))
                            throw new IllegalArgumentException(String.format(
                                    "Navigation characters (%s) on layout are reserved to paginated views and cannot be used on regular views.",
                                    LAYOUT_PREVIOUS_PAGE + ", " + LAYOUT_NEXT_PAGE));

                        final PaginatedViewContext<?> paginatedContext = (PaginatedViewContext<?>) view;
                        paginatedContext.getRoot().resolveNavigationItem(paginatedContext, NAVIGATE_RIGHT);
                        paginatedContext.setNextPageItemSlot(targetSlot);
                        break;
                    }
                    default: {
                        final LayoutPattern pattern = getLayoutOrNull(character);
                        if (pattern != null) pattern.getSlots().push(targetSlot);
                    }
                }
            }
        }

        view.setLayoutItemsLayer(itemsLayer);
        view.setLayoutSignatureChecked(true);

        if (!(view instanceof PaginatedViewContext)) return;

        final Paginator<?> paginator =
                ((PaginatedViewContext<?>) view).paginated().getPaginator();
        if (paginator == null) return;

        paginator.setPageSize(itemsLayer.size());
    }

    private LayoutPattern getLayoutOrNull(char character) {
        return getLayoutPatterns().stream()
                .filter(pattern -> pattern.getCharacter() == character)
                .findFirst()
                .orElse(null);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    @ApiStatus.Internal
    public Deque<ViewItem> getReservedItems() {
        return reservedItems;
    }
}
