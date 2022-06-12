package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@Getter
@Setter
@RequiredArgsConstructor
@ToString(callSuper = true)
class BaseViewContext extends AbstractVirtualView implements ViewContext {

	private final AbstractView root;
	private final ViewContainer container;
	private final ViewContextAttributes attributes;

	protected BaseViewContext(
		final @NotNull AbstractView root,
		final @Nullable ViewContainer container
	) {
		this.root = root;
		this.container = container;
		this.attributes = new ViewContextAttributes(container);
	}

	@Override
	public final @NotNull List<Viewer> getViewers() {
		return getAttributes().getViewers();
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
		return Objects.requireNonNull(
			getAttributes().getContainer(),
			"View context container cannot be null"
		);
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
	public final void setPropagateErrors(boolean propagateErrors) {
		getAttributes().setPropagateErrors(propagateErrors);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <T> PaginatedViewContext<T> paginated() {
		if (!(this.getRoot() instanceof PaginatedView))
			throw new IllegalArgumentException("Only paginated views can enforce paginated view context");

		return (PaginatedViewContext<T>) this;
	}

	@Override
	public final String getUpdatedTitle() {
		return getAttributes().getUpdatedTitle();
	}

	@Override
	public final void close() {
		getAttributes().setMarkedToClose(true);
	}

	@Override
	public void closeUninterruptedly() {
		getContainer().close();
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public final void open(@NotNull Class<? extends AbstractView> viewClass) {
		final PlatformViewFrame platformViewFrame = getRoot().getViewFrame();
		for (final Viewer viewer : getViewers())
			platformViewFrame.open(viewClass, viewer);
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public final void open(@NotNull Class<? extends AbstractView> viewClass, @NotNull Map<String, @Nullable Object> data) {
		final PlatformViewFrame platformViewFrame = getRoot().getViewFrame();
		for (final Viewer viewer : getViewers())
			platformViewFrame.open(viewClass, viewer, data);
	}

	@Override
	public Player getPlayer() {
		throw new UnsupportedOperationException(
			"This function should not be used on your platform, it is only available for reasons" +
				" of backward compatibility with the Bukkit platform."
		);
	}

	@Override
	public boolean isCancelled() {
		throw new UnsupportedOperationException("This context is not cancellable");
	}

	@Override
	public void setCancelled(boolean cancelled) {
		throw new UnsupportedOperationException("This context is not cancellable");
	}

	final ViewItem resolve(int index, boolean resolveOnRoot) {
		ViewItem item = super.resolve(index);
		if (item == null && resolveOnRoot)
			return getRoot().resolve(index);

		return item;
	}

}