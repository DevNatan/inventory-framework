package me.devnatan.inventoryframework.component;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.utils.SlotConverter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public abstract class PlatformComponentBuilder<SELF, CONTEXT> extends AbstractComponentBuilder<SELF> {

    private int position = -1, row = -1, column = -1;
    private boolean cancelOnClick, closeOnClick, updateOnClick;
    protected Consumer<? super IFComponentRenderContext> renderHandler;
    protected Consumer<? super IFComponentUpdateContext> updateHandler;
    protected Consumer<? super IFSlotClickContext> clickHandler;

    protected PlatformComponentBuilder() {}

    // region Public ComponentBuilder API
    public final SELF withSlot(int slot) {
        this.position = slot;
        return (SELF) this;
    }

    public final SELF withSlot(int row, int column) {
        this.row = row;
        this.column = column;
        return (SELF) this;
    }

    /**
     * Updates the current context when a player clicks on this component.
     *
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @return This component builder.
     */
    @ApiStatus.Experimental
    public final SELF updateOnClick() {
        updateOnClick = !updateOnClick;
        return (SELF) this;
    }

    /**
     * Determines whether an actor's click interaction event under this component should be canceled.
     * <p>
     * This method is a shortcut to:
     * <pre>{@code
     * onClick(click -> click.setCancelled(!click.isCancelled());
     * }</pre>
     *
     * @return This component builder.
     */
    public final SELF cancelOnClick() {
        cancelOnClick = !cancelOnClick;
        return (SELF) this;
    }

    /**
     * Closes the current container when an actor interacts with this component.
     * <p>
     * This function was created to support actions during closing to simplify code readability,
     * it executes something and then closes or vice versa.
     * <pre>{@code
     * closeOnClick().onClick(click -> ...)
     * }</pre>
     * <p>
     * This method is a shortcut to:
     * <pre>{@code
     * onClick(IFContext::close);
     * }</pre>
     *
     * @return This component builder.
     */
    public final SELF closeOnClick() {
        closeOnClick = !closeOnClick;
        return (SELF) this;
    }

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    public boolean isContainedWithin(int position) {
        return this.position == position;
    }

    /**
     * Called when the component is updated.
     *
     * @param updateHandler The update handler.
     * @return This component builder.
     */
    public final SELF onUpdate(@Nullable Consumer<? super IFComponentUpdateContext> updateHandler) {
        this.updateHandler = updateHandler;
        return (SELF) this;
    }

    /**
     * Called when a player clicks on the component.
     * <p>
     * This handler works on any container that the actor has access to and only works if the
     * interaction has not been cancelled.
     *
     * @param clickHandler The click handler.
     * @return This component builder.
     */
    public final SELF onClick(@Nullable Runnable clickHandler) {
        this.clickHandler = clickHandler == null ? null : $ -> clickHandler.run();
        return (SELF) this;
    }

    /**
     * Only shows the component if a given condition is satisfied.
     *
     * @param displayCondition Component display condition.
     * @return This component builder.
     * @see #hideIf(Predicate)
     */
    public final SELF displayIf(Predicate<CONTEXT> displayCondition) {
        super.displayCondition = (Predicate<? extends IFContext>) displayCondition;
        return (SELF) this;
    }

    /**
     * Only shows the component if a given condition is satisfied.
     *
     * @param displayCondition Component display condition.
     * @return This component builder.
     * @see #hideIf(BooleanSupplier)
     */
    public final SELF displayIf(BooleanSupplier displayCondition) {
        displayIf(displayCondition == null ? null : $ -> displayCondition.getAsBoolean());
        return (SELF) this;
    }

    /**
     * Hides the component if a given condition is satisfied.
     *
     * @param condition Condition to hide the component.
     * @return This component builder.
     * @see #displayIf(Predicate)
     */
    public final SELF hideIf(Predicate<CONTEXT> condition) {
        return displayIf(condition == null ? null : arg -> !condition.test(arg));
    }

    /**
     * Hides the component if a given condition is satisfied.
     *
     * @param condition Condition to hide the component.
     * @return This component builder.
     * @see #displayIf(BooleanSupplier)
     */
    public final SELF hideIf(BooleanSupplier condition) {
        return displayIf(condition == null ? null : () -> !condition.getAsBoolean());
    }
    // endregion

    @Override
    public void prepareComponent(VirtualView root, AbstractComponent abstractComponent) {
        super.prepareComponent(root, abstractComponent);

        final PlatformComponent<?, ?> component = (PlatformComponent<?, ?>) abstractComponent;
        component.setRenderHandler(renderHandler);
        component.setUpdateHandler(updateHandler);
        component.setClickHandler(clickHandler);
        component.setCancelOnClick(cancelOnClick);
        component.setUpdateOnClick(updateOnClick);
        component.setCloseOnClick(closeOnClick);

        final int position;
        if (row > 0 && column > 0) {
            final ViewContainer container = ViewContainer.from(root);
            position = SlotConverter.convertSlot(row, column, container.getRowsCount(), container.getColumnsCount());
        } else position = this.position;

        component.setPosition(position);
    }
}
