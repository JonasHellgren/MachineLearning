package multi_step_temp_diff.memory;


import common.ScalerLinear;
import monte_carlo_tree_search.domains.cart_pole.StateNormalizerCartPole;
import multi_step_temp_diff.models.ForkEnvironment;
import multi_step_temp_diff.models.ValueMemoryNetworkAbstract;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

import java.util.Arrays;

public class ForkNeuralValueMemory<S> extends ValueMemoryNetworkAbstract<S> {
    private static final int INPUT_SIZE = 1;
    private static final int OUTPUT_SIZE = 1;
    private static final int NOF_NEURONS_HIDDEN = 10;
    private static final double LEARNING_RATE = 0.001;
    private static final int MARGIN = 2;

    ScalerLinear scaler;

    public ForkNeuralValueMemory(double minOut, double maxOut) {
        neuralNetwork = new MultiLayerPerceptron(
                TransferFunctionType.TANH,
                INPUT_SIZE,
                NOF_NEURONS_HIDDEN, NOF_NEURONS_HIDDEN, //NOF_NEURONS_HIDDEN,
                OUTPUT_SIZE);
        super.settings=NetSettings.builder().inputSize(INPUT_SIZE).outPutSize(OUTPUT_SIZE)
                .nofNeuronsHidden(NOF_NEURONS_HIDDEN).learningRate(LEARNING_RATE).build();
        super.createLearningRule(neuralNetwork,settings);
        scaler = new ScalerLinear(0, ForkEnvironment.NOF_STATES,-1,1);
        createOutScalers(minOut* MARGIN, maxOut*MARGIN);
        isWarmedUp=false;
    }

    @Override
    public double[] getInputVec(Integer v) {

      //  double [] myarray = new double[ForkEnvironment.NOF_STATES];
     //   Arrays.fill(myarray, 0);
      //  myarray[v]=1d;

        return new double[]{scaler.calcOutDouble((double) v)};
    }
}
