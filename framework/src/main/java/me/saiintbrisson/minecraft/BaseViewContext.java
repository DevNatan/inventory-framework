package me.saiintbrisson.minecraft;

import lombok.*;
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

	public BaseViewContext(@NotNull ViewContext context) {
		this(context.getView(), context.getContainer());
	}

	@Override
	public void addViewer(@NotNull Viewer viewer) {
		synchronized (viewers) {
			viewers.add(viewer);
		}
	}

	@Override
	public void removeViewer(@NotNull Viewer viewer) {
		synchronized (viewers) {
			viewers.remove(viewer);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(@NotNull String key) {
		return (T) data.get(key);
	}

	@Override
	public void set(@NotNull String key, @NotNull Object value) {
		synchronized (data) {
			data.put(key, value);
		}
	}

	@Override
	public boolean has(@NotNull String key) {
		synchronized (data) {
			return data.containsKey(key);
		}
	}

	@Override
	@NotNull
	public ViewContainer getContainer() {
		return Objects.requireNonNull(
			container,
			"View context container cannot be null"
		);
	}

	@Override
	public @NotNull String getTitle() {
		if (updatedTitle != null) return updatedTitle;
		return view.getTitle();
	}

	@Override
	public int getRows() {
		return getContainer().getRowSize();
	}

	@Override
	public void updateTitle(@NotNull String title) {
		updatedTitle = title;
		getContainer().changeTitle(title);
	}

	@Override
	public void resetTitle() {
		updatedTitle = null;
		getContainer().changeTitle(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> PaginatedViewContext<T> paginated() {
		if (!(getView() instanceof PaginatedView))
			throw new IllegalArgumentException("Only paginated views can enforce paginated view context");

		return (PaginatedViewContext<T>) this;
	}

	@Override
	public void close() {
		getContainer().close();
	}

}
