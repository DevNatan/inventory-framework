package me.devnatan.inventoryframework.component;

import java.util.function.Consumer;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import me.devnatan.inventoryframework.state.StateAccess;
import org.jetbrains.annotations.ApiStatus;

public abstract class PlatformComponent<C, B> extends AbstractComponent
        implements ComponentComposition, StateAccess<C, B> {

    private int position;
    private final boolean cancelOnClick;
    private final boolean closeOnClick;
    private final boolean updateOnClick;
    private final Consumer<? super IFComponentRenderContext> renderHandler;
    private final Consumer<? super IFComponentUpdateContext> updateHandler;
    private final Consumer<? super IFSlotClickContext> clickHandler;

    PlatformComponent() {
        getPipeline().intercept(PipelinePhase.Component.COMPONENT_CLICK, new ComponentClickInterceptor());
        getPipeline().intercept(PipelinePhase.Component.COMPONENT_CLICK, new ComponentCloseOnClickInterceptor());
    }

    // region Public API
    @ApiStatus.OverrideOnly
    protected abstract void onSetup(VirtualView root, ViewConfigBuilder config);

    @ApiStatus.OverrideOnly
    abstract void onRender(IFComponentRenderContext context);

    @ApiStatus.OverrideOnly
    abstract void onUpdate(IFComponentUpdateContext context);

    @ApiStatus.OverrideOnly
    abstract void onClick(IFSlotClickContext context);
    // endregion

    // region Public Builder Methods
    public final boolean isCancelOnClick() {
        return cancelOnClick;
    }

    public final boolean isCloseOnClick() {
        return closeOnClick;
    }

    public final boolean isUpdateOnClick() {
        return updateOnClick;
    }
    // endregion

    // region Internal Components API
    @Override
    public final int getPosition() {
        return position;
    }

    @Override
    public final void setPosition(int position) {
        this.position = position;
    }

    @Override
    public boolean isPositionSet() {
        return getPosition() != -1;
    }

    public final Consumer<? super IFComponentRenderContext> getRenderHandler() {
        return renderHandler;
    }

    public final Consumer<? super IFComponentUpdateContext> getUpdateHandler() {
        return updateHandler;
    }

    public final Consumer<? super IFSlotClickContext> getClickHandler() {
        return clickHandler;
    }
    // endregion

    @Override
    public String toString() {
        return "PlatformComponent{position="
                + position + ", cancelOnClick="
                + cancelOnClick + ", closeOnClick="
                + closeOnClick + ", updateOnClick="
                + updateOnClick + ", renderHandler="
                + renderHandler + ", updateHandler="
                + updateHandler + ", clickHandler="
                + clickHandler + "} "
                + super.toString();
    }
}
