package dynamic_programming.domain;

import lombok.extern.java.Log;
import org.bytedeco.opencv.presets.opencv_core;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Memory for value function, looked up by state
 */

@Log
public class ValueMemory {

    public static ValueMemory newEmpty() {
        return new ValueMemory();
    }

    Map<State, Double> values;

    public ValueMemory() {
        this.values = new HashMap<>();
    }

    public Map<State, Double> getValues() {
        return values;
    }

    public int size() {
        return values.size();
    }

    public boolean isEmpty() {
        return size()==0;
    }

    public void addValue(State state, double value) {
        if (values.containsKey(state)) {
            log.warning("Memory already includes state, state =" + state);
        }

        values.put(state, value);
    }

    public Optional<Double> getValue(State state) {
        return Optional.ofNullable(values.get(state));  //Optional.empty() if state not present
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        for (State state : values.keySet()) {
            sb.append(state).append(":").append(values.get(state)).append(System.lineSeparator());
        }
        return sb.toString();
    }


}
