package black_jack.models;

import lombok.ToString;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ValueMemory {

    public static final double DEFAULT_VALUE = 0d;
    public static final double ALPHA_DEFAULT = 0.1;

    double alpha = ALPHA_DEFAULT;
    Map<Integer, Double> stateValueMap;
    Set<StateObserved> visitedStates;

    public ValueMemory() {
        stateValueMap = new HashMap<>();
        visitedStates = new HashSet<>();
    }

    public ValueMemory(double alpha) {
        this();
        this.alpha = alpha;
    }

    public void clear() {
        stateValueMap.clear();
        visitedStates.clear();
    }

    public int nofItems() {
        return stateValueMap.size();
    }

    public void updateMemory(ReturnsForEpisode returns) {
        for (ReturnItem ri : returns.getReturns()) {
            updateMemory(ri);
        }
    }

    public void updateMemory(ReturnItem ri) {
        double oldValue = getValue(ri.state);
        double newValue = oldValue + alpha * (ri.returnValue - oldValue);
        stateValueMap.put(ri.state.hashCode(), newValue);
        visitedStates.add(ri.state);
    }

    public double getValue(StateObserved state) {
        return stateValueMap.getOrDefault(state.hashCode(), DEFAULT_VALUE);
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
