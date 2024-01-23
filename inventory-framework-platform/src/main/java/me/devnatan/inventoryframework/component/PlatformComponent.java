package me.devnatan.inventoryframework.component;

import static me.devnatan.inventoryframework.pipeline.PipelinePhase.Component.COMPONENT_CLICK;

import java.util.function.Consumer;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.state.StateAccess;
import org.jetbrains.annotations.ApiStatus;

/**
 * Base class for platform-specific components.
 *
 * @param <C> The type of the context associated with this component.
 * @param <B> The type of the builder for creating components.
 */
public abstract class PlatformComponent<C extends IFContext, B> extends AbstractComponent
        implements ComponentComposition, StateAccess<C, B> {

    private C context;
    private boolean cancelOnClick;
    private boolean closeOnClick;
    private boolean updateOnClick;
    private Consumer<? super IFComponentRenderContext> renderHandler;
    private Consumer<? super IFComponentUpdateContext> updateHandler;
    private Consumer<? super IFSlotClickContext> clickHandler;

    PlatformComponent() {
        getPipeline().intercept(COMPONENT_CLICK, new ComponentClickInterceptor());
        getPipeline().intercept(COMPONENT_CLICK, new ComponentCloseOnClickInterceptor());
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
}
