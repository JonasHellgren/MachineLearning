package dl4j.regression_2023;

import common.Dl4JUtil;
import org.apache.commons.math3.util.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import java.util.List;


/**
 * Creates and trains a network adding two numbers
 */

public class RunnerTrainNeuralNetSummingTwoInputs {
    static final double MIN_VALUE = 0, MAX_VALUE = 10d;
    static final int N_SAMPLES_PER_EPOCH = 10, NOF_EPOCHS = 100;
    static final int NOF_INPUTS = 2,NOF_OUTPUTS = 1;

    static SumDataGenerator dataGenerator;
    static NormalizerMinMaxScaler normalizer;
    static MemoryNeuralSum neuralMemory;

    public static void main(String[] args) {
        dataGenerator = createGenerator();
        normalizer = createNormalizer();
        neuralMemory = MemoryNeuralSum.newDefault(normalizer);
        trainMemory();
        evalMemory();
    }

    private static SumDataGenerator createGenerator() {
        return SumDataGenerator.builder()
                .minValue(MIN_VALUE).maxValue(MAX_VALUE).nSamplesPerEpoch(N_SAMPLES_PER_EPOCH).build();
    }

    private static NormalizerMinMaxScaler createNormalizer() {
        var inMinMax = List.of(Pair.create(MIN_VALUE, MAX_VALUE), Pair.create(MIN_VALUE,MAX_VALUE));
        var outMinMax = List.of(Pair.create(MIN_VALUE, 2*MAX_VALUE));
        return Dl4JUtil.createNormalizer(inMinMax,outMinMax);
    }

    private static void trainMemory() {
        for (int i = 0; i < NOF_EPOCHS; i++) {
            var trainData = dataGenerator.getTrainingData();
            neuralMemory.train(trainData.getFirst(), trainData.getSecond());
        }
    }

    private static void evalMemory() {
        var trainData = dataGenerator.getTrainingData();
        for (List<Double> inData : trainData.getFirst()) {
            INDArray inputNDArray = Dl4JUtil.convertList(inData,NOF_INPUTS);
            DataSet dataSet=new DataSet(inputNDArray, Nd4j.zeros(NOF_OUTPUTS));
            normalizer.transform(dataSet);
            var outValue = neuralMemory.getOutValue(inData);
            normalizer.revertLabels(Dl4JUtil.convertList(List.of(outValue), NOF_OUTPUTS));
            System.out.println("inData = " + inData + ", outValue = " + outValue);
        }
    }

}
