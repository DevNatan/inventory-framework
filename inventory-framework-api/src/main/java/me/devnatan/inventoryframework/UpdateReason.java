package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.state.State;

public interface UpdateReason {

    class UpdateOnClick implements UpdateReason {}

    class StateWatch implements UpdateReason {

        private final State<?> state;

        public StateWatch(State<?> state) {
            this.state = state;
        }

        public State<?> getState() {
            return state;
        }
    }
}
