package me.devnatan.inventoryframework.state.timer;

public interface Timer {

	long initialInterval();

	long currentInterval();

	boolean isPaused();

	boolean pause();
}