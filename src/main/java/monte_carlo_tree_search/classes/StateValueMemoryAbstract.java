package monte_carlo_tree_search.classes;

import common.Conditionals;
import common.ScalerLinear;
import lombok.Builder;
import lombok.Getter;
import monte_carlo_tree_search.generic_interfaces.NetworkMemoryInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.network_training.Experience;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;

import java.util.Arrays;
import java.util.List;

@Getter
public abstract class StateValueMemoryAbstract <SSV> implements NetworkMemoryInterface<SSV> {

    private static final double NET_OUT_MIN = 0;
    private static final double NET_OUT_MAX = 1;
    private static final double MAX_ERROR = 0.00001;
    private static final int NOF_ITERATIONS = 1;


    @Builder
    public static class NetSettings {
        public int inputSize, outPutSize, nofNeuronsHidden;
        public double learningRate;
    }

    public MultiLayerPerceptron neuralNetwork;
    // NeuralNetwork neuralNetwork;
    public MomentumBackpropagation learningRule;
    public ScalerLinear scaleOutValueToNormalized;
    public ScalerLinear scaleOutNormalizedToValue;
    public boolean isWarmedUp;
    public NetSettings settings;

    public abstract double[] getInputVec(SSV v);


    public void createLearningRule(MultiLayerPerceptron neuralNetwork, NetSettings settings) {
        learningRule = new MomentumBackpropagation();
        learningRule.setLearningRate(settings.learningRate);
        learningRule.setNeuralNetwork(neuralNetwork);
        learningRule.setMaxIterations(NOF_ITERATIONS);
    }

    @Override
    public double read(StateInterface<SSV> state) {
        double[] inputVec = getInputVec(state.getVariables());
        return getNetworkOutputValue(inputVec);
    }

    protected double getNetworkOutputValue(double[] inputVec) {
        neuralNetwork.setInput(inputVec);
        neuralNetwork.calculate();
        double[] output = Arrays.copyOf(neuralNetwork.getOutput(), settings.outPutSize);
        return scaleOutNormalizedToValue.calcOutDouble(output[0]);
    }

    @Override
    public void save(String fileName) {
        neuralNetwork.save(fileName);
    }

    @Override
    public void load(String fileName) {
        neuralNetwork = (MultiLayerPerceptron) MultiLayerPerceptron.createFromFile(fileName);
    }

    @Override
    public void learn(List<Experience<SSV, Integer>> miniBatch) {
        DataSet trainingSet = getDataSet(miniBatch);
        doWarmUpIfNotDone(trainingSet);
        learningRule.doOneLearningIteration(trainingSet);
    }


    @Override
    public void createOutScalers(double minOut, double maxOut) {
        scaleOutNormalizedToValue =new ScalerLinear(NET_OUT_MIN, NET_OUT_MAX,minOut, maxOut);
        scaleOutValueToNormalized =new ScalerLinear(minOut, maxOut, NET_OUT_MIN, NET_OUT_MAX);
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

    private DataSet getDataSet(List<Experience<SSV, Integer>> buffer) {
        DataSet trainingSet = new DataSet(settings.inputSize, settings.outPutSize);
        for (Experience<SSV, Integer> e : buffer) {
            SSV v = e.stateVariables;
            double[] inputVec = getInputVec(v);
            double normalizedValue= scaleOutValueToNormalized.calcOutDouble(e.value);
            trainingSet.add( new DataSetRow(inputVec,new double[]{normalizedValue}));
        }
        return trainingSet;
    }

}
