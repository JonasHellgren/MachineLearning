package monte_carlo_tree_search.domains.elevator;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import monte_carlo_tree_search.classes.StateValueMemoryAbstract;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.network_training.Experience;
import org.jetbrains.annotations.NotNull;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;
import java.util.List;

/**
 * The memory gives the total nof expected persons waiting per step - negated
 * memory.read(SoE=0.9)=-0.1  <=>  0.1 persons expected to wait at SoE 0.9
 */

@Log

public class ElevatorStateValueMemory<SSV> extends StateValueMemoryAbstract<SSV> {
    private static final int INPUT_SIZE = 1;
    private static final int OUTPUT_SIZE = 1;
    private static final int NOF_NEURONS_HIDDEN = 2;
    private static final double LEARNING_RATE = 0.01;  //0.1
    private static final int NOF_ITERATIONS = 1;

    StateNormalizerElevator<SSV> normalizer;

    public ElevatorStateValueMemory(double minOut, double maxOut) {
        super.neuralNetwork = new MultiLayerPerceptron(
                TransferFunctionType.TANH,  //happens to be adequate for this environment
                INPUT_SIZE,
                NOF_NEURONS_HIDDEN,
                NOF_NEURONS_HIDDEN,
                OUTPUT_SIZE);
        super.learningRule = new MomentumBackpropagation();
        super.learningRule.setLearningRate(LEARNING_RATE);
        super.learningRule.setNeuralNetwork(neuralNetwork);
        super.learningRule.setMaxIterations(NOF_ITERATIONS);
        this.normalizer = new StateNormalizerElevator<>();
        super.createOutScalers(minOut, maxOut);
        super.isWarmedUp=false;
        super.settings= NetSettings.builder()
                .inputSize(INPUT_SIZE)
                .outPutSize(OUTPUT_SIZE)
                .nofNeuronsHidden(NOF_NEURONS_HIDDEN)
                .learningRate(LEARNING_RATE)
                .nofIterations(NOF_ITERATIONS)
                .build();
    }

    @SneakyThrows
    @Override
    public void write(StateInterface<SSV> state, double value) {
        throw new NoSuchMethodException("Not defined/needed - use learn instead");
    }

    @SneakyThrows
    @Override
    public double getAverageValueError(List<Experience<SSV, Integer>> experiences) {
        throw new NoSuchMethodException("Not defined");
    }


    @NotNull
    public double[] getInputVec(SSV v) {
        VariablesElevator vNorm=normalizer.normalize(v);
        return new double[]{vNorm.SoE};
    }

}
