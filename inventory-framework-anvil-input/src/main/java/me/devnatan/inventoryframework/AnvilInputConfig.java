package me.devnatan.inventoryframework;

import java.util.function.UnaryOperator;

public class AnvilInputConfig {

    String initialInput = "";
    boolean closeOnSelect;
    UnaryOperator<String> inputChangeHandler;

    AnvilInputConfig() {}

    public AnvilInputConfig initialInput(String initialInput) {
        this.initialInput = initialInput;
        return this;
    }

    public AnvilInputConfig closeOnSelect() {
        this.closeOnSelect = !this.closeOnSelect;
        return this;
    }

    public AnvilInputConfig onInputChange(UnaryOperator<String> inputChangeHandler) {
        this.inputChangeHandler = inputChangeHandler;
        return this;
    }
}
