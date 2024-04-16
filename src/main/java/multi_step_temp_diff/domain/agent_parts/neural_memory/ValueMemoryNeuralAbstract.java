package multi_step_temp_diff.domain.agent_parts.neural_memory;

import common.other.Conditionals;
import common.math.MathUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.NstepExperience;
import multi_step_temp_diff.domain.agent_parts.neural_memory.normalizer.NormalizerInterface;
import org.neuroph.core.Layer;
import org.neuroph.core.Neuron;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The concrete implementation of this class is adapted for a domain
 * The settings netSettings.minOut(), netSettings.maxOut() are debatable, can maybe be removed
 * Only one output value is handled, see getNetworkOutputValue
 * Careful setting of normalizer is essential
 *
 */

@Getter
@Log
public abstract class ValueMemoryNeuralAbstract<S> implements NetworkMemoryInterface<S> {
    private static final int NOF_ITERATIONS = 1;
    public static final String NON_EQUAL_NETS_WHEN_COPYING_WEIGHT = "Non equal nets when copying weight";

    public MultiLayerPerceptron neuralNetwork;
    public MomentumBackpropagation learningRule;
    NormalizerInterface normalizer;
    public boolean isWarmedUp;
    public NetSettings netSettings;


    public ValueMemoryNeuralAbstract(NetSettings netSettings) {
        List<Integer> nofNeurons = new ArrayList<>();
        nofNeurons.add(netSettings.inputSize());
        for (int i = 0; i < netSettings.nofHiddenLayers(); i++) {
            nofNeurons.add(netSettings.nofNeuronsHidden());
        }
        nofNeurons.add(netSettings.outPutSize());

        this.neuralNetwork = new MultiLayerPerceptron(nofNeurons, netSettings.transferFunctionType());
        this.netSettings = netSettings;
        createLearningRule(neuralNetwork, netSettings);
        normalizer = netSettings.normalizer();
        isWarmedUp = false;
    }

    public abstract double[] getInputVec(StateInterface<S> state);

    public void createLearningRule(MultiLayerPerceptron neuralNetwork, NetSettings settings) {
        learningRule = new MomentumBackpropagation();
        learningRule.setLearningRate(settings.learningRate());
        learningRule.setNeuralNetwork(neuralNetwork);
        learningRule.setMomentum(settings.momentum());
        learningRule.setMaxIterations(NOF_ITERATIONS);
    }

    @Override
    public double read(StateInterface<S> state) {
        double[] inputVec = getInputVec(state);
        return getNetworkOutputValue(inputVec);
    }

    protected double getNetworkOutputValue(double[] inputVec) {
        neuralNetwork.setInput(inputVec);
        neuralNetwork.calculate();
        double[] output = Arrays.copyOf(neuralNetwork.getOutput(), netSettings.outPutSize());
        return normalizer.normalizeInverted(output[0]);
    }


    @Override
    public double learn(List<NstepExperience<S>> miniBatch) {
        DataSet trainingSet = getDataSet(miniBatch);
        doWarmUpIfNotDone(miniBatch);
        learningRule.doOneLearningIteration(trainingSet);
        return learningRule.getTotalNetworkError();
    }

    /**
     * Calculate loss function (error) regarding weight, needed by prioritized buffer
     * Not spotless, more desirable to modify loss function. But not managed in Neuroph.
     */

    @Override
    public void learnUsingWeights(List<NstepExperience<S>> miniBatch) {
        doWarmUpIfNotDone(miniBatch);

        for (NstepExperience<S> e : miniBatch) {
            learningRule.setLearningRate(netSettings.learningRate()*e.weight);
            learningRule.doOneLearningIteration(getDataSet(List.of(e)));
        }
        learningRule.getTotalNetworkError();
    }

    /**
     * needs warm up - else null pointer exception when calling doOneLearningIteration
     */
    private void doWarmUpIfNotDone(List<NstepExperience<S>> miniBatch) {
        Conditionals.executeIfTrue(!isWarmedUp, () -> {
            neuralNetwork.learn(getDataSet(miniBatch));
            isWarmedUp = true;
        });
    }

    private DataSet getDataSet(List<NstepExperience<S>> buffer) {
        DataSet trainingSet = new DataSet(netSettings.inputSize(), netSettings.outPutSize());
        for (NstepExperience<S> e : buffer) {
            double[] inputVec = getInputVec(e.stateToUpdate);
            double valueClipped = MathUtils.clip(e.value, netSettings.minOut(), netSettings.maxOut());
            double normalizedValue = normalizer.normalize(valueClipped);
            trainingSet.add(new DataSetRow(inputVec, new double[]{normalizedValue}));
        }
        return trainingSet;
    }

    @Override
    public void save(String fileName) {
        log.info("Saving network in file = " + fileName);
        neuralNetwork.save(fileName);
    }

    @Override
    public void load(String fileName) {
        neuralNetwork = (MultiLayerPerceptron) MultiLayerPerceptron.createFromFile(fileName);
    }

    @SneakyThrows
    @Override
    public NetworkMemoryInterface<S> copy() {
        throw new NoSuchMethodException();  //throwing if not defined in concrete class
    }

    @Override
    public void copyWeights(NetworkMemoryInterface<S> netOther) {
        ValueMemoryNeuralAbstract<S> netOtherCasted=(ValueMemoryNeuralAbstract<S>) netOther;

        if (this.neuralNetwork.getLayersCount()!=netOtherCasted.neuralNetwork.getLayersCount()) {
            throw new IllegalArgumentException(NON_EQUAL_NETS_WHEN_COPYING_WEIGHT);
        }

        for (int i = 0; i < this.neuralNetwork.getLayersCount(); i++) {
            Layer targetLayer = this.neuralNetwork.getLayerAt(i);
            Layer sourceLayer = netOtherCasted.neuralNetwork.getLayerAt(i);

            if (sourceLayer.getNeuronsCount()!=targetLayer.getNeuronsCount()) {
                throw new IllegalArgumentException(NON_EQUAL_NETS_WHEN_COPYING_WEIGHT);
            }
            for (int j = 0; j < sourceLayer.getNeuronsCount(); j++) {
                Neuron sourceNeuron = sourceLayer.getNeuronAt(j);
                Neuron targetNeuron = targetLayer.getNeuronAt(j);

                for (int k = 0; k < sourceNeuron.getWeights().length; k++) {
                    double weight = sourceNeuron.getWeights()[k].getValue();
                    targetNeuron.getWeights()[k].setValue(weight);
                }
            }
        }
    }


}
