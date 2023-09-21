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
    public void apply(@NotNull ViewConfigBuilder config, @NotNull IFContext context) {}

    /**
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    public static AnvilInput createAnvilInput() {
        return createAnvilInput("");
    }

    /**
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    public static AnvilInput createAnvilInput(@NotNull String initialInput) {
        return createAnvilInput(initialInput, UnaryOperator.identity());
    }

    /**
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    public static AnvilInput createAnvilInput(@NotNull UnaryOperator<String> onInputChange) {
        return createAnvilInput("", onInputChange);
    }

    /**
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    public static AnvilInput createAnvilInput(
            @NotNull String initialInput, @NotNull UnaryOperator<String> onInputChange) {
        final long id = State.next();
        final StateValueFactory factory = (host, state) -> new AnvilInputStateValue(state, initialInput, onInputChange);

        return new AnvilInput(id, factory);
    }
}
