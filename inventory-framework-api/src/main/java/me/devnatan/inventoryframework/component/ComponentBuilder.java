package me.devnatan.inventoryframework.component;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Builder base for any {@link Component} implementation.
 *
 * @param <S> The reference of the component builder itself used as return type for method chaining.
 */
public interface ComponentBuilder<S extends ComponentBuilder<S, C>, C extends IFContext> {

    /**
     * Assigns {@link Ref a reference} to this component.
     *
     * @param reference Component reference key.
     * @return This component builder.
     * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/refs-api">Refs API on Wiki</a>
     */
    S referencedBy(@NotNull Ref<Component> reference);

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
    S withData(@NotNull String key, Object value);

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
    S cancelOnClick();

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
    S closeOnClick();

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
    S watch(State<?>... states);

    /**
     * Listens for value updates in the specified state.
     * <p>
     * Everytime the value of the given state updates, this component will be updated as well.
     *
     * @param state The state to listen changes to.
     * @return This component builder.
     */
    S updateOnStateChange(@NotNull State<?> state);

    /**
     * Listens for value updates in any of the specified states.
     * <p>
     * Everytime the value ANY of the given state updates, this component will be updated as well.
     *
     * @param states The state to listen changes to.
     * @return This component builder.
     */
    S updateOnStateChange(State<?>... states);

    /**
     * Returns a copy of this component builder.
     *
     * @return A copy of this component builder.
     */
    S copy();

    /**
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     */
    @ApiStatus.Internal
    S withExternallyManaged(boolean isExternallyManaged);

    /**
     * Updates the current context when a player clicks on this component.
     *
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @return This component builder.
     */
    @ApiStatus.Experimental
    S updateOnClick();

    /**
     * Only shows the component if a given condition is satisfied.
     *
     * @param displayCondition Component display condition.
     * @return This component builder.
     * @see #hideIf(BooleanSupplier)
     */
    S displayIf(BooleanSupplier displayCondition);

    /**
     * Only shows the component if a given condition is satisfied.
     *
     * @param displayCondition Component display condition.
     * @return This component builder.
     * @see #hideIf(Predicate)
     */
    S displayIf(Predicate<C> displayCondition);

    /**
     * Hides the component if a given condition is satisfied.
     *
     * @param condition Condition to hide the component.
     * @return This component builder.
     * @see #displayIf(BooleanSupplier)
     */
    S hideIf(BooleanSupplier condition);

    /**
     * Hides the component if a given condition is satisfied.
     *
     * @param condition Condition to hide the component.
     * @return This component builder.
     * @see #displayIf(Predicate)
     */
    S hideIf(Predicate<C> condition);
}
