package me.devnatan.inventoryframework.context;

import static me.devnatan.inventoryframework.IFDebug.debug;

import java.util.Iterator;
import java.util.List;
import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.UpdateReason;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentBuilder;
import me.devnatan.inventoryframework.component.ComponentContainer;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateWatcher;

/**
 * Intercepts the rendering phase of a context and renders all components on it.
 */
final class ContextFirstRenderInterceptor implements PipelineInterceptor<IFContext> {

    @Override
    public void intercept(PipelineContext<IFContext> pipeline, IFContext subject) {
        if (!(subject instanceof IFRenderContext)) return;

        final IFRenderContext context = (IFRenderContext) subject;
        registerComponents(context);

        final List<Component> componentList = context.getComponents();

        for (int i = componentList.size(); i > 0; i--) {
            final Component component = componentList.get(i - 1);
            watchStates(context, component);
            context.renderComponent(component);

            if (component instanceof ComponentContainer) {
                for (final Component child : ((ComponentContainer) component).getComponents()) {
                    watchStates(context, child);
                }
            }
        }
    }

    /**
     * Registers all components set up from {@link IFRenderContext#getNotRenderedComponents() component factories}
     * to the rendering context.
     *
     * @param context The context.
     */
    private void registerComponents(IFRenderContext context) {
        final Iterator<ComponentBuilder> iterator =
                context.getNotRenderedComponents().iterator();
        while (iterator.hasNext()) {
            final ComponentBuilder builder = iterator.next();
            final Component component = builder.buildComponent(context);
            assignReference(component);
            context.addComponent(component);
            iterator.remove();
        }
    }

    /**
     * Assigns the component reference
     *
     * @param component The component to assign the reference.
     */
    private void assignReference(Component component) {
        final Ref<Component> ref = component.getReference();
        if (ref == null) return;

        ref.assign(component);
        debug("Reference assigned to %s", component.getClass().getSimpleName());
    }

    /**
     * Registers all components as listeners of the states they want to watch to.
     * <p>
     * If the component is a {@link StateWatcher} the component itself is registered as
     * the state watcher.
     *
     * @param context   The context.
     * @param component The component.
     */
    private void watchStates(IFRenderContext context, Component component) {
        for (final State<?> state : component.getWatchingStates()) {
            context.watchState(
                    state.internalId(),
                    (pipeline, subject) ->
                            context.updateComponent(component, false, new UpdateReason.StateWatch(state)));
        }
    }
}
