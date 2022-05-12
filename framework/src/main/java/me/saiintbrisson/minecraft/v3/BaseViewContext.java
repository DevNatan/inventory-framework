package me.saiintbrisson.minecraft.v3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString
@Getter
@RequiredArgsConstructor
class BaseViewContext extends AbstractVirtualView implements ViewContext {

	private final View view;

	@NotNull
	private final ViewContainer container;

	private String updatedTitle;

	@Override
	public @NotNull String getTitle() {
		if (updatedTitle != null) return updatedTitle;
		return view.getTitle();
	}

	@Override
	public int getRows() {
		return 0;
	}

	@Override
	public void updateTitle(@NotNull String title) {
		updatedTitle = title;
	}

	@Override
	public void resetTitle() {
		updatedTitle = null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> PaginatedViewContext<T> paginated() {
		if (!(getView() instanceof PaginatedView))
			throw new IllegalArgumentException("Only paginated views can enforce paginated view context");

		return (PaginatedViewContext<T>) this;
	}
}
