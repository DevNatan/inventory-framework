package me.saiintbrisson.minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.saiintbrisson.minecraft.exception.UnknownReferenceException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@ToString(callSuper = true)
public class BaseViewContext extends AbstractVirtualView implements ViewContext {

    private final AbstractView root;
    private final ViewContainer container;

    private final List<Viewer> viewers = new ArrayList<>();
    private final Map<String, Object> data = new HashMap<>();
    private String updatedTitle;
    private boolean propagateErrors = true;
    private boolean markedToClose;
    private boolean cancellationAllowed;
    private boolean cancelled;
    private BaseViewContext previous;

    protected BaseViewContext(final @NotNull AbstractView root, final @Nullable ViewContainer container) {
        this.root = root;
        this.container = container;
    }

    @Override
    public void render() {
        getRoot().render(this);
    }

    @Override
    public void update(@NotNull ViewContext context) {
        getRoot().update(this);
    }

    @ApiStatus.Internal
    public List<Viewer> internalGetViewers() {
        return viewers;
    }

    @Override
    public final @NotNull List<Viewer> getViewers() {
        synchronized (internalGetViewers()) {
            return internalGetViewers();
        }
    }

    @Override
    public final void addViewer(@NotNull final Viewer viewer) {
        synchronized (internalGetViewers()) {
            internalGetViewers().add(viewer);
        }
    }

    @Override
    public final void removeViewer(@NotNull final Viewer viewer) {
        synchronized (internalGetViewers()) {
            internalGetViewers().remove(viewer);
        }
    }

    @Override
    public Map<String, Object> getData() {
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> T get(@NotNull final String key) {
        return (T) getData().get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(@NotNull String key, @NotNull Supplier<T> defaultValue) {
        synchronized (getData()) {
            if (!getData().containsKey(key)) {
                final T value = defaultValue.get();
                getData().put(key, value);
                return value;
            }

            return (T) getData().get(key);
        }
    }

    @Override
    public final void set(@NotNull final String key, @NotNull final Object value) {
        synchronized (getData()) {
            getData().put(key, value);
        }
    }

    @Override
    public final boolean has(@NotNull final String key) {
        synchronized (getData()) {
            return getData().containsKey(key);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> T remove(@NotNull String key) {
        synchronized (getData()) {
            Object value = getData().remove(key);
            if (value != null) return (T) value;
        }
        return null;
    }

    @Override
    @NotNull
    public final ViewContainer getContainer() {
        return container;
    }

    @Override
    public int getRows() {
        return getContainer().getRowsCount();
    }

    @Override
    public int getColumns() {
        return getContainer().getColumnsCount();
    }

    @Override
    public int getSize() {
        return getContainer().getSize();
    }

    @Override
    public final @Nullable String getTitle() {
        return updatedTitle != null ? updatedTitle : getInitialTitle();
    }

    @Override
    public @Nullable String getInitialTitle() {
        return getTitle();
    }

    @Override
    public final @Nullable String getUpdatedTitle() {
        return updatedTitle;
    }

    @Override
    public void updateTitle(@NotNull final String title) {
        this.updatedTitle = title;
        getContainer().changeTitle(title);
    }

    @Override
    public void resetTitle() {
        this.updatedTitle = null;
        getContainer().changeTitle(null);
    }

    @Override
    public boolean isPropagateErrors() {
        return propagateErrors;
    }

    @Override
    public void setPropagateErrors(boolean propagateErrors) {
        this.propagateErrors = propagateErrors;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> PaginatedViewContext<T> paginated() {
        if (!(this.getRoot() instanceof PaginatedView))
            throw new IllegalStateException("Only paginated views can enforce paginated view context");

        return (PaginatedViewContext<T>) this;
    }

    @Override
    public void update() {
        getRoot().update(this);
    }

    @Override
    public void close() {
        markedToClose = true;
    }

    @Override
    @Deprecated
    public void closeNow() {
        closeUninterruptedly();
    }

    @Override
    public void closeUninterruptedly() {
        getContainer().close();
    }

    @Override
    public final void open(@NotNull Class<? extends AbstractView> viewClass) {
        open(viewClass, Collections.emptyMap());
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public final void open(
            @NotNull Class<? extends AbstractView> viewClass, @NotNull Map<String, @Nullable Object> data) {
        final PlatformViewFrame platformViewFrame = Objects.requireNonNull(
                getRoot().getViewFrame(),
                "Fast parent view open by context bridge is only supported if root view is registered under a ViewFrame.");

        for (final Viewer viewer : getViewers())
            platformViewFrame.open(
                    viewClass,
                    viewer,
                    data,
                    this instanceof ViewSlotContext ? ((ViewSlotContext) this).getParent() : this);
    }

    @Override
    public @NotNull Player getPlayer() {
        throw new UnsupportedOperationException(
                "This function should not be used on your platform, it is only available for reasons"
                        + " of backward compatibility with the Bukkit platform.");
    }

    @Override
    public boolean isCancelled() {
        if (!isCancellationAllowed()) return false;

        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        if (cancellationAllowed) {
            this.cancelled = cancelled;
            return;
        }

        throw new UnsupportedOperationException(
                String.format("This context is not cancellable: %s", getClass().getName()));
    }

    @Override
    @ApiStatus.Internal
    public void allowCancellation() {
        cancellationAllowed = !cancellationAllowed;
        if (!cancellationAllowed) cancelled = false;
    }

    @Override
    public ViewItem resolve(int index, boolean resolveOnRoot) {
        ViewItem item = super.resolve(index, resolveOnRoot);
        if (item == null && resolveOnRoot) {
            return getRoot().resolve(index, resolveOnRoot);
        }

        return item;
    }

    final ViewItem resolve(int index, boolean resolveOnRoot, boolean entityContainer) {
        // fast path -- user is unable to set items on entity container
        if (entityContainer) return null;
        return resolve(index, resolveOnRoot);
    }

    @Override
    public @NotNull ViewSlotContext ref(@NotNull String key) {
        ViewItem item = tryResolveRef(this, key);
        if (item == null) item = tryResolveRef(getRoot(), key);
        if (item == null) throw new IllegalArgumentException("No reference found for key: " + key);

        final PlatformViewFrame<?, ?, ?> vf = getRoot().getViewFrame();
        if (vf == null)
            throw new IllegalStateException(
                    "Tried to get a slot reference while context framework was not registered yet");

        return vf.getFactory().createSlotContext(item, this, 0, null);
    }

    @Override
    public @Nullable ViewSlotContext refOrNull(@NotNull String key) {
        try {
            return ref(key);
        } catch (UnknownReferenceException ignored) {
            return null;
        }
    }

    private ViewItem tryResolveRef(final AbstractVirtualView view, final String key) {
        VirtualView target = view instanceof ViewSlotContext ? ((ViewSlotContext) view).getParent() : view;
        for (final ViewItem item : target.getItems()) {
            if (item == null) continue;
            if (item.getReferenceKey() == null) continue;
            if (item.getReferenceKey().equals(key)) return item;
        }
        return null;
    }

    @Override
    @ApiStatus.Internal
    int getNextAvailableSlot() {
        final ViewContainer container = getContainer();

        if (getRoot().isLayoutSignatureChecked()) return ViewItem.AVAILABLE;

        if (!isLayoutSignatureChecked()) {
            for (int i = 0; i < container.getSize(); i++) {
                // fast path -- skip resolution if slot isn't interactable
                if (!container.getType().canPlayerInteractOn(i)) continue;

                // slow path -- resolve slot one by one
                if (resolve(i, true) != null) continue;

                return i;
            }
        }

        return ViewItem.AVAILABLE;
    }

    @Override
    final int convertSlot(int row, int column) {
        ViewContainer container = getContainer();
        return IFUtils.convertSlot(row, column, container.getRowsCount(), container.getColumnsCount());
    }

    public void setPrevious(BaseViewContext previous) {
        if (previous instanceof ViewSlotContext)
            throw new IllegalStateException("Previous context cannot be a slot context.");

        this.previous = previous;
    }

    @Override
    @Nullable
    public ViewContext back() {
        final BaseViewContext prev = getPrevious();
        if (prev == null) return null;

        getRoot().resume(prev, this);
        return prev;
    }
}
