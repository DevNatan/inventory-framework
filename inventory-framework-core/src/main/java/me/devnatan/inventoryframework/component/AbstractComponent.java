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
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public abstract class AbstractComponent implements Component {

    private final String key;
    private final VirtualView root;
    private final Ref<Component> reference;
    private final Set<State<?>> watchingStates;
    private final Predicate<? extends IFContext> displayCondition;
    private final Pipeline<VirtualView> pipeline =
            new Pipeline<>(Component.RENDER, Component.UPDATE, Component.CLICK, Component.CLEAR);
    private final boolean isSelfManaged;

    private ComponentHandle handle;
    private boolean isVisible = false;
    private boolean wasForceUpdated;

    protected AbstractComponent(
            String key,
            VirtualView root,
            Ref<Component> reference,
            Set<State<?>> watchingStates,
            Predicate<? extends IFContext> displayCondition,
            boolean isSelfManaged) {
        this.key = key;
        this.root = root;
        this.reference = reference;
        this.watchingStates = watchingStates;
        this.displayCondition = displayCondition;
        this.isSelfManaged = isSelfManaged;
        setHandle(NoopComponentHandle.INSTANCE);
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
    public final IFContext getContext() {
        return getRootAsContext();
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
    public final boolean isSelfManaged() {
        return isSelfManaged;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final boolean shouldRender(IFContext context) {
        return getDisplayCondition() == null || ((Predicate<? super IFContext>) getDisplayCondition()).test(context);
    }

    @Override
    public final void update() {
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

    @Override
    public final @NotNull ComponentHandle getHandle() {
        return handle;
    }

    @Override
    public final void setHandle(ComponentHandle handle) {
        if (this.handle != null) getPipeline().removeInterceptor(this.handle);
        if (handle == null)
            throw new IllegalArgumentException("Component handle argument in #setHandle cannot be null");

        for (final PipelinePhase phase :
                new PipelinePhase[] {Component.RENDER, Component.UPDATE, Component.CLEAR, Component.CLICK}) {

            getPipeline().intercept(phase, handle);
        }
        this.handle = handle;
    }

    @Override
    public final @NotNull Pipeline<VirtualView> getPipeline() {
        return pipeline;
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

    @Override
    public String toString() {
        return "AbstractComponent{" + "key='"
                + key + '\'' + ", root="
                + root + ", reference="
                + reference + ", watchingStates="
                + watchingStates + ", displayCondition="
                + displayCondition + ", pipeline="
                + pipeline + ", handle="
                + handle + ", isVisible="
                + isVisible + ", isSelfManaged="
                + isSelfManaged + ", wasForceUpdated="
                + wasForceUpdated + '}';
    }
}

class NoopComponentHandle extends ComponentHandle {

    static final ComponentHandle INSTANCE = new NoopComponentHandle();

    @Override
    public void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {}
}
