package black_jack.models_memory;

import black_jack.enums.CardAction;
import black_jack.models_cards.StateActionObserved;
import black_jack.models_cards.StateObserved;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@Getter
public class StateActionValueMemory implements MemoryInterface<StateActionObserved> {

    public static final double DEFAULT_VALUE = 0d;

    Map<Integer, Double> stateActionValueMap;
    Set<StateActionObserved> visitedStates;

    public StateActionValueMemory() {
        this.stateActionValueMap = new HashMap<>();
        this.visitedStates = new HashSet<>();
    }

    @Override
    public void clear() {
        stateActionValueMap.clear();
        visitedStates.clear();
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
    public double readBestValue(StateObserved state) {
        double valueHit=stateActionValueMap.getOrDefault(
                StateActionObserved.newFromStateAndAction(state, CardAction.hit).hashCode()
                ,DEFAULT_VALUE);
        double valueStick=stateActionValueMap.getOrDefault(
                StateActionObserved.newFromStateAndAction(state, CardAction.stick).hashCode()
                ,DEFAULT_VALUE);

        return Math.max(valueHit,valueStick);
    }

    @Override
    public void write(StateActionObserved state, double value) {
        stateActionValueMap.put(state.hashCode(),value);
        visitedStates.add(state);
    }

    @Override
    public double average() {
        throw  new RuntimeException("Non defined method");
        //return 0;
    }

    @Override
    public Set<Double> valuesOf(Predicate<StateObserved> p) {
       throw new RuntimeException("Not implemented");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (StateActionObserved s : visitedStates) {
            sb.append(s.toString()).append(", value = ").
                    append(stateActionValueMap.getOrDefault(s.hashCode(), DEFAULT_VALUE)).
                    append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

}
