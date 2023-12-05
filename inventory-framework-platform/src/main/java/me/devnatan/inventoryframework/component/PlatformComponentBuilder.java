package me.devnatan.inventoryframework.component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;

import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public abstract class PlatformComponentBuilder<SELF extends PlatformComponentBuilder<SELF, CONTEXT>, CONTEXT extends IFContext>
	extends AbstractComponentBuilder {

	private Consumer<? super IFComponentRenderContext> renderHandler;
	private Consumer<? super IFComponentUpdateContext> updateHandler;

    protected PlatformComponentBuilder() {}

	// region Protected ComponentBuilder API
	protected final Consumer<? super IFComponentRenderContext> getRenderHandler() {
		return renderHandler;
	}

	protected final Consumer<? super IFComponentUpdateContext> getUpdateHandler() {
		return updateHandler;
	}
	// endregion

	/**
	 * Called when the item is rendered.
	 * <p>
	 * This handler is called every time the item or the view that owns it is updated.
	 *
	 * @param renderHandler The render handler.
	 * @return This item builder.
	 */
	public final SELF onRender(@Nullable Consumer<? super IFComponentRenderContext> renderHandler) {
		this.renderHandler = renderHandler;
		return (SELF) this;
	}

	/**
	 * Called when the item is updated.
	 *
	 * @param updateHandler The update handler.
	 * @return This item builder.
	 */
	public final SELF onUpdate(@Nullable Consumer<? super IFComponentUpdateContext> updateHandler) {
		this.updateHandler = updateHandler;
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
		setDisplayCondition(displayCondition);
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
		setDisplayCondition(displayCondition == null ? null : $ -> displayCondition.getAsBoolean());
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

	/**
	 * Assigns {@link Ref a reference} to this component.
	 *
	 * @param reference Component reference key.
	 * @return This component builder.
	 * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/refs-api">Refs API on Wiki</a>
	 */
	public final SELF referencedBy(@NotNull Ref<Component> reference) {
		super.setReference(reference);
		return (SELF) this;
	}

	/**
	 * Adds a new user-defined property to this component.
	 * <p>
	 * User-defined properties can be used to persist data that can be retrieved later even after
	 * several actions applied to that component.
	 * <p>
	 * An example of user-defined data persistence is for post-moving identification of a component
	 * inside the container, you can define a data in this item and as soon as the actor moves it
	 * the data will remain there, and you can use it any way you want.
	 *
	 * @param key   The property key.
	 * @param value The property value.
	 * @return This component builder.
	 */
	public final SELF withData(@NotNull String key, Object value) {
		if (getData() == null) setData(new HashMap<>());
		getData().put(key, value);
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
		setCancelOnClick(!isCancelOnClick());
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
		setCloseOnClick(!isCloseOnClick());
		return (SELF) this;
	}

	/**
	 * Watches one or more states.
	 * <p>
	 * When any of the states provided as parameters are modified, the item generated from this
	 * builder will be updated.
	 *
	 * @param states The state to watch.
	 * @return This component builder.
	 * @deprecated Use {@link #updateOnStateChange(State)} instead.
	 */
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "3.2.0")
	public final SELF watch(State<?>... states) {
		if (getWatchingStates() == null) setWatchingStates(new LinkedHashSet<>());
		getWatchingStates().addAll(Arrays.asList(states));
		return (SELF) this;
	}

	/**
	 * Listens for value updates in the specified state.
	 * <p>
	 * Everytime the value of the given state updates, this component will be updated as well.
	 *
	 * @param state The state to listen changes to.
	 * @return This component builder.
	 */
	public final SELF updateOnStateChange(@NotNull State<?> state) {
		if (getWatchingStates() == null) setWatchingStates(new LinkedHashSet<>());
		getWatchingStates().add(state);
		return (SELF) this;
	}

	/**
	 * Listens for value updates in any of the specified states.
	 * <p>
	 * Everytime the value ANY of the given state updates, this component will be updated as well.
	 *
	 * @param state The state to listen changes to.
	 * @param states Other states to listen changes to.
	 * @return This component builder.
	 */
	public final SELF updateOnStateChange(@NotNull State<?> state, State<?>... states) {
		if (getWatchingStates() == null) setWatchingStates(new LinkedHashSet<>());
		getWatchingStates().add(state);
		getWatchingStates().addAll(Arrays.asList(states));
		return (SELF) this;
	}

	/**
	 * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided.</i></b>
	 */
	@ApiStatus.Internal
	public final SELF withExternallyManaged(boolean isExternallyManaged) {
		setManagedExternally(isExternallyManaged);
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
		setUpdateOnClick(!isUpdateOnClick());
		return (SELF) this;
	}

	/**
	 * Sets the key of the component.
	 *
	 * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
	 * such API may be changed or may be removed completely in any further release. </i></b>
	 *
	 * @return This component builder.
	 */
	@ApiStatus.Experimental
	public final SELF key(String key) {
		setKey(key);
		return (SELF) this;
	}
}
