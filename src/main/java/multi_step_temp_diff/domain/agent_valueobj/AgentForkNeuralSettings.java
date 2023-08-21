package multi_step_temp_diff.domain.agent_valueobj;

import lombok.Builder;
import multi_step_temp_diff.domain.agent_abstract.NetworkMemoryInterface;
import multi_step_temp_diff.domain.agent_abstract.normalizer.NormalizeMinMax;
import multi_step_temp_diff.domain.agents.fork.NeuralValueMemoryFork;
import multi_step_temp_diff.domain.environment_valueobj.ForkEnvironmentSettings;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import org.neuroph.util.TransferFunctionType;

@Builder
public record AgentForkNeuralSettings(
        NetworkMemoryInterface<ForkVariables> memory,
        int startState,
        Double discountFactor
) implements AgentSettingsInterface {

    public static final int START_STATE = 0;

    public static AgentForkNeuralSettings getDefault() {

        NetSettings netSettings = getNetSettings();

        return AgentForkNeuralSettings.builder()
                .memory(new NeuralValueMemoryFork<>(netSettings))
                .startState(START_STATE)
                .discountFactor(1d)
                .build();
    }

    private static NetSettings getNetSettings() {
        ForkEnvironmentSettings envSettings=ForkEnvironmentSettings.getDefault();
        return NetSettings.builder()
                .inputSize(envSettings.nofStates()).nofNeuronsHidden(envSettings.nofStates())
                .minOut(envSettings.rewardHell()).maxOut(envSettings.rewardHeaven())
                .nofHiddenLayers(1).transferFunctionType(TransferFunctionType.TANH)
                .normalizer(new NormalizeMinMax(envSettings.rewardHell(),envSettings.rewardHeaven())).build();
    }

    public static AgentForkNeuralSettings getWithDiscountFactorAndMemorySettings(double discountFactor,
                                                                                 NetSettings netSettings) {
        return AgentForkNeuralSettings.builder()
                .memory(new NeuralValueMemoryFork<>(netSettings))
                .startState(START_STATE)
                .discountFactor(discountFactor)
                .build();
    }

    public static AgentForkNeuralSettings getWithDiscountFactor(double discountFactor) {
        NetSettings netSettings = getNetSettings();
        return AgentForkNeuralSettings.builder()
                .memory(new NeuralValueMemoryFork<>(netSettings))
                .startState(START_STATE)
                .discountFactor(discountFactor)
                .build();
    }

}
