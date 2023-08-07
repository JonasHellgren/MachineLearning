package multi_step_temp_diff.domain.agents.charge;

import lombok.Builder;
import lombok.NonNull;
import multi_step_temp_diff.domain.agent_abstract.AgentAbstract;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_abstract.NetworkMemoryInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agent_parts.NstepExperience;
import multi_step_temp_diff.domain.agent_parts.ValueTracker;
import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.agent_valueobj.NetSettings;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.InputVectorSetterChargeInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;

import java.util.List;


public class AgentChargeNeural extends AgentAbstract<ChargeVariables> implements AgentNeuralInterface<ChargeVariables> {

    AgentChargeNeuralSettings agentSettings;
    NetworkMemoryInterface<ChargeVariables> memory;

    @Builder
    public AgentChargeNeural(@NonNull EnvironmentInterface<ChargeVariables> environment,
                             @NonNull StateInterface<ChargeVariables> state,
                             @NonNull AgentChargeNeuralSettings agentSettings,
                             @NonNull InputVectorSetterChargeInterface inputVectorSetterCharge) {
        super(environment, state, agentSettings.discountFactor());
        this.agentSettings =agentSettings;
        NetSettings netSettings = NetSettings.builder()
                .inputSize(agentSettings.nofStates()).nofNeuronsHidden(agentSettings.nofStates())
                .minOut(agentSettings.minValue()).maxOut(agentSettings.maxValue())
                .learningRate(agentSettings.learningRate())
                .normalizer(agentSettings.normalizer())
                .build();
        memory=new NeuralValueMemoryCharge<>(netSettings,inputVectorSetterCharge);
    }


    @Override
    public double readValue(StateInterface<ChargeVariables> state) {
        return memory.read(state);
    }

    @Override
    public void learn(List<NstepExperience<ChargeVariables>> miniBatch) {
        double error=memory.learn(miniBatch);
        errorHistory.addValue(error);
    }
}
