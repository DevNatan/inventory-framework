package me.devnatan.inventoryframework.component;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public abstract class AbstractComponent implements Component {

    private final String key;
    private final VirtualView root;
    private final Ref<Component> reference;
    private final Set<State<?>> watchingStates;
    private final Predicate<? extends IFContext> displayCondition;

    private boolean isVisible;
    private boolean wasForceUpdated;

    protected AbstractComponent(
            String key,
            VirtualView root,
            Ref<Component> reference,
            Set<State<?>> watchingStates,
            Predicate<? extends IFContext> displayCondition) {
        this.key = key;
        this.root = root;
        this.reference = reference;
        this.watchingStates = watchingStates;
        this.displayCondition = displayCondition;
    }

    @Override
    public final String getKey() {
        return key;
    }

    @Override
    public final @NotNull VirtualView getRoot() {
        return root;
    }

    @Override
    public final ViewContainer getContainer() {
        return getRootAsContext().getContainer();
    }

    @Override
    public final @UnmodifiableView Set<State<?>> getWatchingStates() {
        return Collections.unmodifiableSet(watchingStates);
    }

    @Override
    public final boolean isVisible() {
        if (getRoot() instanceof Component) return ((Component) getRoot()).isVisible() && isVisible;

        return isVisible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    @Override
    public final boolean isManagedExternally() {
        // TODO remove this from API
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final boolean shouldRender(IFContext context) {
        return displayCondition == null || ((Predicate<? super IFContext>) displayCondition).test(context);
    }

    @Override
    public final void update() {
        if (isManagedExternally())
            throw new IllegalStateException(
                    "This component is externally managed by another component and cannot be updated directly");

        getRootAsContext().updateComponent(this, false, null);
    }

    @Override
    public final Ref<Component> getReference() {
        return reference;
    }

    @Override
    public final void forceUpdate() {
        wasForceUpdated = true;
        getRootAsContext().updateComponent(this, true, null);
        wasForceUpdated = false;
    }

    @Override
    public final void show() {
        setVisible(true);
        update();
    }

    @Override
    public final void hide() {
        setVisible(false);
        update();
    }

    protected final Predicate<? extends IFContext> getDisplayCondition() {
        return displayCondition;
    }

    // region Internals
    protected final IFRenderContext getRootAsContext() {
        if (getRoot() instanceof AbstractComponent) return ((AbstractComponent) getRoot()).getRootAsContext();
        if (getRoot() instanceof RootView) throw new IllegalStateException("Root is not a context but a regular view");

        return (IFRenderContext) getRoot();
    }

    protected final boolean wasForceUpdated() {
        return wasForceUpdated;
    }
    // endregion
}
