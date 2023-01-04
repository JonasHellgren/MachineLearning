package monte_carlo_tree_search.network_training;

import common.ScalerLinear;
import lombok.Getter;
import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.domains.cart_pole.EnvironmentCartPole;
import monte_carlo_tree_search.domains.cart_pole.StateCartPole;
import monte_carlo_tree_search.domains.cart_pole.StateNormalizerCartPole;
import org.jetbrains.annotations.NotNull;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class CartPoleStateValueMemory {
    private static final int INPUT_SIZE = 4;
    private static final int OUTPUT_SIZE = 1;
    private static final int NOF_NEURONS_HIDDEN = INPUT_SIZE;
    private static final double LEARNING_RATE = 0.1;
    private static final int NOF_ITERATION_WARMUP = 1;
    private static final int NET_OUT_MIN = 0;
    private static final int NET_OUT_MAX = 1;

    MultiLayerPerceptron ann;
    MomentumBackpropagation learningRule;
    StateNormalizerCartPole normalizer;
    ScalerLinear scalerOut;

    public CartPoleStateValueMemory() {
        ann = new MultiLayerPerceptron(
                TransferFunctionType.GAUSSIAN,  //GAUSSIAN
                INPUT_SIZE,
                NOF_NEURONS_HIDDEN,
                NOF_NEURONS_HIDDEN,
                OUTPUT_SIZE);
        learningRule = new MomentumBackpropagation();
        learningRule.setLearningRate(LEARNING_RATE);
        learningRule.setNeuralNetwork(ann);
        learningRule.setMaxIterations(NOF_ITERATION_WARMUP);
        normalizer = new StateNormalizerCartPole();
        scalerOut=new ScalerLinear(NET_OUT_MIN, NET_OUT_MAX,0, EnvironmentCartPole.MAX_NOF_STEPS);
        ann.learn(getWarmUpTrainingSet());  //needs warm up - else null pointer exception when calling doOneLearningIteration
    }

    public void doOneLearningIteration(List<Experience<CartPoleVariables, Integer>> miniBatch) {
        DataSet trainingSet = getDataSet(miniBatch);
      //  System.out.println("trainingSet = " + trainingSet);
        learningRule.doOneLearningIteration(trainingSet);
    }

    public double read(CartPoleVariables v) {
        double[] inputVec = getInputVec(v);
        ann.setInput(inputVec);
        ann.calculate();
        double[] output = Arrays.copyOf(ann.getOutput(), OUTPUT_SIZE);
        return scalerOut.calcOutDouble(output[0]);
    }

    public static double normalizeOutput(double value) {
        ScalerLinear scaler=new ScalerLinear(0, EnvironmentCartPole.MAX_NOF_STEPS, NET_OUT_MIN, NET_OUT_MAX);
        return scaler.calcOutDouble(value);
    }

    @NotNull
    private double[] getInputVec(CartPoleVariables v) {
        CartPoleVariables vNorm=normalizer.normalize(v);
        return new double[]{vNorm.theta, vNorm.x, vNorm.thetaDot, vNorm.xDot};
    }


    //todo normalize, Columns names
    public DataSet getDataSet(List<Experience<CartPoleVariables, Integer>> buffer) {
        DataSet trainingSet = new DataSet(INPUT_SIZE, OUTPUT_SIZE);

        for (Experience<CartPoleVariables, Integer> e : buffer) {
            CartPoleVariables v = e.stateVariables;
            double[] inputVec = getInputVec(v);
            trainingSet.add(
                    new DataSetRow(inputVec,new double[]{e.value}));

        }
        return trainingSet;

    }

    private DataSet getWarmUpTrainingSet() {
        List<Experience<CartPoleVariables, Integer>> buffer = new ArrayList<>();
        buffer.add(Experience.<CartPoleVariables, Integer>builder()
                .stateVariables(StateCartPole.newRandom().getVariables())
                .value(0)
                .build());
        return getDataSet(buffer);
    }

}
