package monte_carlo_tree_search.domains.cart_pole;

import common.ListUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import monte_carlo_tree_search.models_and_support_classes.ValueMemoryNetworkAbstract;
import monte_carlo_tree_search.interfaces.StateInterface;
import monte_carlo_tree_search.network_training.Experience;
import org.jetbrains.annotations.NotNull;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

import java.util.ArrayList;
import java.util.List;

/***
 * This memory is adapted for the cart pole environment, a neural network is used.
 */

@Getter
public class CartPoleValueMemoryNetwork<SSV,AV> extends ValueMemoryNetworkAbstract<SSV,AV> {
    private static final int INPUT_SIZE = 4;
    private static final int OUTPUT_SIZE = 1;
    private static final int NOF_NEURONS_HIDDEN = INPUT_SIZE;
    private static final double LEARNING_RATE = 0.1;

    StateNormalizerCartPole<SSV> normalizer;

    public CartPoleValueMemoryNetwork() {
        this(0,EnvironmentCartPole.MAX_NOF_STEPS_DEFAULT);
    }
    public CartPoleValueMemoryNetwork(double minOut, double maxOut) {
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


    @Override
    public double getAverageValueError(List<Experience<SSV, AV>> experienceList) {
        List<Double> errors=new ArrayList<>();
        for (Experience<SSV, AV> e : experienceList) {
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
