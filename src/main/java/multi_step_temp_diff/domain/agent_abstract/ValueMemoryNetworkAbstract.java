package multi_step_temp_diff.domain.agent_abstract;

import common.Conditionals;
import common.MathUtils;
import lombok.Getter;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_valueobj.NetSettings;
import multi_step_temp_diff.domain.agent_parts.NstepExperience;
import multi_step_temp_diff.domain.agent_abstract.normalizer.NormalizerInterface;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Log
public abstract class ValueMemoryNetworkAbstract<S> implements NetworkMemoryInterface<S> {

    private static final double MAX_ERROR = 0.00001;
    private static final int NOF_ITERATIONS = 1;

    public MultiLayerPerceptron neuralNetwork;
    public MomentumBackpropagation learningRule;
    NormalizerInterface normalizer;
    public boolean isWarmedUp;
    public NetSettings netSettings;


    public ValueMemoryNetworkAbstract(NetSettings netSettings) {
        List<Integer> nofNeurons=new ArrayList<>();
        nofNeurons.add(netSettings.inputSize());
        for (int i = 0; i < netSettings.nofHiddenLayers() ; i++) {
            nofNeurons.add(netSettings.nofNeuronsHidden());
        }
        nofNeurons.add(netSettings.outPutSize());

        this.neuralNetwork = new MultiLayerPerceptron(nofNeurons,netSettings.transferFunctionType());
        this.netSettings = netSettings;
        createLearningRule(neuralNetwork, netSettings);
        normalizer=netSettings.normalizer();
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
        doWarmUpIfNotDone(trainingSet);
        learningRule.doOneLearningIteration(trainingSet);
        return learningRule.getTotalNetworkError();
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

    private DataSet getDataSet(List<NstepExperience<S>> buffer) {
        DataSet trainingSet = new DataSet(netSettings.inputSize(), netSettings.outPutSize());
        for (NstepExperience<S> e : buffer) {
            double[] inputVec = getInputVec(e.stateToUpdate);
            double valueClipped= MathUtils.clip(e.value, netSettings.minOut(), netSettings.maxOut());
            double normalizedValue= normalizer.normalize(valueClipped);
            trainingSet.add( new DataSetRow(inputVec,new double[]{normalizedValue}));
        }
        return trainingSet;
    }

    @Override
    public void save(String fileName) {
        log.info("Saving network in file = "+fileName);
        neuralNetwork.save(fileName);
    }

    @Override
    public void load(String fileName) {
        neuralNetwork = (MultiLayerPerceptron) MultiLayerPerceptron.createFromFile(fileName);
    //    neuralNetwork = (MultiLayerPerceptron) MultiLayerPerceptron.load(fileName);


    }



}
