package me.devnatan.inventoryframework.pipeline;

import java.util.List;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentFactory;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
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
        final List<Component> componentList = context.getComponents();

        for (int i = componentList.size(); i > 0; i--) {
            final Component component = componentList.get(i - 1);
            context.renderComponent(component);
        }
    }

    /**
     * Registers all components set up from {@link IFRenderContext#getComponentFactories() component factories}
     * to the rendering context.
     *
     * @param context The context.
     */
    private void registerComponents(IFRenderContext context) {
        context.getComponentFactories().stream()
                .map(ComponentFactory::create)
                // TODO Setup watches on context initialization not on first render
                .peek(component -> setupWatchers(context, component))
                .forEach(context::addComponent);
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
        for (final State<?> stateBeingWatched : component.getWatchingStates()) {
            final StateWatcher watcher;
            if (component instanceof StateWatcher) watcher = (StateWatcher) component;
            else watcher = new SingleComponentStateWatcherUpdater(context, component);

            context.watchState(stateBeingWatched.internalId(), watcher);
        }
    }
}

class SingleComponentStateWatcherUpdater implements StateWatcher {

    private final IFContext root;
    private final Component componentToUpdate;

    public SingleComponentStateWatcherUpdater(IFContext root, Component componentToUpdate) {
        this.root = root;
        this.componentToUpdate = componentToUpdate;
    }

    @Override
    public void stateRegistered(@NotNull State<?> state, Object caller) {}

    @Override
    public void stateUnregistered(@NotNull State<?> state, Object caller) {}

    @Override
    public void stateValueGet(
            @NotNull State<?> state,
            @NotNull StateValueHost host,
            @NotNull StateValue internalValue,
            Object rawValue) {}

    @Override
    public void stateValueSet(
            @NotNull StateValueHost host, @NotNull StateValue value, Object rawOldValue, Object rawNewValue) {
        root.updateComponent(componentToUpdate);
    }

    @Override
    public String toString() {
        return "SingleComponentStateWatcherUpdater{" + "root="
                + root + ", componentToUpdate="
                + componentToUpdate + '}';
    }
}
