package dynamic_programming.domain;

import lombok.extern.java.Log;

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

    Map<State,Double> values;

    public ValueMemory() {
        this.values = new HashMap<>();
    }

    public int size() {
        return values.size();
    }

    public void addValue(State state, double value) {
        if (values.containsKey(state)) {
            log.warning("Memory already includes state, state ="+state);
        }

        values.put(state,value);
    }

    public Optional<Double> getValue(State state) {
        return  Optional.ofNullable(values.get(state));  //Optional.empty() if state not present

    }


}
