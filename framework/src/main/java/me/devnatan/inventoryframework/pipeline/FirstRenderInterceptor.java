package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentFactory;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateValue;
import me.devnatan.inventoryframework.state.StateValueHost;
import me.devnatan.inventoryframework.state.StateWatcher;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepts the rendering phase of a context and renders all components on it.
 */
public final class FirstRenderInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
        if (!(subject instanceof IFRenderContext)) return;

        final IFRenderContext context = (IFRenderContext) subject;
        registerComponents(context);

        final Viewer viewer = context.getViewer();
        final ElementFactory elementFactory = context.getRoot().getElementFactory();

        for (final Component component : context.getComponents()) {
            final IFSlotRenderContext slotRenderContext = elementFactory.createSlotContext(
                    component.getPosition(),
                    component,
                    context.getContainer(),
                    viewer,
                    context,
                    IFSlotRenderContext.class);
            component.render(slotRenderContext);
            setupWatchers(context, component);
        }
    }

    /**
     * Registers all components set up from {@link IFRenderContext#getComponentFactories()} to
     * the renderization context.
     *
     * @param context The context.
     */
    private void registerComponents(IFRenderContext context) {
        context.getComponentFactories().stream().map(ComponentFactory::create).forEach(context::addComponent);
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
    private void setupWatchers(IFRenderContext context, Component component) {
        for (final State<?> watch : component.getWatchingStates()) {
            final StateWatcher listener;
            if (component instanceof StateWatcher) listener = (StateWatcher) component;
            else
                listener = new StateWatcher() {
                    @Override
                    public void stateRegistered(@NotNull State<?> state, Object caller) {}

                    @Override
                    public void stateUnregistered(@NotNull State<?> state, Object caller) {}

                    @Override
                    public void stateValueInitialized(
                            @NotNull StateValueHost host, @NotNull StateValue value, Object initialValue) {}

                    @Override
                    public void stateValueGet(
                            @NotNull State<?> state,
                            @NotNull StateValueHost host,
                            @NotNull StateValue internalValue,
                            Object rawValue) {}

                    @Override
                    public void stateValueSet(
                            @NotNull StateValueHost host,
                            @NotNull StateValue value,
                            Object rawOldValue,
                            Object rawNewValue) {
                        context.updateRoot();
                    }
                };

            context.watchState(watch.internalId(), listener);
        }
    }
}
