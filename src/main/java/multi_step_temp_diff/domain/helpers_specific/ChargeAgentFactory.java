package multi_step_temp_diff.domain.helpers_specific;

import common.ListUtils;
import lombok.Builder;
import lombok.NonNull;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_parts.neural_memory.normalizer.NormalizerMeanStd;
import multi_step_temp_diff.domain.agents.charge.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.agents.charge.AgentChargeNeural;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.HotEncodingSoCAtOccupiedElseValue;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import org.neuroph.util.TransferFunctionType;

import java.util.List;

import static multi_step_temp_diff.domain.helpers_specific.ChargeAgentParameters.*;

@Builder
public class ChargeAgentFactory {

    @NonNull EnvironmentInterface<ChargeVariables> environment;
    @NonNull ChargeEnvironmentSettings envSettings;
    @Builder.Default
    double discountFactor = DISCOUNT_FACTOR;
    @Builder.Default
    public  double learningRate  = LEARNING_RATE ;


    public AgentNeuralInterface<ChargeVariables> buildAgent(ChargeState initState,
                                                            int nofLayers,
                                                            int nofNeuronsHidden) {
        AgentChargeNeuralSettings agentSettings = AgentChargeNeuralSettings.builder()
                .learningRate(learningRate).discountFactor(discountFactor).momentum(MOMENTUM)
                .nofLayersHidden(nofLayers).nofNeuronsHidden(nofNeuronsHidden)
                .transferFunctionType(TransferFunctionType.TANH)
                .valueNormalizer(new NormalizerMeanStd(ListUtils.merge(
                        List.of(envSettings.rewardBad() * ALPHA),
                        CHARGE_REWARD_VALUES_EXCEPT_FAIL)))
                .build();

        AgentNeuralInterface<ChargeVariables> agent = AgentChargeNeural.builder()
                .environment(environment).state(initState)
                .agentSettings(agentSettings)
                .inputVectorSetterCharge(
                        new HotEncodingSoCAtOccupiedElseValue(
                                agentSettings,
                                envSettings,
                                NORMALIZER_CHARGE_INPUT_ONEDOTONE, VALUE_IF_NOT_OCCUPIED))
                .build();

        ChargeAgentNeuralHelper helper = ChargeAgentNeuralHelper.builder()
                .agent(agent).build();
        helper.resetAgentMemory(envSettings, BUFFER_SIZE_FOR_RESET, TIME_BUDGET_RESET);

        return agent;
    }
}
