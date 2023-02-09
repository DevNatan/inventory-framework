package me.devnatan.inventoryframework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.devnatan.inventoryframework.component.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

@ApiStatus.Internal
public final class DefaultVirtualViewImpl implements VirtualView {

    private final List<Component> components = new ArrayList<>();
	private final List<IFItem<?>> items = new ArrayList<>();

    @Override
    public @UnmodifiableView List<Component> getComponents() {
        return Collections.unmodifiableList(components);
    }

	@Override
	public @Nullable IFItem<?> getItem(int index) {
		if (items.isEmpty()) return null;
		if (items.size() < index) return null;

		return items.get(index);
	}
}
