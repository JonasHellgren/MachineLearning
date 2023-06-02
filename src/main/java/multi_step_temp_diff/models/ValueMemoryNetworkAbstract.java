package multi_step_temp_diff.models;

import common.Conditionals;
import common.MathUtils;
import common.ScalerLinear;
import lombok.Builder;
import lombok.Getter;
import multi_step_temp_diff.interfaces.NetworkMemoryInterface;
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
    public ScalerLinear scaleOutValueToNormalized;
    public ScalerLinear scaleOutNormalizedToValue;
    public boolean isWarmedUp;
    public NetSettings settings;

    public abstract double[] getInputVec(Integer v);


    public void createLearningRule(MultiLayerPerceptron neuralNetwork, NetSettings settings) {
        learningRule = new MomentumBackpropagation();
        learningRule.setLearningRate(settings.learningRate);
        learningRule.setNeuralNetwork(neuralNetwork);
        learningRule.setMaxIterations(NOF_ITERATIONS);
    }

    @Override
    public double read(S state) {
        double[] inputVec = getInputVec((Integer) state);  //todo S
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
    public void learn(List<NstepExperience> miniBatch) {
        DataSet trainingSet = getDataSet(miniBatch);
        doWarmUpIfNotDone(trainingSet);
        learningRule.doOneLearningIteration(trainingSet);
    }


    @Override
    public void createOutScalers(double minOut, double maxOut) {
        scaleOutNormalizedToValue =new ScalerLinear(settings.netOutMin, settings.netOutMax,minOut, maxOut);
        scaleOutValueToNormalized =new ScalerLinear(minOut, maxOut, settings.netOutMin, settings.netOutMax);
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

    private DataSet getDataSet(List<NstepExperience> buffer) {
        DataSet trainingSet = new DataSet(settings.inputSize, settings.outPutSize);
        for (NstepExperience e : buffer) {
            double[] inputVec = getInputVec(e.stateToUpdate);  //todo cottect?
            double valueClipped= MathUtils.clip(e.value,settings.minOut,settings.maxOut);
            double normalizedValue= scaleOutValueToNormalized.calcOutDouble(valueClipped);
            trainingSet.add( new DataSetRow(inputVec,new double[]{normalizedValue}));
        }
        return trainingSet;
    }


    @Override
    public double getAverageValueError(List<NstepExperience> experienceList) {  //todo - to abstract

      /*  List<Double> errors=new ArrayList<>();
        for (NstepExperience e : experienceList) {
            double expectedValue= e.value;
            double memoryValue=getNetworkOutputValue(getInputVec(e.stateVariables));
            errors.add(Math.abs(expectedValue-memoryValue));
        }
        return ListUtils.findAverage(errors).orElseThrow();

        */
        return 0;
    }



}
