package me.saiintbrisson.minecraft;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@RequiredArgsConstructor
@ToString(callSuper = true)
class BaseViewContext extends AbstractVirtualView implements ViewContext {

    private final AbstractView root;
    private final ViewContainer container;
    private final ViewContextAttributes attributes;

    protected BaseViewContext(final @NotNull AbstractView root, final @Nullable ViewContainer container) {
        this.root = root;
        this.container = container;
        this.attributes = new ViewContextAttributes(container);
    }

    @Override
    public final @NotNull List<Viewer> getViewers() {
        synchronized (getAttributes().getViewers()) {
            return Collections.unmodifiableList(getAttributes().getViewers());
        }
    }

    @Override
    public final Map<String, Object> getData() {
        return Collections.unmodifiableMap(getAttributes().getData());
    }

    final void addViewer(@NotNull final Viewer viewer) {
        synchronized (getAttributes().getViewers()) {
            getAttributes().getViewers().add(viewer);
        }
    }

    final void removeViewer(@NotNull final Viewer viewer) {
        synchronized (getAttributes().getViewers()) {
            getAttributes().getViewers().remove(viewer);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> T get(@NotNull final String key) {
        return (T) getAttributes().getData().get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(@NotNull String key, @NotNull Supplier<T> defaultValue) {
        synchronized (getAttributes().getData()) {
            if (!getAttributes().getData().containsKey(key)) {
                final T value = defaultValue.get();
                getAttributes().getData().put(key, value);
                return value;
            }

            return (T) getAttributes().getData().get(key);
        }
    }

    @Override
    public final void set(@NotNull final String key, @NotNull final Object value) {
        synchronized (getAttributes().getData()) {
            getAttributes().getData().put(key, value);
        }
    }

    @Override
    public final boolean has(@NotNull final String key) {
        synchronized (getAttributes().getData()) {
            return getAttributes().getData().containsKey(key);
        }
    }

    @Override
    @NotNull
    public final ViewContainer getContainer() {
        return Objects.requireNonNull(getAttributes().getContainer(), "View context container cannot be null");
    }

    @Override
    public final @NotNull String getTitle() {
        return getAttributes().getUpdatedTitle() != null
                ? getAttributes().getUpdatedTitle()
                : getRoot().getTitle();
    }

    @Override
    public final int getRows() {
        return getContainer().getColumnsCount();
    }

    @Override
    public final int getSize() {
        return getContainer().getSize();
    }

    @Override
    public final void updateTitle(@NotNull final String title) {
        getAttributes().setTitle(title);
    }

    @Override
    public final void resetTitle() {
        getAttributes().setTitle(null);
    }

    @Override
    public final boolean isPropagateErrors() {
        return getAttributes().isPropagateErrors();
    }

    @Override
    public void setPropagateErrors(boolean propagateErrors) {
        getAttributes().setPropagateErrors(propagateErrors);
    }

    /** {@inheritDoc} * */
    @Override
    public final ViewUpdateJob getUpdateJob() {
        ViewUpdateJob ownJob = super.getUpdateJob();
        if (ownJob != null) return ownJob;

        return getRoot().getUpdateJob();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> PaginatedViewContext<T> paginated() {
        if (!(this.getRoot() instanceof PaginatedView))
            throw new IllegalStateException("Only paginated views can enforce paginated view context");

        return (PaginatedViewContext<T>) this;
    }

    @Override
    public final String getUpdatedTitle() {
        return getAttributes().getUpdatedTitle();
    }

    @Override
    public final void update() {
        getRoot().update(this);
    }

    @Override
    public final void close() {
        getAttributes().setMarkedToClose(true);
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

        for (final Viewer viewer : getViewers()) platformViewFrame.open(viewClass, viewer, data);
    }

    @Override
    public @NotNull Player getPlayer() {
        throw new UnsupportedOperationException(
                "This function should not be used on your platform, it is only available for reasons"
                        + " of backward compatibility with the Bukkit platform.");
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        throw new UnsupportedOperationException(
                String.format("This context is not cancellable: %s", getClass().getName()));
    }

    @Override
    public ViewItem resolve(int index, boolean resolveOnRoot) {
        ViewItem item = super.resolve(index);
        if (item == null && resolveOnRoot) return getRoot().resolve(index);

        return item;
    }

    final ViewItem resolve(int index, boolean resolveOnRoot, boolean entityContainer) {
        // fast path -- user is unable to set items on entity container
        if (entityContainer) return null;
        return resolve(index, resolveOnRoot);
    }

    @Override
    public @NotNull ViewSlotContext ref(final String key) {
        ViewItem item = tryResolveRef(this, key);
        if (item == null) item = tryResolveRef(getRoot(), key);
        if (item == null) return null;

        final PlatformViewFrame<?, ?, ?> vf = getRoot().getViewFrame();
        if (vf == null)
            throw new IllegalStateException(
                    "Tried to get a slot reference while context framework was not registered yet");

        return vf.getFactory().createSlotContext(item, this, 0, null);
    }

    private ViewItem tryResolveRef(final AbstractVirtualView view, final String key) {
        for (final ViewItem item : view.getItems()) {
            if (item == null) continue;
            if (item.getReferenceKey() == null) continue;
            if (item.getReferenceKey().equals(key)) return item;
        }
        return null;
    }
}
