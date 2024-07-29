package policy_gradient_problems.domain.value_classes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used by training tracker
 */

public record MeasuresAllStatesAtEpisode(
        Map<Integer, List<Double>> stateMeasuresMap  //stateNew, measures
) {

    public MeasuresAllStatesAtEpisode(Integer state, List<Double> measures) {
        this(new HashMap<>());
        stateMeasuresMap.put(state,measures);
    }

    public void addItem(int state, List<Double> actionProbabilities) {
        stateMeasuresMap.put(state,actionProbabilities);
    }

    public String toString() {
        return  stateMeasuresMap.entrySet().toString();
    }

}
