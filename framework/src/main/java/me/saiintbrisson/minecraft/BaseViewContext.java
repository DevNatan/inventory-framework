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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@Getter
@Setter
@RequiredArgsConstructor
@ToString(callSuper = true)
class BaseViewContext extends AbstractVirtualView implements ViewContext {

	private final List<Viewer> viewers = new ArrayList<>();

	@NotNull
	private final AbstractView root;

	@Nullable
	private final ViewContainer container;

	@Setter(AccessLevel.NONE)
	private String updatedTitle;

	private boolean propagateErrors;
	private final Map<String, Object> data = new HashMap<>();

	public BaseViewContext(@NotNull final ViewContext context) {
		this(context.getRoot(), context.getContainer());
	}

	final void addViewer(@NotNull final Viewer viewer) {
		synchronized (viewers) {
			viewers.add(viewer);
		}
	}

	final void removeViewer(@NotNull final Viewer viewer) {
		synchronized (viewers) {
			viewers.remove(viewer);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public final <T> T get(@NotNull final String key) {
		return (T) data.get(key);
	}

	@Override
	public <T> T get(@NotNull String key, @NotNull Supplier<T> defaultValue) {
		synchronized (data) {
			if (!data.containsKey(key)) {
				final T value = defaultValue.get();
				data.put(key, value);
				return value;
			}

			return (T) data.get(key);
		}
	}

	@Override
	public final void set(@NotNull final String key, @NotNull final Object value) {
		synchronized (data) {
			data.put(key, value);
		}
	}

	@Override
	public final boolean has(@NotNull final String key) {
		synchronized (data) {
			return data.containsKey(key);
		}
	}

	@Override
	@NotNull
	public final ViewContainer getContainer() {
		return Objects.requireNonNull(
			container,
			"View context container cannot be null"
		);
	}

	@Override
	public final @NotNull String getTitle() {
		if (updatedTitle != null) return updatedTitle;
		return root.getTitle();
	}

	@Override
	public final int getRows() {
		return getContainer().getColumnsCount();
	}

	@Override
	public final void updateTitle(@NotNull final String title) {
		updatedTitle = title;
		getContainer().changeTitle(title);
	}

	@Override
	public final void resetTitle() {
		updatedTitle = null;
		getContainer().changeTitle(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <T> PaginatedViewContext<T> paginated() {
		if (!(this.getRoot() instanceof PaginatedView))
			throw new IllegalArgumentException("Only paginated views can enforce paginated view context");

		return (PaginatedViewContext<T>) this;
	}

	@Override
	public final void close() {
		getContainer().close();
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public final void open(@NotNull Class<? extends AbstractView> viewClass) {
		final PlatformViewFrame platformViewFrame = root.getViewFrame();
		for (final Viewer viewer : getViewers())
			platformViewFrame.open(viewClass, viewer);
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public final void open(@NotNull Class<? extends AbstractView> viewClass, @NotNull Map<String, @Nullable Object> data) {
		final PlatformViewFrame platformViewFrame = root.getViewFrame();
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

}
