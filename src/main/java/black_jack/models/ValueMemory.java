package black_jack.models;

import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ValueMemory implements MemoryInterface {

    public static final double DEFAULT_VALUE = 0d;

    Map<Integer, Double> stateValueMap;
    Set<StateObserved> visitedStates;

    public ValueMemory() {
        stateValueMap = new HashMap<>();
        visitedStates = new HashSet<>();
    }

    public void clear() {
        stateValueMap.clear();
        visitedStates.clear();
    }

    public int nofItems() {
        return stateValueMap.size();
    }

    public double read(StateObserved state) {
        return stateValueMap.getOrDefault(state.hashCode(), DEFAULT_VALUE);
    }

    public void write(StateObserved state, double value) {
        stateValueMap.put(state.hashCode(),value);
        visitedStates.add(state);
    }

    public double average() {
        double sum = stateValueMap.values().stream().mapToDouble(d -> d).sum();
        int nofItems=stateValueMap.size();
        return (nofItems==0)
                ? 0
                :sum/(double)nofItems;
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
