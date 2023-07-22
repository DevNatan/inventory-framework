package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;

/**
 * Builder base for any {@link Component} implementation.
 *
 * @param <S> The reference of the component builder itself used as return type for method chaining.
 */
public interface ComponentBuilder<S extends ComponentBuilder<S>> {

    /**
     * Defines the reference key for this component.
     * <p>
     * Reference keys can be used to get an instance of a component that you can later reference
     * this component in an unknown handler in your code and update this component manually, for
     * example, if necessary.
     * <pre>{@code
     * IFSlotContext context = context.ref("my-component");
     * }</pre>
     *
     * @param key The component reference key.
     * @return This component builder.
     */
    S referencedBy(@NotNull String key);

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
     */
    S watch(State<?>... states);

    /**
     * Returns a copy of this component builder.
     *
     * @return A copy of this component builder.
     */
    S copy();
}
