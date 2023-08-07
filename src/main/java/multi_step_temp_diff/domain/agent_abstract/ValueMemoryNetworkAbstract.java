package multi_step_temp_diff.domain.agent_abstract;

import common.Conditionals;
import common.MathUtils;
import common.ScalerLinear;
import lombok.Builder;
import lombok.Getter;
import multi_step_temp_diff.domain.agent_valueobj.NetSettings;
import multi_step_temp_diff.domain.agent_parts.NstepExperience;
import multi_step_temp_diff.domain.normalizer.NormalizeMinMax;
import multi_step_temp_diff.domain.normalizer.NormalizerInterface;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import java.util.Arrays;
import java.util.List;

@Getter

public abstract class ValueMemoryNetworkAbstract<S> implements NetworkMemoryInterface<S> {

    private static final double MAX_ERROR = 0.00001;
    private static final int NOF_ITERATIONS = 1;

    public MultiLayerPerceptron neuralNetwork;
    public MomentumBackpropagation learningRule;
    NormalizerInterface normalizer;
    public boolean isWarmedUp;
    public NetSettings netSettings;


    public ValueMemoryNetworkAbstract(MultiLayerPerceptron neuralNetwork,
                                      NetSettings netSettings) {
        this.neuralNetwork = neuralNetwork;
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

      //  System.out.println("learningRule.getLearningRate() = " + learningRule.getLearningRate());

        DataSet trainingSet = getDataSet(miniBatch);
        doWarmUpIfNotDone(trainingSet);
        learningRule.doOneLearningIteration(trainingSet);


        return learningRule.getTotalNetworkError();

        //      System.out.println("after");

    }

    public void createNormalizer(double minOut, double maxOut) {
        normalizer=new NormalizeMinMax(minOut,maxOut);
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

    public void save(String fileName) {
        neuralNetwork.save(fileName);
    }

    public void load(String fileName) {
        neuralNetwork = (MultiLayerPerceptron) MultiLayerPerceptron.createFromFile(fileName);
    }



}
