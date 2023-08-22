package multi_step_temp_diff.runners;

import common.ListUtils;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agent_parts.neural_memory.normalizer.NormalizerMeanStd;
import multi_step_temp_diff.domain.agents.charge.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.agents.charge.AgentChargeNeural;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.HotEncodingSoCAtOccupiedElseValue;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.helpers_specific.ChargeScenariosEvaluator;
import org.neuroph.util.TransferFunctionType;

import java.util.List;
import static multi_step_temp_diff.domain.helpers_specific.ChargeAgentParameters.*;
import static multi_step_temp_diff.domain.helpers_specific.ChargeScenariosFactory.*;

public class RunnerChargeScenariosEvaluatorNeural {
    public static final double ALPHA = 3d;

    static AgentNeuralInterface<ChargeVariables> agent;
    static EnvironmentInterface<ChargeVariables> environment;
    static ChargeEnvironmentSettings envSettings;

    public static void main(String[] args) {
        envSettings = ChargeEnvironmentSettings.newDefault();
        environment = new ChargeEnvironment(envSettings);
        agent = buildAgent(ChargeState.newDummy());

        agent.loadMemory(FOLDER_NETWORKS + FILENAME_CHARGE_BOTH_FREE_NET);
        StateInterface<ChargeVariables> state = BatPosSplit_AatPos40_BothModerateSoC.state();
        printStateAndValue(state);

        agent.loadMemory(FOLDER_NETWORKS + FILENAME_CHARGE_BOTH_FREE_NET);
        printStateAndValue(state);
        ChargeScenariosEvaluator evaluator = ChargeScenariosEvaluator.newAllScenarios(environment,agent);

        System.out.println("evaluator = " + evaluator);


    }

    private static void printStateAndValue(StateInterface<ChargeVariables> state) {
        System.out.println("state = " + state);
        System.out.println("value BatPosSplit_AatPos40_BothModerateSoC.state= " + agent.readValue(state));
    }


    private static AgentNeuralInterface<ChargeVariables> buildAgent(ChargeState initState) {
        AgentChargeNeuralSettings agentSettings = AgentChargeNeuralSettings.builder()
                .transferFunctionType(TransferFunctionType.TANH)
                .valueNormalizer(new NormalizerMeanStd(ListUtils.merge(
                        List.of(envSettings.rewardBad() * ALPHA),
                        CHARGE_REWARD_VALUES_EXCEPT_FAIL)))
                .build();

        return AgentChargeNeural.builder()
                .environment(environment).state(initState)
                .agentSettings(agentSettings)
                .inputVectorSetterCharge(
                        new HotEncodingSoCAtOccupiedElseValue(
                                agentSettings,
                                envSettings,
                                NORMALIZER_CHARGE_INPUT_ONEDOTONE, VALUE_IF_NOT_OCCUPIED))
                .build();
    }

}
