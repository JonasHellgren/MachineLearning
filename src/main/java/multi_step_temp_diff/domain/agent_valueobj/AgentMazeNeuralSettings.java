package multi_step_temp_diff.domain.agent_valueobj;

import lombok.Builder;
import multi_step_temp_diff.domain.agent_abstract.NetworkMemoryInterface;
import multi_step_temp_diff.domain.agents.maze.MazeNeuralValueMemory;
import multi_step_temp_diff.domain.environments.maze.MazeVariables;
import java.util.HashMap;

@Builder
public record AgentMazeNeuralSettings(
        double discountFactor,
        double learningRate,
        NetworkMemoryInterface<MazeVariables> memory,
        int startX,
        int startY)
{
    public static final double VALUE_IF_NOT_PRESENT=0;
    public static final double LEARNING_RATE=0.5;
    public static final int START_X = 0, START_Y = 0;

    public static AgentMazeNeuralSettings getWithDiscountAndLearningRate(double discountFactor,double learningRate) {
        return AgentMazeNeuralSettings.builder()
                .discountFactor(discountFactor)
                .learningRate(learningRate)
                .memory(new MazeNeuralValueMemory<>(learningRate))
                .startX(START_X).startY(START_Y)
                .build();
    }

    public static AgentMazeNeuralSettings getDefault() {
        return AgentMazeNeuralSettings.builder()
                .discountFactor(1)
                .learningRate(LEARNING_RATE)
                .memory(new MazeNeuralValueMemory<>(LEARNING_RATE))
                .startX(START_X).startY(START_Y)
                .build();
    }


}
