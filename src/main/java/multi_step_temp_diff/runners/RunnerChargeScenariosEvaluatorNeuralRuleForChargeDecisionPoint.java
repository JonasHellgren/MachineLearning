package multi_step_temp_diff.runners;

import common.Counter;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_abstract.AgentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.agents.charge.AgentChargeGreedyRuleForChargeDecisionPoint;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.helpers_specific.ChargeScenariosEvaluator;
import org.jetbrains.annotations.NotNull;

import static java.lang.System.out;

@Log
public class RunnerChargeScenariosEvaluatorNeuralRuleForChargeDecisionPoint {

    public static final double SOC_LIMIT = 0.8;
    public static final int POS_A_START = 0, POS_B_START = 1;

    static EnvironmentInterface<ChargeVariables> environment;
    static AgentInterface<ChargeVariables> agent;
    static ChargeEnvironmentLambdas lambdas;

    public static void main(String[] args) {

        environment = new ChargeEnvironment();
        ChargeEnvironment environmentCasted;
        environmentCasted = (ChargeEnvironment) environment;
        StateInterface<ChargeVariables> state = new ChargeState(ChargeVariables.builder()
                .posA(POS_A_START).posB(POS_B_START)
                .build());
        lambdas = new ChargeEnvironmentLambdas(environmentCasted.getSettings());
        agent = new AgentChargeGreedyRuleForChargeDecisionPoint(environment,
                state,
                SOC_LIMIT,
                environmentCasted.getSettings(),
                AgentChargeNeuralSettings.newDefault());


        ChargeScenariosEvaluator evaluator= ChargeScenariosEvaluator.newAllScenarios(
                environment,
                agent);
        evaluator.evaluate();

        out.println("evaluator = " + evaluator);

    }


}
