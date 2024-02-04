package me.devnatan.inventoryframework.component;

import static me.devnatan.inventoryframework.pipeline.PipelinePhase.ComponentPhase.COMPONENT_CLEAR;
import static me.devnatan.inventoryframework.pipeline.PipelinePhase.ComponentPhase.COMPONENT_CLICK;
import static me.devnatan.inventoryframework.pipeline.PipelinePhase.ComponentPhase.COMPONENT_RENDER;
import static me.devnatan.inventoryframework.pipeline.PipelinePhase.ComponentPhase.COMPONENT_UPDATE;

import java.util.Collections;
import java.util.Objects;
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
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public abstract class AbstractComponent implements Component {

    private final Pipeline<IFComponentContext> pipeline = new Pipeline<>(PipelinePhase.ComponentPhase.values());

    private VirtualView root;
    private String key;
    private Ref<Component> reference;
    private Set<State<?>> watchingStates;
    private Predicate<? extends IFContext> displayCondition;
    private boolean selfManaged;
    private boolean visible = true;

    protected AbstractComponent() {
        final Pipeline<IFComponentContext> pipeline = getPipeline();
        pipeline.intercept(COMPONENT_RENDER, ($, ctx) -> render((IFComponentRenderContext) ctx));
        pipeline.intercept(COMPONENT_UPDATE, ($, ctx) -> update((IFComponentUpdateContext) ctx));
        pipeline.intercept(COMPONENT_CLICK, ($, ctx) -> clicked((IFSlotClickContext) ctx));
        pipeline.intercept(COMPONENT_CLEAR, ($, ctx) -> clear((IFComponentClearContext) ctx));
    }

    abstract boolean render(IFComponentRenderContext context);

    abstract boolean update(IFComponentUpdateContext context);

    abstract boolean clear(IFComponentClearContext context);

    abstract boolean clicked(IFSlotClickContext context);

    @Override
    public final String getKey() {
        return key;
    }

    protected final void setKey(String key) {
        this.key = key;
    }

    @Override
    public final @NotNull VirtualView getRoot() {
        return Objects.requireNonNull(root, "ComponentPhase root cannot be null");
    }

    protected final void setRoot(VirtualView root) {
        this.root = root;
    }

    @Override
    public IFContext getContext() {
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

    protected final void setWatchingStates(Set<State<?>> watchingStates) {
        this.watchingStates = watchingStates;
    }

    @Override
    public final boolean isVisible() {
        if (getRoot() instanceof Component) return ((Component) getRoot()).isVisible() && visible;

        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public final boolean isSelfManaged() {
        return selfManaged;
    }

    protected final void setSelfManaged(boolean selfManaged) {
        this.selfManaged = selfManaged;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final boolean shouldRender(IFContext context) {
        return getDisplayCondition() == null || ((Predicate<? super IFContext>) getDisplayCondition()).test(context);
    }

    protected final Predicate<? extends IFContext> getDisplayCondition() {
        return displayCondition;
    }

    protected final void setDisplayCondition(Predicate<? extends IFContext> displayCondition) {
        this.displayCondition = displayCondition;
    }

    @Override
    public final Ref<Component> getReference() {
        return reference;
    }

    protected final void setReference(Ref<Component> reference) {
        this.reference = reference;
    }

    @Override
    public final void show() {
        setVisible(true);
    }

    @Override
    public final void hide() {
        setVisible(false);
    }

    protected final Pipeline<IFComponentContext> getPipeline() {
        return pipeline;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public final void interceptPipelineCall(PipelinePhase phase, PipelineInterceptor<?> interceptor) {
        getPipeline().intercept(phase, (PipelineInterceptor) interceptor);
    }

    // region Internals
    protected final IFRenderContext getRootAsContext() {
        if (getRoot() instanceof AbstractComponent) return ((AbstractComponent) getRoot()).getRootAsContext();
        if (getRoot() instanceof RootView) throw new IllegalStateException("Root is not a context but a regular view");

        return (IFRenderContext) getRoot();
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
                + pipeline + ", isVisible="
                + visible + ", isSelfManaged="
                + selfManaged + '}';
    }
}
