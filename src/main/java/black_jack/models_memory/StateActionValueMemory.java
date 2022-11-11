package black_jack.models_memory;

import black_jack.models_cards.StateActionObserved;
import black_jack.models_cards.StateObserved;

import java.util.Map;
import java.util.Set;

public class StateActionValueMemory implements MemoryInterface {

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
    public double read(StateObserved state) {
        throw new RuntimeException("Non defined method");
        //return 0;
    }

    @Override
    public void write(StateObserved state, double Value) {
        throw new RuntimeException("Non defined method");
    }

    public double readStateAction(StateActionObserved state) {
        return stateActionValueMap.getOrDefault(state.hashCode(), DEFAULT_VALUE);
    }

    public void writeStateAction(StateActionObserved state, double value) {
        stateActionValueMap.put(state.hashCode(),value);
        visitedStates.add(state);
    }



    @Override
    public double average() {
        return 0;
    }
}
