package me.devnatan.inventoryframework.state;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MutableValue implements InternalStateValue {

    private Object currValue;

    @Override
    public Object get() {
        return currValue;
    }

    @Override
    public void set(Object value) {
        this.currValue = value;
    }
}
