package me.devnatan.inventoryframework.state.timer;

public interface Timer {

	void loop();

	long initialInterval();

	long currentInterval();

	void changeInterval(long interval);

	boolean isPaused();

	boolean pause();
}