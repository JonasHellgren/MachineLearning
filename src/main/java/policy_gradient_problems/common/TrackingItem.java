package policy_gradient_problems.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record TrackingItem(
        Map<Integer, List<Double>> stateProbabilitiesMap
) {

    public static TrackingItem newEmpty() {
        return new TrackingItem(new HashMap<>());
    }

    public void addItem(int state, List<Double> actionProbabilities) {
        stateProbabilitiesMap.put(state,actionProbabilities);
    }

    public String toString() {
        return  stateProbabilitiesMap.entrySet().toString();
    }

}
