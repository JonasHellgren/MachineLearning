package multi_step_temp_diff.domain.helpers_specific;

import common.other.Conditionals;
import common.list_arrays.ListUtils;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_abstract.AgentInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.helpers_common.AgentEvaluatorResults;
import multi_step_temp_diff.domain.helpers_common.InitStateVariantsEvaluator;
import multi_step_temp_diff.domain.helpers_common.Scenario;

import java.util.*;
import java.util.function.Predicate;

import static multi_step_temp_diff.domain.helpers_specific.ChargeScenariosFactory.*;
import static multi_step_temp_diff.domain.helpers_specific.ChargeScenariosFactory.BatPos0_At1_BothHighSoC_1000steps;

@Builder
@Log
public class ChargeScenariosEvaluator {

    @NonNull List<Scenario<ChargeVariables>> scenarios;   //initState, simStepsMax;
    @NonNull EnvironmentInterface<ChargeVariables> environment;
    @NonNull AgentInterface<ChargeVariables> agent;

    static Map<Scenario<ChargeVariables>, AgentEvaluatorResults> scenarioResultMap;
    @Builder.Default
    List<Double> sumRewardList=new ArrayList<>();

    public static ChargeScenariosEvaluator newAllScenarios(@NonNull EnvironmentInterface<ChargeVariables> environment,
                                                           @NonNull AgentInterface<ChargeVariables> agent) {
        return ChargeScenariosEvaluator.builder()
                .environment(environment).agent(agent)
                .scenarios(List.of(
                        BatPos0_At1_BothHighSoC,
                        BatPos0_AtPosSplitCriticalSoCA,
                        BatPos0_At20_BothMaxSoC,
                        BatPosSplit_AatPos40_BothModerateSoC,
                        B3BehindModerateSoC_AatSplitModerateSoC,
                        B1BehindCriticalSoC_AatSplitModerateSoC,
                        B3BehindCriticalSoC_AatSplitModerateSoC,
                        BatPos0_At1_BothHighSoC_1000steps
                ))
                .build();
    }

    public static ChargeScenariosEvaluator newSingleScenario(
            @NonNull Scenario<ChargeVariables> scenario,
            @NonNull EnvironmentInterface<ChargeVariables> environment,
            @NonNull AgentInterface<ChargeVariables> agent) {
        return ChargeScenariosEvaluator.builder()
                .environment(environment).agent(agent)
                .scenarios(List.of(scenario))
                .build();
    }


    public Map<Scenario<ChargeVariables>, AgentEvaluatorResults> getScenarioResultMap() {
        if (isResultMapNull.test(scenarioResultMap)) {
            return new HashMap<>();
        }
        return scenarioResultMap;
    }

    static Predicate<Map<Scenario<ChargeVariables>, AgentEvaluatorResults>> isResultMapNull = (rm) ->
            rm == null;

    public List<Double> evaluate() {
        scenarioResultMap = new HashMap<>();
        InitStateVariantsEvaluator<ChargeVariables> evaluator = InitStateVariantsEvaluator.<ChargeVariables>builder()
                .environment(environment)
                .agent(agent)
                .build();

        for (Scenario<ChargeVariables> scenario : scenarios) {
            scenarioResultMap.put(scenario, evaluator.evaluate(scenario));
        }

        sumRewardList=scenarioResultMap.values().stream().map(r -> r.sumRewards()).toList();
        return sumRewardList;
    }

    public double getSumOfRewardAllScenarios() {
        Conditionals.executeIfTrue(sumRewardList==null || sumRewardList.isEmpty(), () ->
                log.warning("No evaluation result available, run evaluate first"));
        return  ListUtils.sumDoubleList(sumRewardList);
    }


    @Override
    public String toString() {
        if (isResultMapNull.test(scenarioResultMap)) {
            return "No results available";
        }
        List<String> stringList = scenarioResultMap.keySet().stream()
                .map(s -> "name = " + s.name() + ", sumRewardsNSteps = " + scenarioResultMap.get(s).sumRewards())
                .toList();

        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        for (String txt : stringList) {
            sb.append(txt);
            sb.append(System.lineSeparator());
        }

        sb.append("sum of reward-sums all scenarios= ").append(getSumOfRewardAllScenarios());
        return sb.toString();

    }

}
