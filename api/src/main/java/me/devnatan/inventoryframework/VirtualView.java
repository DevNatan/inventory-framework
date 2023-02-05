package me.devnatan.inventoryframework;

import java.util.List;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.IFContext;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

/**
 * VirtualView is the basis for creating a view it contains the implementation with methods that are
 * shared between regular views and contexts which are called "unified methods".
 * <p>
 * We call "view" a {@link VirtualView}, "root view" a {@link RootView} and implementations,
 * and "context" a {@link IFContext} and derivations.
 */
public interface VirtualView {

    /**
     * All components in this view.
     *
     * @return An unmodifiable list view of all components in this view.
     */
    @UnmodifiableView
    List<Component> getComponents();

	@Nullable
	IFItem<?> getItem(int index);
}
