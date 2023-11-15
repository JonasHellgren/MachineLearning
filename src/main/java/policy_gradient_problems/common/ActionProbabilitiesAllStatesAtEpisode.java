package policy_gradient_problems.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ActionProbabilitiesAllStatesAtEpisode(
        Map<Integer, List<Double>> stateProbabilitiesMap  //state, actionProbabilities
) {

    public ActionProbabilitiesAllStatesAtEpisode(Integer state, List<Double> actionProbabilities) {
        this(new HashMap<>());
        stateProbabilitiesMap.put(state,actionProbabilities);
    }

    public void addItem(int state, List<Double> actionProbabilities) {
        stateProbabilitiesMap.put(state,actionProbabilities);
    }

    public String toString() {
        return  stateProbabilitiesMap.entrySet().toString();
    }

}
