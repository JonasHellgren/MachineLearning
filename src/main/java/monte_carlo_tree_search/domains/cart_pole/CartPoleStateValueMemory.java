package monte_carlo_tree_search.domains.cart_pole;

import common.Conditionals;
import common.ListUtils;
import common.ScalerLinear;
import lombok.Getter;
import lombok.SneakyThrows;
import monte_carlo_tree_search.classes.StateValueMemoryAbstract;
import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.domains.cart_pole.EnvironmentCartPole;
import monte_carlo_tree_search.domains.cart_pole.StateNormalizerCartPole;
import monte_carlo_tree_search.generic_interfaces.NetworkMemoryInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.network_training.Experience;
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
public class CartPoleStateValueMemory<SSV> extends StateValueMemoryAbstract<SSV> {
    private static final int INPUT_SIZE = 4;
    private static final int OUTPUT_SIZE = 1;
    private static final int NOF_NEURONS_HIDDEN = INPUT_SIZE;
    private static final double LEARNING_RATE = 0.1;

    StateNormalizerCartPole<SSV> normalizer;

    public CartPoleStateValueMemory() {
        this(0,EnvironmentCartPole.MAX_NOF_STEPS_DEFAULT);
    }
    public CartPoleStateValueMemory(double minOut, double maxOut) {
        neuralNetwork = new MultiLayerPerceptron(
                TransferFunctionType.GAUSSIAN,  //happens to be adequate for this environment
                INPUT_SIZE,
                NOF_NEURONS_HIDDEN,
                NOF_NEURONS_HIDDEN,
                OUTPUT_SIZE);
        super.settings= NetSettings.builder()
                .inputSize(INPUT_SIZE)
                .outPutSize(OUTPUT_SIZE)
                .nofNeuronsHidden(NOF_NEURONS_HIDDEN)
                .learningRate(LEARNING_RATE)
                .build();
        super.createLearningRule(neuralNetwork,settings);
        normalizer = new StateNormalizerCartPole<>();
        createOutScalers(minOut, maxOut);
        isWarmedUp=false;
    }

    @SneakyThrows
    @Override
    public void write(StateInterface<SSV> state, double value) {
        throw new NoSuchMethodException("Not defined/needed - use learn instead");
    }

    @Override
    public double getAverageValueError(List<Experience<SSV, Integer>> experienceList) {
        List<Double> errors=new ArrayList<>();
        for (Experience<SSV, Integer> e : experienceList) {
            double expectedValue= e.value;
            double memoryValue=getNetworkOutputValue(getInputVec(e.stateVariables));
            errors.add(Math.abs(expectedValue-memoryValue));
        }
        return ListUtils.findAverage(errors).orElseThrow();
    }

    @NotNull
    public double[] getInputVec(SSV v) {
        CartPoleVariables vNorm=normalizer.normalize(v);
        return new double[]{vNorm.theta, vNorm.x, vNorm.thetaDot, vNorm.xDot};
    }



}
