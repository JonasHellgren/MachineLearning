package multi_step_temp_diff.domain.helpers_common;

import lombok.Builder;
import lombok.NonNull;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import java.util.ArrayList;
import java.util.List;

@Builder
public class InitStateVariantsEvaluator<S> {
    @Builder.Default
    List<Scenario<S>> scenarios=new ArrayList<>();   //initState, simStepsMax;
    @NonNull EnvironmentInterface<S> environment;
    @NonNull AgentNeuralInterface<S> agent;

    public List<AgentEvaluatorResults> evaluate() {
        List<AgentEvaluatorResults> results = new ArrayList<>();
        for (Scenario<S> scenario : scenarios) {
            AgentEvaluatorResults result = evaluate(scenario);
            results.add(result);
        }
        return results;
    }

    public AgentEvaluatorResults evaluate(Scenario<S> scenario) {
        AgentEvaluator<S> evaluator = AgentEvaluator.<S>builder()
                .environment(environment).agent(agent).simStepsMax(scenario.simStepsMax())
                .build();
        return evaluator.simulate(scenario.state());
    }


}
