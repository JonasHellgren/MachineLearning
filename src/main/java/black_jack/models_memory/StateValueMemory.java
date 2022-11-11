package black_jack.models_memory;

import black_jack.models_cards.StateInterface;
import black_jack.models_cards.StateObserved;
import freemarker.ext.jsp.FreemarkerTag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StateValueMemory implements MemoryInterface<StateObserved> {

    public static final double DEFAULT_VALUE = 0d;

    Map<Integer, Double> stateValueMap;
    Set<StateObserved> visitedStates;

    public StateValueMemory() {
        stateValueMap = new HashMap<>();
        visitedStates = new HashSet<>();
    }

    @Override
    public void clear() {
        stateValueMap.clear();
        visitedStates.clear();
    }

    @Override
    public int nofItems() {
        return stateValueMap.size();
    }

    @Override
    public double read(StateObserved state) {
        return stateValueMap.getOrDefault(state.hashCode(), DEFAULT_VALUE);
    }

    @Override
    public double readBestValue(StateObserved state) {
        return read(state);  //trivial if no action/one value
    }

    @Override
    public void write(StateObserved state, double value) {
        stateValueMap.put(state.hashCode(),value);
        visitedStates.add(state);
    }

    @Override
    public double average() {
        double sum = stateValueMap.values().stream()
                .mapToDouble(d -> d).sum();
        int nofItems=stateValueMap.size();
        return (nofItems==0)
                ? 0
                :sum/(double)nofItems;
    }

    @Override
    public Set<Double> valuesOf(Predicate<StateObserved> p) {
        Set<StateObserved> stateSet= StateInterface.allStates();
        Set<StateObserved> set=stateSet.stream().filter(p).collect(Collectors.toSet());
        Set<Double> setDouble=new HashSet<>();
        set.forEach(s -> setDouble.add(stateValueMap.get(s.hashCode())));
        return setDouble;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (StateObserved s : visitedStates) {
            sb.append(s.toString()).append(", value = ").
                    append(stateValueMap.getOrDefault(s.hashCode(), DEFAULT_VALUE)).
                    append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }


}
