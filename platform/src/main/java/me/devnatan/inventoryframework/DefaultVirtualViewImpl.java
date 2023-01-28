package me.devnatan.inventoryframework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.devnatan.inventoryframework.component.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnmodifiableView;

@ApiStatus.Internal
public final class DefaultVirtualViewImpl implements VirtualView {

    private final List<Component> components = new ArrayList<>();

    @Override
    public @UnmodifiableView List<Component> getComponents() {
        return Collections.unmodifiableList(components);
    }
}
