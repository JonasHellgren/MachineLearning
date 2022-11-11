package black_jack.models_memory;

import black_jack.enums.CardAction;
import black_jack.models_cards.StateObservedActionObserved;
import black_jack.models_cards.StateObservedInterface;
import black_jack.models_cards.StateObservedObserved;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class StateActionValueMemory implements MemoryInterface<StateObservedActionObserved> {

    public static final double DEFAULT_VALUE = -0d;

    Map<Integer, Double> stateActionValueMap;
    Set<StateObservedActionObserved> visitedStates;

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
    public double read(StateObservedActionObserved state) {
        return stateActionValueMap.getOrDefault(state.hashCode(), DEFAULT_VALUE);
    }

    @Override
    public double readBestValue(StateObservedObserved state) {
        double valueHit=stateActionValueMap.getOrDefault(
                StateObservedActionObserved.newFromStateAndAction(state, CardAction.hit).hashCode()
                ,DEFAULT_VALUE);
        double valueStick=stateActionValueMap.getOrDefault(
                StateObservedActionObserved.newFromStateAndAction(state, CardAction.stick).hashCode()
                ,DEFAULT_VALUE);

        return Math.max(valueHit,valueStick);
    }

    @Override
    public void write(StateObservedActionObserved state, double value) {
        stateActionValueMap.put(state.hashCode(),value);
        visitedStates.add(state);
    }

    @Override
    public double average() {
        throw  new RuntimeException("Non defined method");
        //return 0;
    }



    @Override
    public Set<Double> valuesOf(Predicate<StateObservedObserved> p) {
        Set<StateObservedObserved> stateSet= StateObservedInterface.allStates();
        Set<StateObservedObserved> set=stateSet.stream().filter(p).collect(Collectors.toSet());
        Set<Double> setDouble=new HashSet<>();
        set.forEach(s -> setDouble.add(readBestValue(s)));
        return setDouble;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (StateObservedActionObserved s : visitedStates) {
            sb.append(s.toString()).append(", value = ").
                    append(stateActionValueMap.getOrDefault(s.hashCode(), DEFAULT_VALUE)).
                    append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

}
