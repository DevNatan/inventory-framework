package me.devnatan.inventoryframework.state;

public interface TimerState extends State<TimerState.Timer> {

    interface Timer {

        long initialInterval();

        long currentInterval();

        boolean isPaused();

        boolean togglePause();
    }
}
