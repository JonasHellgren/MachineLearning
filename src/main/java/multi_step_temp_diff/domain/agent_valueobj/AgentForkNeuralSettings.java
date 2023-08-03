package multi_step_temp_diff.domain.agent_valueobj;

import lombok.Builder;
import multi_step_temp_diff.domain.agent_abstract.NetworkMemoryInterface;
import multi_step_temp_diff.domain.agents.fork.ForkNeuralValueMemory;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;

@Builder
public record AgentForkNeuralSettings(
        NetworkMemoryInterface<ForkVariables> memory,
        int startState,
        double discountFactor
) {

    public static final int START_STATE = 0;
    public static final ForkNeuralValueMemory<ForkVariables> MEMORY = new ForkNeuralValueMemory<>();

    public static AgentForkNeuralSettings getDefault() {

        return AgentForkNeuralSettings.builder()
                .memory(MEMORY)
                .startState(START_STATE)
                .discountFactor(1)
                .build();
    }

    public static AgentForkNeuralSettings getWithDiscountFactorAndMemorySettings(double discountFactor,
                                                                                 NetSettings netSettings) {
        return AgentForkNeuralSettings.builder()
                .memory(new ForkNeuralValueMemory<>(netSettings))
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
