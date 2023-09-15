package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.state.BaseMutableState;
import me.devnatan.inventoryframework.state.StateValueFactory;

final class AnvilInputState extends BaseMutableState<String> {

    AnvilInputState(long id, StateValueFactory valueFactory) {
        super(id, valueFactory);
    }
}
