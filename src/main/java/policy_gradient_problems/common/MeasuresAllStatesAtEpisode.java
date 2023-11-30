package policy_gradient_problems.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record MeasuresAllStatesAtEpisode(
        Map<Integer, List<Double>> stateMeasuresMap  //state, measures
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
