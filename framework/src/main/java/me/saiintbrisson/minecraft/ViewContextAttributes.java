package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
class ViewContextAttributes {

	private final ViewContainer container;

	private final List<Viewer> viewers = new ArrayList<>();

	@Setter(AccessLevel.NONE)
	private String updatedTitle;

	@Setter private boolean propagateErrors = true;
	@Setter private boolean markedToClose;

	private final Map<String, Object> data = new HashMap<>();

	public final void setTitle(@Nullable final String title) {
		updatedTitle = title;
		container.changeTitle(title);
	}

}
