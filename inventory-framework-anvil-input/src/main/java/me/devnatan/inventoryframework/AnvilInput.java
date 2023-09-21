package me.devnatan.inventoryframework;

import java.util.function.UnaryOperator;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.state.BaseMutableState;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateValueFactory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public final class AnvilInput extends BaseMutableState<String> implements ViewConfig.Modifier {

    AnvilInput(long id, StateValueFactory valueFactory) {
        super(id, valueFactory);
    }

    @Override
    public void apply(@NotNull ViewConfigBuilder config, @NotNull IFContext context) {
        // do nothing - config modifier is only used for reference here
    }

    /**
     * Returns the default configuration of the anvil input feature.
     *
     * @return Default configuration of the anvil input feature.
     */
    public static AnvilInputConfig defaultConfig() {
        return AnvilInputFeature.DEFAULT_CONFIG;
    }

    /**
     * Creates a new AnvilInput instance.
     * <p>
     * <b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/anvil-input">Anvil Input on Wiki</a>
     */
    @ApiStatus.Experimental
    public static AnvilInput createAnvilInput() {
        return createAnvilInput("");
    }

    /**
     * Creates a new AnvilInput instance with an initial input.
     * <p>
     * <b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     * @param initialInput Initial text input value.
     * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/anvil-input">Anvil Input on Wiki</a>
     */
    @ApiStatus.Experimental
    public static AnvilInput createAnvilInput(@NotNull String initialInput) {
        return createAnvilInput(initialInput, UnaryOperator.identity());
    }

    /**
     * Creates a new AnvilInput instance with an input change handler.
     * <p>
     * <code>onInputChange</code> parameter can be used to transform the input provided by the player.
     * Note that it's not called immediately, only when view is closed or the players interacts with
     * the item placed at container's {@link ViewType#getResultSlots() first result slot}.
     * <p>
     * <b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @param onInputChange Input change handler, current input will be set to the result of it.
     * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/anvil-input">Anvil Input on Wiki</a>
     */
    @ApiStatus.Experimental
    public static AnvilInput createAnvilInput(@NotNull UnaryOperator<String> onInputChange) {
        return createAnvilInput("", onInputChange);
    }

    /**
     * Creates a new AnvilInput instance with an initial input and an input change handler.
     * <p>
     * <code>onInputChange</code> parameter can be used to transform the input provided by the player.
     * Note that it's not called immediately, only when view is closed or the players interacts with
     * the item placed at container's {@link ViewType#getResultSlots() first result slot}.
     * <p>
     * <b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @param initialInput Initial text input value.
     * @param onInputChange Input change handler, current input will be set to the result of it.
     * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/anvil-input">Anvil Input on Wiki</a>
     */
    @ApiStatus.Experimental
    public static AnvilInput createAnvilInput(
            @NotNull String initialInput, @NotNull UnaryOperator<String> onInputChange) {
        return createAnvilInput(defaultConfig().initialInput(initialInput).onInputChange(onInputChange));
    }

    /**
     * Creates a new AnvilInput instance.
     * <p>
     * <b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @param config Anvil input feature configuration.
     * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/anvil-input">Anvil Input on Wiki</a>
     */
    @ApiStatus.Experimental
    public static AnvilInput createAnvilInput(@NotNull AnvilInputConfig config) {
        final long id = State.next();
        final StateValueFactory factory = (host, state) -> new AnvilInputStateValue(state, config);

        return new AnvilInput(id, factory);
    }
}
