package me.devnatan.inventoryframework.state.timer;

public interface Timer {

	long initialInterval();

	long currentInterval();

	void changeInterval(long interval);

	boolean isPaused();

	boolean pause();
}