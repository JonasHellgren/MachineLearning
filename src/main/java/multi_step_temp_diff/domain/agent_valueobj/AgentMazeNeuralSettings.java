package multi_step_temp_diff.domain.agent_valueobj;

import lombok.Builder;
import lombok.NonNull;
import multi_step_temp_diff.domain.environment_valueobj.MazeEnvironmentSettings;
import multi_step_temp_diff.domain.normalizer.NormalizeMinMax;
import multi_step_temp_diff.domain.normalizer.NormalizerInterface;

import static common.DefaultPredicates.defaultIfNullDouble;
import static common.DefaultPredicates.defaultIfNullInteger;

@Builder
public record AgentMazeNeuralSettings(
        Integer nofStates,
        Integer nofLayersHidden,
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
    public static final double DISCOUNT_FACTOR = 1d;


    /*
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

    */

    @Builder
    public AgentMazeNeuralSettings(Integer nofStates,
                                   Integer nofLayersHidden,
                                   Double minValue,
                                   Double maxValue,
                                   double discountFactor,
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
        this.normalizer=new NormalizeMinMax(0,envSettings.rewardGoal()*2);
    }

    public static AgentMazeNeuralSettings newDefault() {
        return AgentMazeNeuralSettings.builder().build();
    }




}
