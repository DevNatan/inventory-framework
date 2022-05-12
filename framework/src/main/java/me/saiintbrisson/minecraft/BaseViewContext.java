package me.saiintbrisson.minecraft;

import lombok.*;
import org.jetbrains.annotations.NotNull;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
class BaseViewContext extends AbstractVirtualView implements ViewContext {

	@NotNull
	private final View view;
	@NotNull
	private final ViewContainer container;

	@Setter(AccessLevel.NONE)
	private String updatedTitle;

	private boolean propagateErrors;

	public BaseViewContext(@NotNull ViewContext context) {
		this(context.getView(), context.getContainer());
	}

	@Override
	public @NotNull String getTitle() {
		if (updatedTitle != null) return updatedTitle;
		return view.getTitle();
	}

	@Override
	public int getRows() {
		return container.getRowSize();
	}

	@Override
	public void updateTitle(@NotNull String title) {
		updatedTitle = title;
		container.changeTitle(title);
	}

	@Override
	public void resetTitle() {
		updatedTitle = null;
		container.changeTitle(null);
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
		container.close();
	}

}
