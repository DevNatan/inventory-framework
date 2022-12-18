package me.saiintbrisson.minecraft;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.devnatan.inventoryframework.IFContext;
import me.devnatan.inventoryframework.VirtualView;
import me.saiintbrisson.minecraft.event.EventBus;
import me.saiintbrisson.minecraft.event.EventListener;
import me.saiintbrisson.minecraft.event.EventSubscription;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class AbstractVirtualView implements VirtualView {

    @ToString.Exclude
    private ViewItem[] items;

    private ViewErrorHandler errorHandler;
    private final List<LayoutPattern> layoutPatterns = new ArrayList<>();
    private String[] layout;
    private Stack<Integer> layoutItemsLayer;
    private boolean layoutSignatureChecked;
    private Deque<ViewItem> reservedItems;
    int reservedItemsCount;

    final EventBus eventBus = new EventBus();

    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public ViewItem[] getItems() {
        return items;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ViewItem getItem(int index) {
        return getItems()[index];
    }

    @Override
    public final void setItems(ViewItem[] items) {
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
    public void setErrorHandler(ViewErrorHandler errorHandler) {
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
    @ApiStatus.Internal
    public final void apply(@Nullable ViewItem item, int slot) {
        if (getItems() == null) throw new IllegalStateException("VirtualView was not initialized yet");

        getItems()[slot] = item;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public final ViewItem slot(int slot) {
        return slot(slot, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public ViewItem slot(int slot, Object item) {
        inventoryModificationTriggered();

        final ViewItem viewItem = new ViewItem(slot).withItem(item);
        apply(viewItem, slot);
        return viewItem;
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
    public final @NotNull ViewItem availableSlot() {
        return availableSlot(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull ViewItem availableSlot(Object item) {
        final int slot = getNextAvailableSlot();

        // item slot will be resolved after layout resolution
        if (slot == ViewItem.AVAILABLE) {
            final ViewItem viewItem = new ViewItem(slot).withItem(item);
            if (getReservedItems() == null) reservedItems = new ArrayDeque<>();

            getReservedItems().add(viewItem);
            return viewItem;
        }

        return slot(slot, item);
    }

    /**
     * Determines the next available slot.
     *
     * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @return The next available slot.
     */
    @ApiStatus.Internal
    abstract int getNextAvailableSlot();

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        throw new UnsupportedOperationException("This view cannot render itself");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(@NotNull IFContext context) {
        throw new UnsupportedOperationException("This view cannot render");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        throw new UnsupportedOperationException("This view cannot update itself");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(@NotNull IFContext context) {
        throw new UnsupportedOperationException("This view cannot update");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ApiStatus.Internal
    public ViewItem resolve(int index, boolean resolveOnRoot) {
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
     *
     * {@inheritDoc}
     */
    @ApiStatus.Internal
    @Override
    public void inventoryModificationTriggered() {}

    public final void runCatching(IFContext context, @NotNull Runnable runnable) {
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

    boolean throwException(final IFContext context, @NotNull final Exception exception) throws Exception {
        if (context != null && context.getErrorHandler() != null) {
            context.getErrorHandler().error(context, exception);
            if (!context.isPropagateErrors()) return false;
        }

        launchError(getErrorHandler(), context, exception);
        return true;
    }

    protected final void launchError(ViewErrorHandler errorHandler, IFContext context, @NotNull Exception exception)
            throws Exception {
        if (errorHandler == null) return;

        errorHandler.error(context, exception);
    }

    private void tryRunOrFail(final IFContext context, @NotNull final Runnable runnable) {
        try {
            runnable.run();
        } catch (final Exception e) {
            try {
                throwException(context, e);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Returns the slot associated with the specified row and column.
     *
     * @param row    The rows count.
     * @param column The columns count.
     * @return The slot position based in specified row and column.
     */
    abstract int convertSlot(int row, int column);

    /**
     * {@inheritDoc}
     */
    @Override
    @ApiStatus.Internal
    public List<LayoutPattern> getLayoutPatterns() {
        return layoutPatterns;
    }

    /**
     * {@inheritDoc}
     */
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
        if (getLayout() != null) throw new IllegalStateException("Layout can only be set once.");

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

        layoutPatterns.add(new LayoutPattern(character, ($) -> factory.get()));
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

    /**
     * {@inheritDoc}
     **/
    @Override
    public void setLayout(char character, BiConsumer<Integer, ViewItem> factory) {
        checkReservedLayoutCharacter(character);

        if (factory == null) {
            layoutPatterns.removeIf(pattern -> pattern.getCharacter() == character);
            return;
        }

        layoutPatterns.add(new LayoutPattern(character, index -> new ViewItem()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ApiStatus.Internal
    public Stack<Integer> getLayoutItemsLayer() {
        return layoutItemsLayer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ApiStatus.Internal
    public void setLayoutItemsLayer(Stack<Integer> layoutItemsLayer) {
        this.layoutItemsLayer = layoutItemsLayer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ApiStatus.Internal
    public boolean isLayoutSignatureChecked() {
        return layoutSignatureChecked;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ApiStatus.Internal
    public void setLayoutSignatureChecked(boolean layoutSignatureChecked) {
        this.layoutSignatureChecked = layoutSignatureChecked;
    }

    @Override
    @ApiStatus.Internal
    public Deque<ViewItem> getReservedItems() {
        return reservedItems;
    }

    @Override
    @ApiStatus.Internal
    public int getReservedItemsCount() {
        return reservedItemsCount;
    }

    @Override
    @ApiStatus.Internal
    public void setReservedItemsCount(int reservedItemsCount) {
        this.reservedItemsCount = reservedItemsCount;
    }

    /**
     * Throws an exception if a character is a reserved layout character.
     *
     * @param character The character.
     * @throws IllegalArgumentException If the character is reserved.
     */
    private static void checkReservedLayoutCharacter(char character) {
        if (!(character == LAYOUT_EMPTY_SLOT
                || character == LAYOUT_FILLED_SLOT
                || character == LAYOUT_PREVIOUS_PAGE
                || character == LAYOUT_NEXT_PAGE)) return;

        throw new IllegalArgumentException(String.format(
                "The \"%c\" character is reserved in layouts and cannot be used due to backwards compatibility.",
                character));
    }

    @Override
    public <T> EventSubscription on(@NotNull Class<? extends T> event, @NotNull EventListener<T> listener) {
        return getEventBus().listen(event, listener);
    }

    @Override
    public <T> EventSubscription on(@NotNull String event, @NotNull EventListener<T> listener) {
        return getEventBus().listen(event, listener);
    }

    @Override
    public void emit(@NotNull Object event) {
        getEventBus().emit(event, null);
    }

    @Override
    public void emit(@NotNull String event, Object value) {
        getEventBus().emit(event, value);
    }
}
