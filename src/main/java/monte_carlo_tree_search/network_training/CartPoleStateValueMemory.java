package monte_carlo_tree_search.network_training;

import lombok.Getter;
import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.domains.cart_pole.StateCartPole;
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
    private static final int NOF_NEURONS_HIDDEN = 10;
    private static final double LEARNING_RATE = 0.001;
    private static final int NOF_ITERATION_WARMUP = 1;

    MultiLayerPerceptron ann;
    MomentumBackpropagation learningRule;

    public CartPoleStateValueMemory() {
        ann = new MultiLayerPerceptron(
                TransferFunctionType.TANH,
                INPUT_SIZE,
                NOF_NEURONS_HIDDEN,
                NOF_NEURONS_HIDDEN,
                OUTPUT_SIZE);
        learningRule = new MomentumBackpropagation();
        learningRule.setLearningRate(LEARNING_RATE);
        learningRule.setNeuralNetwork(ann);
        learningRule.setMaxIterations(NOF_ITERATION_WARMUP);
        ann.learn(getWarmUpTrainingSet());  //needs warm up - else null pointer exception when calling doOneLearningIteration
    }

    public void doOneLearningIteration(List<Experience<CartPoleVariables, Integer>> miniBatch) {
        DataSet trainingSet = getDataSet(miniBatch);
       // System.out.println("trainingSet = " + trainingSet);
        learningRule.doOneLearningIteration(trainingSet);
    }

    public double read(CartPoleVariables v) {
        double[] inputVec = new double[]{v.theta, v.x, v.thetaDot, v.xDot};
            ann.setInput(inputVec);
            ann.calculate();
            double[] output = Arrays.copyOf(ann.getOutput(), OUTPUT_SIZE);
            return output[0];

    }


    //todo normalize, Columns names
    public DataSet getDataSet(List<Experience<CartPoleVariables, Integer>> buffer) {
        DataSet trainingSet = new DataSet(INPUT_SIZE, OUTPUT_SIZE);

        for (Experience<CartPoleVariables, Integer> e : buffer) {
            CartPoleVariables v = e.stateVariables;
            trainingSet.add(
                    new DataSetRow(new double[]{v.theta, v.x, v.thetaDot, v.xDot},
                    new double[]{e.value}));

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
