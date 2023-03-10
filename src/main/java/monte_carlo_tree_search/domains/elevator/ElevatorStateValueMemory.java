package monte_carlo_tree_search.domains.elevator;

import common.Conditionals;
import common.ScalerLinear;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
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
import java.util.Arrays;
import java.util.List;

@Log
public class ElevatorStateValueMemory<SSV> implements NetworkMemoryInterface<SSV> {
    private static final int INPUT_SIZE = 1;
    private static final int OUTPUT_SIZE = 1;
    private static final int NOF_NEURONS_HIDDEN = 1;
    private static final double LEARNING_RATE = 0.01;  //0.1
    private static final int NOF_ITERATIONS = 1;
    private static final double NET_OUT_MIN = 0;
    private static final double NET_OUT_MAX = 1;

    //MultiLayerPerceptron neuralNetwork;
    NeuralNetwork neuralNetwork;
    MomentumBackpropagation learningRule;
    StateNormalizerElevator<SSV> normalizer;
    ScalerLinear scaleOutValueToNormalized;
    ScalerLinear scaleOutNormalizedToValue;
    boolean isWarmedUp;

    public ElevatorStateValueMemory(double minOut, double maxOut) {
        neuralNetwork = new MultiLayerPerceptron(
                TransferFunctionType.SIGMOID,  //happens to be adequate for this environment
                INPUT_SIZE,
                NOF_NEURONS_HIDDEN,
                NOF_NEURONS_HIDDEN,
                OUTPUT_SIZE);
        learningRule = new MomentumBackpropagation();
        learningRule.setLearningRate(LEARNING_RATE);
        learningRule.setNeuralNetwork(neuralNetwork);
        learningRule.setMaxIterations(NOF_ITERATIONS);
        normalizer = new StateNormalizerElevator<>();
        createOutScalers(minOut, maxOut);
        isWarmedUp=false;
    }

    @SneakyThrows
    @Override
    public void write(StateInterface<SSV> state, double value) {
        throw new NoSuchMethodException("Not defined/needed - use learn instead");
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

    @Override
    public void save(String fileName) {
        neuralNetwork.save(fileName);
    }

    @Override
    public void load(String fileName) {
        neuralNetwork = MultiLayerPerceptron.createFromFile(fileName);
    }

    @Override
    public void learn(List<Experience<SSV, Integer>> miniBatch) {
        DataSet trainingSet = getDataSet(miniBatch);
        log.info("trainingSet = " + trainingSet);
        doWarmUpIfNotDone(trainingSet);
        learningRule.doOneLearningIteration(trainingSet);
    }


    @SneakyThrows
    @Override
    public MomentumBackpropagation getLearningRule() {
    return (MomentumBackpropagation) neuralNetwork.getLearningRule();
    }



    @Override
    public double getAverageValueError(List<Experience<SSV, Integer>> experiences) {
        return 0;
    }

    @Override
    public void createOutScalers(double minOut, double maxOut) {
        scaleOutNormalizedToValue =new ScalerLinear(NET_OUT_MIN, NET_OUT_MAX,minOut, maxOut);
        scaleOutValueToNormalized =new ScalerLinear(minOut, maxOut, NET_OUT_MIN, NET_OUT_MAX);

    }

    //todo, below methods to interface or abstract class

    @NotNull
    private double[] getInputVec(SSV v) {
        VariablesElevator vNorm=normalizer.normalize(v);
        return new double[]{vNorm.SoE};
    }

    /**
     * needs warm up - else null pointer exception when calling doOneLearningIteration
     */
    private void doWarmUpIfNotDone(DataSet trainingSet) {
        Conditionals.executeIfTrue(!isWarmedUp, () -> {
            log.info("Warming up");
            System.out.println("trainingSet = " + trainingSet);
            neuralNetwork.learn(trainingSet);
            log.info("after learn");
            isWarmedUp=true;
        });
    }

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

}
