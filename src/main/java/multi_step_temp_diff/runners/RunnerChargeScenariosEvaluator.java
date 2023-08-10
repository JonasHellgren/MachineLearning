package multi_step_temp_diff.runners;

import common.ListUtils;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_abstract.normalizer.NormalizerMeanStd;
import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.agents.charge.AgentChargeNeural;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.HotEncodingSoCAtOccupiedElseValue;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.helpers_specific.ChargeAgentNeuralHelper;
import multi_step_temp_diff.domain.helpers_specific.ChargeScenariosEvaluator;
import org.neuroph.util.TransferFunctionType;

import java.util.List;

import static multi_step_temp_diff.domain.helpers_specific.ChargeAgentNeuralHelper.*;
import static multi_step_temp_diff.domain.helpers_specific.ChargeScenariousContainer.*;

public class RunnerChargeScenariosEvaluator {
    public static final double ALPHA = 3d;

    static AgentNeuralInterface<ChargeVariables> agent;
    static EnvironmentInterface<ChargeVariables> environment;
    static ChargeEnvironmentSettings envSettings;

    public static void main(String[] args) {
        envSettings = ChargeEnvironmentSettings.newDefault();
        environment = new ChargeEnvironment(envSettings);
        agent=buildAgent(ChargeState.newDummy());
        agent.loadMemory(FOLDER_NETWORKS+FILENAME_CHARGE_BOTH_FREE_NET);

        ChargeScenariosEvaluator evaluator= ChargeScenariosEvaluator.builder()
                .environment(environment).agent(agent)
                .scenarios(List.of(
                      BatPos0At1BothHighSoC,
                       BatPos0AtPosSplitLowSoCA,
                        BatPos0At20BothHighSoC,
                       BatPosSplitAtPos40BothModerateSoC,
                        BJustBehindLowSoC_AatSplitModerateSoC
                ) )
                .build();

        List<Double> sumRewardList=evaluator.evaluate();

        System.out.println("sumRewardList = " + sumRewardList);

        System.out.println("evaluator = " + evaluator);


    }


    private static  AgentNeuralInterface<ChargeVariables> buildAgent(ChargeState initState) {
        AgentChargeNeuralSettings agentSettings = AgentChargeNeuralSettings.builder()
                .transferFunctionType(TransferFunctionType.TANH)
                .valueNormalizer(new NormalizerMeanStd(ListUtils.merge(
                        List.of(envSettings.rewardBad() * ALPHA),
                        ChargeAgentNeuralHelper.CHARGE_REWARD_VALUES_EXCEPT_FAIL)))
                .build();

        return AgentChargeNeural.builder()
                .environment(environment).state(initState)
                .agentSettings(agentSettings)
                .inputVectorSetterCharge(
                        new HotEncodingSoCAtOccupiedElseValue(
                                agentSettings,
                                envSettings,
                                NORMALIZER_ONEDOTONE, VALUE_IF_NOT_OCCUPIED))
                .build();
    }

}
