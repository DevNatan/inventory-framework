package me.devnatan.inventoryframework.component;

import java.util.Objects;
import me.devnatan.inventoryframework.context.ComponentClearContext;
import me.devnatan.inventoryframework.context.ComponentUpdateContext;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.IFComponentContext;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.PublicComponentRenderContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import org.jetbrains.annotations.ApiStatus;

public abstract class BukkitComponentHandle<T> extends PlatformComponentHandle<Context, BukkitItemComponentBuilder> {

    protected boolean renderItemOnContainerInDefaultBehavior = true;

    @ApiStatus.OverrideOnly
    public abstract T builder();

    /**
     * If the item must be rendered in the container when {@link #rendered(PublicComponentRenderContext)}
     * default behavior is called, disabling this calls component render handler but do not render
     * the component in the container.
     *
     * @param renderItemOnContainerInDefaultBehavior If the item should be rendered in the container.
     */
    protected final void setRenderItemOnContainerInDefaultBehavior(boolean renderItemOnContainerInDefaultBehavior) {
        this.renderItemOnContainerInDefaultBehavior = renderItemOnContainerInDefaultBehavior;
    }

    /**
     * Renders the component in the given context.
     *
     * @param render The context that this component will be rendered on.
     */
    protected void rendered(PublicComponentRenderContext render) {
        final PlatformComponent component = (PlatformComponent) render.getComponent();
        if (component.getRenderHandler() != null) {
            component.getRenderHandler().accept(render.getConfinedContext());
        }

        if (!renderItemOnContainerInDefaultBehavior) return;

        component.setPosition(render.getSlot());

        if (!component.isPositionSet())
            throw new IllegalStateException("Component position is not set. A position for the component must be "
                    + "assigned via #withSlot(...) in ComponentBuilder or programmatically before render");

        render.getContainer().renderItem(component.getPosition(), render.getItem());
        component.setVisible(true);
    }

    /**
     * Called when the component is updated in the given context.
     *
     * @param update The update context.
     */
    protected void updated(ComponentUpdateContext update) {
        final PlatformComponent component = (PlatformComponent) update.getComponent();

        if (update.isCancelled()) return;

        // Static item with no `displayIf` must not even reach the update handler
        if (!component.isSelfManaged()
                && !update.isForceUpdate()
                && component.getDisplayCondition() == null
                && component.getRenderHandler() == null) return;

        if (component.isVisible() && component.getUpdateHandler() != null) {
            component.getUpdateHandler().accept(update);
            if (update.isCancelled()) return;
        }

        if (update.isCancelled()) return;

        ((IFRenderContext) component.getContext()).renderComponent(component);
    }

    /**
     * Called when the component is cleared from the given context.
     *
     * @param clear The context that this component will be cleared from.
     */
    protected void cleared(ComponentClearContext clear) {
        if (clear.isCancelled()) return;

        final PlatformComponent component = (PlatformComponent) clear.getComponent();
        component.getContainer().removeItem(component.getPosition());
    }

    /**
     * Called when a viewer clicks in the component.
     *
     * @param click The click context.
     */
    protected void clicked(SlotClickContext click) {
        final PlatformComponent component = (PlatformComponent) click.getComponent();
        if (component.getClickHandler() != null) component.getClickHandler().accept(click);

        if (click.isCancelled()) return;

        if (component.isUpdateOnClick()) component.update();
        if (component.isCloseOnClick()) click.closeForPlayer();
    }

    @Override
    public final void intercept(PipelineContext<IFComponentContext> pipeline, IFComponentContext subject) {
        final PipelinePhase.Component phase = (PipelinePhase.Component) Objects.requireNonNull(
                pipeline.getPhase(), "Pipeline phase cannot be null in ComponentHandle interceptor");

        switch (phase) {
            case COMPONENT_RENDER:
                rendered(new PublicComponentRenderContext((IFComponentRenderContext) subject));
                break;
            case COMPONENT_UPDATE:
                updated((ComponentUpdateContext) subject);
                break;
            case COMPONENT_CLICK:
                clicked((SlotClickContext) subject);
                break;
            case COMPONENT_CLEAR:
                cleared((ComponentClearContext) subject);
                break;
        }
    }
}
