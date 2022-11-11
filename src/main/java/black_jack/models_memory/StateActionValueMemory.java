package black_jack.models_memory;

import black_jack.models_cards.StateActionObserved;
import black_jack.models_cards.StateObserved;

import java.util.Map;
import java.util.Set;

public class StateActionValueMemory implements MemoryInterface<StateActionObserved> {

    public static final double DEFAULT_VALUE = 0d;

    Map<Integer, Double> stateActionValueMap;
    Set<StateActionObserved> visitedStates;

    @Override
    public void clear() {

    }

    @Override
    public int nofItems() {
        return stateActionValueMap.size();
    }

    @Override
    public double read(StateActionObserved state) {
        return stateActionValueMap.getOrDefault(state.hashCode(), DEFAULT_VALUE);
    }

    @Override
    public void write(StateActionObserved state, double value) {
        stateActionValueMap.put(state.hashCode(),value);
        visitedStates.add(state);

    }

    @Override
    public double average() {
        return 0;
    }
}
