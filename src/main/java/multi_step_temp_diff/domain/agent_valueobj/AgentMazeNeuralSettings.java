package multi_step_temp_diff.domain.agent_valueobj;

import lombok.Builder;
import multi_step_temp_diff.domain.agent_abstract.NetworkMemoryInterface;
import multi_step_temp_diff.domain.agents.maze.NeuralValueMemoryMaze;
import multi_step_temp_diff.domain.environment_valueobj.MazeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.maze.MazeEnvironment;
import multi_step_temp_diff.domain.environments.maze.MazeVariables;
import multi_step_temp_diff.domain.normalizer.NormalizeMinMax;
import multi_step_temp_diff.domain.normalizer.NormalizerInterface;

@Builder
public record AgentMazeNeuralSettings(
        Integer nofStates,
        Double minValue,
        Double maxValue,
        double discountFactor,
        double learningRate,
        int startX,
        int startY,
        NormalizerInterface normalizer)
{
    public static final double VALUE_IF_NOT_PRESENT=0;
    public static final double LEARNING_RATE=0.5;
    public static final int START_X = 0, START_Y = 0;

    public static AgentMazeNeuralSettings getWithDiscountAndLearningRate(double discountFactor,double learningRate) {
        MazeEnvironmentSettings envSettings= MazeEnvironmentSettings.getDefault();

        return AgentMazeNeuralSettings.builder()
                .nofStates((int) (envSettings.nofCols()+  envSettings.nofRows()))
                .minValue(0d).maxValue(envSettings.rewardGoal()*2)
                .discountFactor(discountFactor)
                .learningRate(learningRate)
                .startX(START_X).startY(START_Y)
                .normalizer(new NormalizeMinMax(0,envSettings.rewardGoal()*2))
                .build();
    }

    public static AgentMazeNeuralSettings getDefault() {
        MazeEnvironmentSettings envSettings= MazeEnvironmentSettings.getDefault();
        return AgentMazeNeuralSettings.builder()
                .nofStates((int) (envSettings.nofCols()+  envSettings.nofRows()))
                .minValue(0d).maxValue(envSettings.rewardGoal()*2)
                .discountFactor(1)
                .learningRate(LEARNING_RATE)
                .startX(START_X).startY(START_Y)
                .normalizer(new NormalizeMinMax(0,envSettings.rewardGoal()*2))
                .build();
    }


}
