package me.devnatan.inventoryframework;

public class AnvilInputConfig {

    String initialInput = "";
    boolean closeOnSelect;

    AnvilInputConfig() {}

    public AnvilInputConfig initialInput(String initialInput) {
        this.initialInput = initialInput;
        return this;
    }

    public AnvilInputConfig closeOnSelect() {
        this.closeOnSelect = !this.closeOnSelect;
        return this;
    }
}
