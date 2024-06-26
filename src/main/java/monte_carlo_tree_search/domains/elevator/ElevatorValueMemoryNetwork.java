package monte_carlo_tree_search.domains.elevator;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import monte_carlo_tree_search.models_and_support_classes.ValueMemoryNetworkAbstract;
import monte_carlo_tree_search.interfaces.StateInterface;
import monte_carlo_tree_search.network_training.Experience;
import org.jetbrains.annotations.NotNull;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;
import java.util.List;

/**
 * The memory gives the total nof expected persons waiting per step - negated
 * memory.read(SoE=0.9)=-0.1  <=>  0.1 persons expected to wait at SoE 0.9
 */

@Log

public class ElevatorValueMemoryNetwork<SSV,AV> extends ValueMemoryNetworkAbstract<SSV,AV> {
    private static final int INPUT_SIZE = 1;
    private static final int OUTPUT_SIZE = 1;
    private static final int NOF_NEURONS_HIDDEN = 2;
    private static final double LEARNING_RATE = 0.01;  //0.1

    StateNormalizerElevator<SSV> normalizer;

    public ElevatorValueMemoryNetwork(double minOut, double maxOut) {
        super.neuralNetwork = new MultiLayerPerceptron(
                TransferFunctionType.TANH,  //happens to be adequate for this environment
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
        this.normalizer = new StateNormalizerElevator<>();
        super.createOutScalers(minOut, maxOut);
        super.isWarmedUp=false;

    }

    @SneakyThrows
    @Override
    public double getAverageValueError(List<Experience<SSV, AV>> experiences) {
        throw new NoSuchMethodException("Not defined");
    }


    @NotNull
    public double[] getInputVec(SSV v) {
        VariablesElevator vNorm=normalizer.normalize(v);
        return new double[]{vNorm.SoE};
    }

}
