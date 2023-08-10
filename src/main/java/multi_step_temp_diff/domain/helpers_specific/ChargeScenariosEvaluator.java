package multi_step_temp_diff.domain.helpers_specific;

import lombok.Builder;
import lombok.NonNull;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.helpers_common.AgentEvaluatorResults;
import multi_step_temp_diff.domain.helpers_common.InitStateVariantsEvaluator;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
public class ChargeScenariosEvaluator {

    @NonNull List<Pair<StateInterface<ChargeVariables>, Integer>> scenarios;   //initState, simStepsMax;
    @NonNull EnvironmentInterface<ChargeVariables> environment;
    @NonNull AgentNeuralInterface<ChargeVariables> agent;

    Map<Pair<StateInterface<ChargeVariables>, Integer>,AgentEvaluatorResults> scenarioResultMap;



    public ChargeScenariosEvaluator(@NonNull List<Pair<StateInterface<ChargeVariables>, Integer>> scenarios,
                                    @NonNull EnvironmentInterface<ChargeVariables> environment,
                                    @NonNull AgentNeuralInterface<ChargeVariables> agent) {
        this.scenarios = scenarios;
        this.environment = environment;
        this.agent = agent;

        scenarioResultMap=new HashMap<>();
    }

    public Map<Pair<StateInterface<ChargeVariables>, Integer>, AgentEvaluatorResults> getScenarioResultMap() {
        return scenarioResultMap;
    }

    public List<Double> evaluate() {

        InitStateVariantsEvaluator<ChargeVariables> evaluator= InitStateVariantsEvaluator.<ChargeVariables>builder()
                .environment(environment)
                .agent(agent)
                .build();

        scenarioResultMap.clear();
        for (Pair<StateInterface<ChargeVariables>, Integer> scenario : scenarios) {
            scenarioResultMap.put(scenario,evaluator.evaluate(scenario));
        }
        return scenarioResultMap.values().stream().map(r -> r.sumRewards()).toList();

    }

}
