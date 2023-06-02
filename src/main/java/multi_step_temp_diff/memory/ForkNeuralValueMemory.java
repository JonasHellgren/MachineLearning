package multi_step_temp_diff.memory;

import multi_step_temp_diff.models.ForkEnvironment;
import multi_step_temp_diff.models.ValueMemoryNetworkAbstract;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;
import java.util.Arrays;

/**
 * Input is a binary vector with zeros except at active state. Much more stable than one double input.
 */

public class ForkNeuralValueMemory<S> extends ValueMemoryNetworkAbstract<S> {

    private static final int NOF_STATES = ForkEnvironment.NOF_STATES;
    private static final int INPUT_SIZE = NOF_STATES;
    private static final int OUTPUT_SIZE = 1;
    private static final int NOF_NEURONS_HIDDEN = INPUT_SIZE;
    private static final double MARGIN = 1.0;

    //todo snyggare constuktor
    public ForkNeuralValueMemory(double minOut, double maxOut,double learningRate) {
        neuralNetwork = new MultiLayerPerceptron(
                TransferFunctionType.TANH,
                INPUT_SIZE,
                NOF_NEURONS_HIDDEN, //NOF_NEURONS_HIDDEN,
                OUTPUT_SIZE);
        super.settings = NetSettings.builder()
                .inputSize(INPUT_SIZE).outPutSize(OUTPUT_SIZE).nofNeuronsHidden(NOF_NEURONS_HIDDEN)
                .minOut(minOut).maxOut(maxOut).netOutMin(0.0).netOutMax(1.0)
                .learningRate(learningRate).build();
        super.createLearningRule(neuralNetwork, settings);
        createOutScalers(minOut * MARGIN, maxOut * MARGIN);
        isWarmedUp = false;
    }

    @Override
    public double[] getInputVec(Integer s) {
        double[] inArray = new double[NOF_STATES];
        Arrays.fill(inArray, 0);
        inArray[s] = 1d;
        return inArray;
    }
}
