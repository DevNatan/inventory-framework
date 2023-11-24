package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.*;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentComposition;
import me.devnatan.inventoryframework.component.ComponentContainer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public abstract class PlatformContext extends AbstractIFContext implements ComponentContainer {

    private boolean endless;
    private boolean active = true;

    PlatformContext() {}

    @SuppressWarnings("rawtypes")
    @Override
    public abstract @NotNull PlatformView getRoot();

	// region Title Update
    /**
     * The actual title of this context.
     * <p>
     * If the title has been dynamically changed, it will return the {@link #getUpdatedTitle() updated title}.
     *
     * @return The updated title, the current title of this view, if <code>null</code> will return
     * the default title for this view type.
     */
    @NotNull
    public String getTitle() {
        return getUpdatedTitle() == null ? getInitialTitle() : getUpdatedTitle();
    }

    /**
     * Title that has been {@link #updateTitleForEveryone(String) dynamically changed} in this context.
     *
     * @return The updated title or null if it wasn't updated.
     * @see #updateTitleForEveryone(String)
     */
    @Nullable
    public final String getUpdatedTitle() {
        return getContainerOrThrow().getTitle();
    }

    @Override
    public final void updateTitleForEveryone(@NotNull String title) {
        for (final Viewer viewer : getViewers()) getContainerOrThrow().changeTitle(title, viewer);
    }

    @Override
    public final void resetTitleForEveryone() {
        for (final Viewer viewer : getViewers()) getContainerOrThrow().changeTitle(null, viewer);
    }
	// endregion

	// region Open & Close
    @Override
    public final void closeForEveryone() {
        getContainerOrThrow().close();
    }

    @Override
    public final void openForEveryone(@NotNull Class<? extends RootView> other) {
        openForEveryone(other, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void openForEveryone(@NotNull Class<? extends RootView> other, Object initialData) {
        getRoot().navigateTo(other, (IFRenderContext) this, initialData);
    }
	// endregion

	// region Internal Context API
    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean isEndless() {
        return endless;
    }

    @Override
    public void setEndless(boolean endless) {
        this.endless = endless;
    }

	/**
	 * Tries to get a container from the current context or throws an exception if not available.
	 * @return The container of this context.
	 * @throws InventoryFrameworkException If there's no container available in the current context.
	 */
	protected final @NotNull ViewContainer getContainerOrThrow() {
		if (this instanceof IFRenderContext) return ((IFRenderContext) this).getContainer();
		if (this instanceof IFCloseContext) return ((IFCloseContext) this).getContainer();
		if (this instanceof IFSlotContext) return ((IFSlotContext) this).getContainer();

		throw new InventoryFrameworkException(String.format(
			"Container is not available in the current context: %s",
			getClass().getName()));
	}
	// endregion

	// region Internal Components Rendering
	/**
	 * Renders a component in this context.
	 *
	 * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided.</i></b>
	 *
	 * @param component The component to be rendered.
	 */
	@ApiStatus.Internal
	public final void renderComponent(@NotNull Component component) {
		if (!component.shouldRender(this)) {
			component.setVisible(false);

			final Optional<Component> overlapOptional = getOverlappingComponentToRender(this, component);
			if (overlapOptional.isPresent()) {
				Component overlap = overlapOptional.get();
				renderComponent(overlap);

				if (overlap.isVisible()) return;
			}

			component.cleared(this);
			clearComponent(component);
			return;
		}

		component.render(createComponentRenderContext(component, false));
	}

	/**
	 * Updates a component in this context.
	 *
	 * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided.</i></b>
	 *
	 * @param component The component to be updated.
	 * @param force If update should be forced.
	 * @param reason Reason why the component was updated.
	 */
	@ApiStatus.Internal
	public final void updateComponent(Component component, boolean force, UpdateReason reason) {
		component.updated(createComponentUpdateContext(component, force, reason));
	}

	/**
	 * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided.</i></b>
	 *
	 * @param component The component to be cleared.
	 */
	@ApiStatus.Internal
	public final void clearComponent(@NotNull Component component) {}

	/**
	 * Creates a IFComponentRenderContext for the current platform.
	 *
	 * @param component The component.
	 * @param force If the context was created due to usage of forceRender().
	 * @return A new IFComponentRenderContext instance.
	 */
	@ApiStatus.Internal
	protected abstract IFComponentRenderContext createComponentRenderContext(
		Component component,
		boolean force
	);

	/**
	 * Creates a IFComponentUpdateContext for the current platform.
	 *
	 * @param component The component.
	 * @param force If the context was created due to usage of forceUpdate().
	 * @param reason Reason why this component was updated.
	 * @return A new IFComponentUpdateContext instance.
	 */
	@ApiStatus.Internal
	protected abstract IFComponentUpdateContext createComponentUpdateContext(
		Component component,
		boolean force,
		UpdateReason reason
	);

	private Optional<Component> getOverlappingComponentToRender(ComponentContainer container, Component subject) {
		// TODO Support recursive overlapping (more than two components overlapping each other)
		for (final Component child : container.getInternalComponents()) {
			if (!child.isVisible()) continue;
			if (child.getKey().equals(subject.getKey())) continue;
			if (child instanceof ComponentComposition) {
				// This prevents from child being compared with its own root that would cause an
				// infinite rendering loop causing the root being re-rendered entirely, thus the
				// child, because child always intersects with its root since it is inside it
				if (subject.getRoot() instanceof Component
					&& child.getKey().equals(((Component) subject.getRoot()).getKey())) {
					continue;
				}

				// We skip ComponentComposition here because is expected to ComponentComposition,
				// on its render handler use #renderComponent to render its children so each
				// child will have its own overlapping checks
				for (final Component deepChild : ((ComponentComposition) child).getInternalComponents()) {
					if (!deepChild.isVisible()) continue;
					if (deepChild.intersects(subject)) return Optional.of(deepChild);
				}

				// Ignore ComponentComposition, we want to check intersections only with children
				continue;
			}

			if (child.intersects(subject)) return Optional.of(child);
		}

		return Optional.empty();
	}
	// endregion

	@Override
	public String toString() {
		return "PlatformContext{" + "endless=" + endless + ", active=" + active + "} " + super.toString();
	}
}
