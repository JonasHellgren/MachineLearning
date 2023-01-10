package monte_carlo_tree_search.network_training;

import common.Conditionals;
import common.ListUtils;
import common.ScalerLinear;
import lombok.Getter;
import lombok.SneakyThrows;
import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.domains.cart_pole.EnvironmentCartPole;
import monte_carlo_tree_search.domains.cart_pole.StateNormalizerCartPole;
import monte_carlo_tree_search.generic_interfaces.NetworkMemoryInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import org.jetbrains.annotations.NotNull;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/***
 * This memory is adapted for the cart pole environment, a neural network is used.
 */

@Getter
public class CartPoleStateValueMemory<SSV> implements NetworkMemoryInterface<SSV> {
    private static final int INPUT_SIZE = 4;
    private static final int OUTPUT_SIZE = 1;
    private static final int NOF_NEURONS_HIDDEN = INPUT_SIZE;
    private static final double LEARNING_RATE = 0.1;
    private static final int NOF_ITERATIONS = 1;
    private static final int NET_OUT_MIN = 0;
    private static final int NET_OUT_MAX = 1;

    //MultiLayerPerceptron neuralNetwork;
    NeuralNetwork neuralNetwork;
    MomentumBackpropagation learningRule;
    StateNormalizerCartPole<SSV> normalizer;
    ScalerLinear scaleOutValueToNormalized;
    ScalerLinear scaleOutNormalizedToValue;
    boolean isWarmedUp;

    public CartPoleStateValueMemory() {
        neuralNetwork = new MultiLayerPerceptron(
                TransferFunctionType.GAUSSIAN,  //happens to be adequate for this environment
                INPUT_SIZE,
                NOF_NEURONS_HIDDEN,
                NOF_NEURONS_HIDDEN,
                OUTPUT_SIZE);
        learningRule = new MomentumBackpropagation();
        learningRule.setLearningRate(LEARNING_RATE);
        learningRule.setNeuralNetwork(neuralNetwork);
        learningRule.setMaxIterations(NOF_ITERATIONS);
        normalizer = new StateNormalizerCartPole<>();
        createOutScalers(EnvironmentCartPole.MAX_NOF_STEPS_DEFAULT);
        isWarmedUp=false;
    }

    public void createOutScalers(int maxNofSteps) {
        scaleOutNormalizedToValue =new ScalerLinear(NET_OUT_MIN, NET_OUT_MAX,0, maxNofSteps);
        scaleOutValueToNormalized =new ScalerLinear(0, maxNofSteps, NET_OUT_MIN, NET_OUT_MAX);
    }

    public void learn(List<Experience<SSV, Integer>> miniBatch) {
        DataSet trainingSet = getDataSet(miniBatch);
        doWarmUpIfNotDone(trainingSet);
        learningRule.doOneLearningIteration(trainingSet);
    }

    @Override
    public double read(StateInterface<SSV> state) {
        double[] inputVec = getInputVec(state.getVariables());
        return getNetworkOutputValue(inputVec);
    }

    private double getNetworkOutputValue(double[] inputVec) {
        neuralNetwork.setInput(inputVec);
        neuralNetwork.calculate();
        double[] output = Arrays.copyOf(neuralNetwork.getOutput(), OUTPUT_SIZE);
        return scaleOutNormalizedToValue.calcOutDouble(output[0]);
    }

    @SneakyThrows
    @Override
    public void write(StateInterface<SSV> state, double value) {
        throw new NoSuchMethodException("Not defined/needed - use learn instead");
    }

    @Override
    public double getAverageValueError(ReplayBuffer<SSV, Integer> buffer) {
        List<Double> errors=new ArrayList<>();
        for (Experience<SSV, Integer> e : buffer.getBuffer()) {
            double expectedValue= e.value;
            SSV v = e.stateVariables;
            double memoryValue=getNetworkOutputValue(getInputVec(v));
            errors.add(Math.abs(expectedValue-memoryValue));
        }
        return ListUtils.findMax(errors).orElseThrow();
    }

    @NotNull
    private double[] getInputVec(SSV v) {
        CartPoleVariables vNorm=normalizer.normalize(v);
        return new double[]{vNorm.theta, vNorm.x, vNorm.thetaDot, vNorm.xDot};
    }

    /**
     * needs warm up - else null pointer exception when calling doOneLearningIteration
     */
    private void doWarmUpIfNotDone(DataSet trainingSet) {
        Conditionals.executeIfTrue(!isWarmedUp, () -> {
            neuralNetwork.learn(trainingSet);
            isWarmedUp=true;
        });
    }

    //todo define columns names
    private DataSet getDataSet(List<Experience<SSV, Integer>> buffer) {
        DataSet trainingSet = new DataSet(INPUT_SIZE, OUTPUT_SIZE);
        for (Experience<SSV, Integer> e : buffer) {
            SSV v = e.stateVariables;
            double[] inputVec = getInputVec(v);
            double normalizedValue= scaleOutValueToNormalized.calcOutDouble(e.value);
            trainingSet.add( new DataSetRow(inputVec,new double[]{normalizedValue}));
        }
        return trainingSet;
    }


    @Override
    public void save(String fileName)  {
        neuralNetwork.save(fileName);
    }

    @Override
    public void load(String fileName) {
        neuralNetwork = MultiLayerPerceptron.createFromFile(fileName);
    }

}
