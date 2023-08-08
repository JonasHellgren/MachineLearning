package multi_step_temp_diff.domain.agent_valueobj;

import lombok.Builder;
import multi_step_temp_diff.domain.agent_abstract.NetworkMemoryInterface;
import multi_step_temp_diff.domain.agents.fork.NeuralValueMemoryFork;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;

@Builder
public record AgentForkNeuralSettings(
        NetworkMemoryInterface<ForkVariables> memory,
        int startState,
        Double discountFactor
) implements AgentSettingsInterface {

    public static final int START_STATE = 0;
    public static final NeuralValueMemoryFork<ForkVariables> MEMORY = new NeuralValueMemoryFork<>();

    public static AgentForkNeuralSettings getDefault() {

        return AgentForkNeuralSettings.builder()
                .memory(MEMORY)
                .startState(START_STATE)
                .discountFactor(1d)
                .build();
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
        return AgentForkNeuralSettings.builder()
                .memory(MEMORY)
                .startState(START_STATE)
                .discountFactor(discountFactor)
                .build();
    }

}
