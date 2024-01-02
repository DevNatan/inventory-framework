package me.devnatan.inventoryframework.component;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFComponentClearContext;
import me.devnatan.inventoryframework.context.IFComponentContext;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
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
    private final Pipeline<IFComponentContext> pipeline = new Pipeline<>(PipelinePhase.Component.values());
    private final boolean isSelfManaged;

    private ComponentHandle handle;
    private boolean isVisible = true;
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
    public final Ref<Component> getReference() {
        return reference;
    }

    @Override
    public final void show() {
        setVisible(true);
    }

    @Override
    public final void hide() {
        setVisible(false);
    }

    @Override
    public final @NotNull ComponentHandle getHandle() {
        return handle;
    }

    @Override
    public final void setHandle(ComponentHandle handle) {
        if (handle == null)
            throw new IllegalArgumentException("Component handle argument in #setHandle cannot be null");
        if (this.handle != null) getPipeline().removeInterceptor(this.handle);

        getPipeline().addInterceptor(handle);
        this.handle = handle;
        this.handle.setComponent(this);
    }

    final Pipeline<IFComponentContext> getPipeline() {
        return pipeline;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public final void interceptPipelineCall(PipelinePhase phase, PipelineInterceptor<?> interceptor) {
        getPipeline().intercept(phase, (PipelineInterceptor) interceptor);
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

    @Override
    public void render(IFComponentRenderContext context) {}

    @Override
    public void update(IFComponentUpdateContext context) {}

    @Override
    public void clear(IFComponentClearContext context) {}

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
                + isSelfManaged + '}';
    }
}
