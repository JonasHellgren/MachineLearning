package multi_step_temp_diff.domain.agents.charge;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import multi_step_temp_diff.domain.agent_abstract.AgentAbstract;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_abstract.NetworkMemoryInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agent_parts.NstepExperience;
import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.agent_valueobj.NetSettings;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.InputVectorSetterChargeInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Getter
public class AgentChargeNeural extends AgentAbstract<ChargeVariables> implements AgentNeuralInterface<ChargeVariables> {

    AgentChargeNeuralSettings agentSettings;
    NetworkMemoryInterface<ChargeVariables> memory;

    @Builder
    public AgentChargeNeural(@NonNull EnvironmentInterface<ChargeVariables> environment,
                             @NonNull StateInterface<ChargeVariables> state,
                             @NonNull AgentChargeNeuralSettings agentSettings,
                             @NonNull InputVectorSetterChargeInterface inputVectorSetterCharge) {
        super(environment, state, agentSettings);
        this.agentSettings =agentSettings;
        NetSettings netSettings = NetSettings.builder()
                .inputSize(inputVectorSetterCharge.inputSize()).nofNeuronsHidden(agentSettings.nofStates())
                .nofHiddenLayers(agentSettings.nofLayersHidden())
                .minOut(agentSettings.minValue()).maxOut(agentSettings.maxValue())
                .nofNeuronsHidden(agentSettings.nofNeuronsHidden())
                .transferFunctionType(agentSettings.transferFunctionType())
                .learningRate(agentSettings.learningRate()).momentum(agentSettings.momentum())
                .normalizer(agentSettings.valueNormalizer())
                .build();
        memory=new NeuralValueMemoryCharge<>(netSettings,inputVectorSetterCharge);
    }


    @Override
    public double readValue(StateInterface<ChargeVariables> state) {
        return memory.read(state);
    }

    @Override
    public void learn(List<NstepExperience<ChargeVariables>> miniBatch) {
        memory.learn(miniBatch);
        List<Double> errors=new ArrayList<>();
        for (NstepExperience<ChargeVariables> exp: miniBatch) {
            errors.add(Math.abs(exp.value-memory.read(exp.stateToUpdate)));
        }
        addErrorsToHistory(errors);
    }

    @Override
    public void saveMemory(String fileName) {
        memory.save(fileName);
    }

    @Override
    public void loadMemory(String fileName) {
        memory.load(fileName);
    }


}
