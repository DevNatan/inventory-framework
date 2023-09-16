package me.devnatan.inventoryframework;

public class AnvilInputConfig {

    boolean neverClose;

    AnvilInputConfig(boolean neverClose) {
        this.neverClose = neverClose;
    }

    public AnvilInputConfig neverClose() {
        this.neverClose = !this.neverClose;
        return this;
    }
}
