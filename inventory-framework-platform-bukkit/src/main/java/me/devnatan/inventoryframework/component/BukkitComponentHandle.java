package me.devnatan.inventoryframework.component;

import java.util.Objects;
import me.devnatan.inventoryframework.IFDebug;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.ComponentClearContext;
import me.devnatan.inventoryframework.context.ComponentRenderContext;
import me.devnatan.inventoryframework.context.ComponentUpdateContext;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.IFComponentClearContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.PublicComponentRenderContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import org.jetbrains.annotations.ApiStatus;

public abstract class BukkitComponentHandle<T> extends PlatformComponentHandle<Context, BukkitItemComponentBuilder> {

    @ApiStatus.OverrideOnly
    public abstract T builder();

    /**
     * Renders the component in the given context.
     *
     * @param render The context that this component will be rendered on.
     */
    protected abstract void rendered(PublicComponentRenderContext render);

    /**
     * Called when the component is updated in the given context.
     *
     * @param update The update context.
     */
    protected void updated(ComponentUpdateContext update) {}

    /**
     * Called when the component is cleared from the given context.
     *
     * @param clear The context that this component will be cleared from.
     */
    protected void cleared(ComponentClearContext clear) {}

    /**
     * Called when a viewer clicks in the component.
     *
     * @param click The click context.
     */
    protected void clicked(SlotClickContext click) {}

    @Override
    public final void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
        final PipelinePhase phase = Objects.requireNonNull(
                pipeline.getPhase(), "Pipeline phase cannot be null in ComponentHandle interceptor");

        if (phase == Component.RENDER) {
            final ComponentRenderContext context = (ComponentRenderContext) subject;
            final PlatformComponent component = (PlatformComponent) context.getComponent();
            final PublicComponentRenderContext publicContext = new PublicComponentRenderContext(context);
            final int position = component.getPosition();

            if (component.getRenderHandler() != null) {
                component.getRenderHandler().accept(context);
                rendered(publicContext);

                if (position >= 0) context.getContainer().renderItem(component.getPosition(), context.getItem());
                component.setVisible(true);
                return;
            }

            rendered(publicContext);
            if (position >= 0) {
                if (context.getItem() == null) {
                    if (context.getContainer().getType().isResultSlot(position)) {
                        component.setVisible(true);
                        return;
                    }

                    // TODO This error must be in slot creation and not on render
                    //      so the developer will know where the error is
                    if (!component.isSelfManaged())
                        throw new IllegalStateException(
                                "At least one fallback item or render handler must be provided for "
                                        + component.getClass().getName());
                    return;
                }

                context.getContainer().renderItem(position, context.getItem());
            }
            component.setVisible(true);
        }

        if (phase == Component.UPDATE) {
            final ComponentUpdateContext context = (ComponentUpdateContext) subject;
            final PlatformComponent component = (PlatformComponent) context.getComponent();
            updated(context);

            if (context.isCancelled()) return;

            // Static item with no `displayIf` must not even reach the update handler
            if (!component.isSelfManaged()
                    && !context.isForceUpdate()
                    && component.getDisplayCondition() == null
                    && component.getRenderHandler() == null) return;

            if (component.isVisible() && component.getUpdateHandler() != null) {
                component.getUpdateHandler().accept(context);
                if (context.isCancelled()) return;
            }

            if (context.isCancelled()) return;

            ((IFRenderContext) component.getContext()).renderComponent(component);
        }

        if (phase == Component.CLEAR) {
            final IFComponentClearContext context = (IFComponentClearContext) subject;
            if (context.isCancelled()) return;

            final PlatformComponent component = (PlatformComponent) context.getComponent();
            component.getContainer().removeItem(component.getPosition());
            cleared((ComponentClearContext) context);
        }

        if (phase == Component.CLICK) {
            final SlotClickContext context = (SlotClickContext) subject;
            final PlatformComponent component = (PlatformComponent) context.getComponent();
            IFDebug.debug("component: %s", component);
            if (component.getClickHandler() != null) component.getClickHandler().accept(context);

            clicked(context);
            if (context.isCancelled()) return;

            if (component.isUpdateOnClick()) component.update();
            if (component.isCloseOnClick()) context.closeForPlayer();
        }
    }
}
