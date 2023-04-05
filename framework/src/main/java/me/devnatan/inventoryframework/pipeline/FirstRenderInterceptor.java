package me.devnatan.inventoryframework.pipeline;

import java.util.Set;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentFactory;
import me.devnatan.inventoryframework.component.ItemComponent;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateManagementListener;
import me.devnatan.inventoryframework.state.StateValue;
import me.devnatan.inventoryframework.state.StateValueHost;
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

    private void registerComponents(IFRenderContext context) {
        context.getComponentFactories().stream().map(ComponentFactory::create).forEach(context::addComponent);
    }

    private void setupWatchers(IFRenderContext context, Component component) {
        if (!(component instanceof ItemComponent)) return;

        final Set<State<?>> watches = ((ItemComponent) component).getWatching();
        for (final State<?> watch : watches) {
            context.watchState(watch.internalId(), new StateManagementListener() {
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
            });
        }
    }
}
