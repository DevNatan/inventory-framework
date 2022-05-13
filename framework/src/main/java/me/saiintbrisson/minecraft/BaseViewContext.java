package me.saiintbrisson.minecraft;

import lombok.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
class BaseViewContext extends AbstractVirtualView implements ViewContext {

	private final List<Viewer> viewers = new ArrayList<>();

	@NotNull
	private final View view;

	@Nullable
	private final ViewContainer container;

	@Setter(AccessLevel.NONE)
	private String updatedTitle;

	private boolean propagateErrors;
	private final Map<String, Object> data = new HashMap<>();

	public BaseViewContext(@NotNull final ViewContext context) {
		this(context.getView(), context.getContainer());
	}

	@Override
	public Player getPlayer() {
		throw new UnsupportedOperationException("Player implementation is not supported in this context");
	}

	@Override
	public final Viewer getViewer() {
		if (viewers.isEmpty())
			throw new IllegalStateException("context is not valid, there's no viewers available");

		return viewers.get(0);
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
		return view.getTitle();
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
		if (!(getView() instanceof PaginatedView))
			throw new IllegalArgumentException("Only paginated views can enforce paginated view context");

		return (PaginatedViewContext<T>) this;
	}

	@Override
	public final void close() {
		getContainer().close();
	}

}
