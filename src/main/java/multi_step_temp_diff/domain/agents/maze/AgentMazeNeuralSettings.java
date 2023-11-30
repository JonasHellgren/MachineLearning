package multi_step_temp_diff.domain.agents.maze;

import lombok.Builder;
import multi_step_temp_diff.domain.agent_parts.neural_memory.normalizer.NormalizerMeanStd;
import multi_step_temp_diff.domain.agent_abstract.AgentSettingsInterface;
import multi_step_temp_diff.domain.environments.maze.MazeEnvironmentSettings;
import multi_step_temp_diff.domain.agent_parts.neural_memory.normalizer.NormalizerInterface;

import java.util.List;

import static common.MyFunctions.*;

@Builder
public record AgentMazeNeuralSettings(
        Integer nofStates,
        Integer nofLayersHidden,
        Double minValue,
        Double maxValue,
        Double discountFactor,
        double learningRate,
        int startX,
        int startY,
        NormalizerInterface normalizer)
implements AgentSettingsInterface {
    public static final double VALUE_IF_NOT_PRESENT=0;
    public static final double LEARNING_RATE=0.5;
    public static final int START_X = 0, START_Y = 0;
    public static final double DISCOUNT_FACTOR = 1d;
    public static final List<Double> VALUE_LIST = List.of(100 * 10d, 80d, 50d, 30d, 50d, 30d);

    @Builder
    public AgentMazeNeuralSettings(Integer nofStates,
                                   Integer nofLayersHidden,
                                   Double minValue,
                                   Double maxValue,
                                   Double discountFactor,
                                   double learningRate,
                                   int startX,
                                   int startY,
                                   NormalizerInterface normalizer) {
        MazeEnvironmentSettings envSettings= MazeEnvironmentSettings.getDefault();
        this.nofStates = envSettings.nofCols()+  envSettings.nofRows();
        this.nofLayersHidden = defaultIfNullInteger.apply(nofLayersHidden, 1);
        this.startX = defaultIfNullInteger.apply(startX, START_X);
        this.startY = defaultIfNullInteger.apply(startY, START_Y);
        this.minValue = 0d;
        this.maxValue = envSettings.rewardGoal();
        this.discountFactor = defaultIfNullDouble.apply(discountFactor, DISCOUNT_FACTOR);
        this.learningRate = defaultIfNullDouble.apply(learningRate, LEARNING_RATE);
        //this.nofNeuronsHidden = defaultIfNullInteger.apply(nofNeuronsHidden, nofStates);
        //this.normalizer=new NormalizeMinMax(0,envSettings.rewardGoal()*2);
        this.normalizer= (NormalizerInterface) defaultIfNullObject.apply(normalizer,new NormalizerMeanStd(VALUE_LIST));
    }

    public static AgentMazeNeuralSettings newDefault() {
        return AgentMazeNeuralSettings.builder().build();
    }




}
