package me.saiintbrisson.minecraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
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
		this((AbstractView) context.getRoot(), context.getContainer());
	}

	@Override
	public final void addViewer(@NotNull final Viewer viewer) {
		synchronized (viewers) {
			viewers.add(viewer);
		}
	}

	@Override
	public final void removeViewer(@NotNull final Viewer viewer) {
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
		return getContainer().getRowSize();
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
	public final void open(@NotNull Object viewer, @NotNull Map<String, Object> data) {
		// workaround to keep backward compatibility
		root.open(root.getViewFrame().getFactory().createViewer(viewer), data);
	}

	@Override
	public Player getPlayer() {
		throw new UnsupportedOperationException();
	}

}
