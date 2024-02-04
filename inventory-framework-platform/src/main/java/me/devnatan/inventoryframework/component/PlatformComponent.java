package me.devnatan.inventoryframework.component;

import static me.devnatan.inventoryframework.pipeline.PipelinePhase.Component.COMPONENT_CLICK;

import java.util.function.Consumer;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;
import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.UpdateReason;
import me.devnatan.inventoryframework.context.IFComponentContext;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.state.StateAccess;
import me.devnatan.inventoryframework.state.StateAccessImpl;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for platform-specific components.
 *
 * @param <C> The type of the context associated with this component.
 * @param <B> The type of the builder for creating components.
 */
@Accessors(makeFinal = true)
public abstract class PlatformComponent<C extends IFContext, B> extends AbstractComponent implements StateAccess<C, B> {

    @Delegate
    private final StateAccess<C, B> stateAccess;

	private int position = -1;
    private C context;
    private boolean cancelOnClick;
    private boolean closeOnClick;
    private boolean updateOnClick;
    private Consumer<? super IFComponentRenderContext> renderHandler;
    private Consumer<? super IFComponentUpdateContext> updateHandler;
    private Consumer<? super IFSlotClickContext> clickHandler;

    PlatformComponent() {
		super();
        @SuppressWarnings("rawtypes")
        final PlatformView root = (PlatformView) getRootAsContext().getRoot();
        stateAccess = new StateAccessImpl<>(this, root.getStateRegistry());

        final Pipeline<IFComponentContext> pipeline = getPipeline();
        pipeline.intercept(COMPONENT_CLICK, new ComponentCancelOnClickInterceptor());
        pipeline.intercept(COMPONENT_CLICK, new ComponentCloseOnClickInterceptor());
    }

	@Override
	boolean render(IFComponentRenderContext context) {
		return false;
	}

	@Override
	boolean update(IFComponentUpdateContext context) {
		if (context.isCancelled()) return false;

		// Static item with no `displayIf` must not even reach the update handler
		if (!isSelfManaged()
			&& !context.isForceUpdate()
			&& getDisplayCondition() == null
			&& getRenderHandler() == null) return false;

		if (isVisible() && getUpdateHandler() != null) {
			getUpdateHandler().accept(context);
			if (context.isCancelled()) return false;
		}

		((IFRenderContext) getContext()).renderComponent(this);
		return true;
	}


	@Override
	boolean clicked(IFSlotClickContext context) {
		if (getClickHandler() != null) {
			getClickHandler().accept(context);
			if (context.isCancelled()) return false;
		}

		if (isCloseOnClick()) {
			context.closeForPlayer();
			return true;
		}

		if (isUpdateOnClick())
			context.getParent().updateComponent(this, false, new UpdateReason.UpdateOnClick());
		return true;
	}

	@Override
	public boolean isContainedWithin(int position) {
		return getPosition() == position;
	}

	@Override
	public boolean intersects(@NotNull Component other) {
		throw new UnsupportedOperationException();
	}

	/**
     * Lifecycle event handler for setting up the component.
     * This method is called during the setup phase of the component's lifecycle.
     *
     * @param root   The VirtualView representing the root of the component's view hierarchy.
     * @param config The builder for configuring the component.
     */
    @ApiStatus.OverrideOnly
    protected abstract void onSetup(C root, B config);

    /**
     * Gets the context associated with this component.
     *
     * @return The context associated with this component.
     */
    @Override
    public final C getContext() {
        return context;
    }

    /**
     * Sets the context associated with this component.
     *
     * @param context The new context for this component.
     */
    final void setContext(C context) {
        this.context = context;
    }

    /**
     * Checks if the component should cancel click events.
     *
     * @return True if click events should be canceled, false otherwise.
     */
    public boolean isCancelOnClick() {
        return cancelOnClick;
    }

    /**
     * Sets whether the component should cancel click events.
     *
     * @param cancelOnClick True to cancel click events, false otherwise.
     */
    final void setCancelOnClick(boolean cancelOnClick) {
        this.cancelOnClick = cancelOnClick;
    }

    /**
     * Checks if the component should close on click.
     *
     * @return True if the component should close on click, false otherwise.
     */
    public final boolean isCloseOnClick() {
        return closeOnClick;
    }

    /**
     * Sets whether the component should close on click.
     *
     * @param closeOnClick True to close the component on click, false otherwise.
     */
    final void setCloseOnClick(boolean closeOnClick) {
        this.closeOnClick = closeOnClick;
    }

    /**
     * Checks if the component should update on click.
     *
     * @return True if the component should update on click, false otherwise.
     */
    public final boolean isUpdateOnClick() {
        return updateOnClick;
    }

    /**
     * Sets whether the component should update on click.
     *
     * @param updateOnClick True to update the component on click, false otherwise.
     */
    final void setUpdateOnClick(boolean updateOnClick) {
        this.updateOnClick = updateOnClick;
    }

    /**
     * Gets the render handler for the component.
     *
     * @return The render handler for the component.
     */
    public final Consumer<? super IFComponentRenderContext> getRenderHandler() {
        return renderHandler;
    }

    /**
     * Sets the render handler for the component.
     * The handler is called everytime component is about to render.
     *
     * @param renderHandler The new render handler for the component.
     */
    final void setRenderHandler(Consumer<? super IFComponentRenderContext> renderHandler) {
        this.renderHandler = renderHandler;
    }

    /**
     * Gets the update handler for the component.
     *
     * @return The update handler for the component.
     */
    public final Consumer<? super IFComponentUpdateContext> getUpdateHandler() {
        return updateHandler;
    }

    /**
     * Sets the update handler for the component.
     *
     * @param updateHandler The new update handler for the component.
     */
    final void setUpdateHandler(Consumer<? super IFComponentUpdateContext> updateHandler) {
        this.updateHandler = updateHandler;
    }

    /**
     * Gets the click handler for the component.
     *
     * @return The click handler for the component.
     */
    public final Consumer<? super IFSlotClickContext> getClickHandler() {
        return clickHandler;
    }

    /**
     * Sets the click handler for the component.
     *
     * @param clickHandler The new click handler for the component.
     */
    final void setClickHandler(Consumer<? super IFSlotClickContext> clickHandler) {
        this.clickHandler = clickHandler;
    }

	/**
	 * The position of this component.
	 * <p>
	 * Platform components may not have a defined position if they are not buildable, that is,
	 * if they were "assigned" to a context without the use of a {@link ComponentBuilder}.
	 *
	 * @return The position of this component, returns {@code -1} if no position was set.
	 * @see #hasPosition()
	 */
	public final int getPosition() {
		return position;
	}

	/**
	 * Sets the position of this component.
	 *
	 * @param position The new position of this component.
	 */
	public final void setPosition(int position) {
		this.position = position;
	}

	/**
	 * Checks if a position was set for this component.
	 *
	 * @return If a position was set for this component.
	 */
	public final boolean hasPosition() {
		return getPosition() != -1;
	}
}
