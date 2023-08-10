package multi_step_temp_diff.domain.helpers_common;

import lombok.Builder;
import lombok.NonNull;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@Builder
public class InitStateVariantsEvaluator<S> {
    @Builder.Default
    List<Pair<StateInterface<S>, Integer>> scenarios=new ArrayList<>();   //initState, simStepsMax;
    @NonNull EnvironmentInterface<S> environment;
    @NonNull AgentNeuralInterface<S> agent;

    public List<AgentEvaluatorResults> evaluate() {
        List<AgentEvaluatorResults> results = new ArrayList<>();
        for (Pair<StateInterface<S>, Integer> scenario : scenarios) {
            AgentEvaluatorResults result = evaluate(scenario);
            results.add(result);
        }
        return results;
    }

    public AgentEvaluatorResults evaluate(Pair<StateInterface<S>, Integer> scenario) {
        StateInterface<S> initState = scenario.getLeft();
        int simStepsMax = scenario.getRight();
        AgentEvaluator<S> evaluator = AgentEvaluator.<S>builder()
                .environment(environment).agent(agent).simStepsMax(simStepsMax)
                .build();
        return evaluator.simulate(initState);
    }


}
