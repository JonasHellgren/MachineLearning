package dl4j.regression_2023;

import common.Dl4JUtil;
import org.apache.commons.math3.util.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import java.util.List;


/**
 * https://medium.com/@AlexanderObregon/getting-started-with-machine-learning-in-java-using-deeplearning4j-3a5dc47dbbf4
 */

public class FitSum2 {
    public static final double MIN_VALUE = 0, MAX_VALUE = 10d;
    public static final int N_SAMPLES_PER_EPOCH = 10;
    static int NOF_INPUTS = 2,NOF_OUTPUTS = 1;

    static SumDataGenerator dataGenerator;
    static NormalizerMinMaxScaler normalizer;
    static MemoryNeuralSum neuralMemory;

    public static void main(String[] args) {
        int nofEpochs = 100;
        dataGenerator = SumDataGenerator.builder()
                .minValue(MIN_VALUE).maxValue(MAX_VALUE).nSamplesPerEpoch(N_SAMPLES_PER_EPOCH).build();
        var inMinMax = List.of(Pair.create(MIN_VALUE, MAX_VALUE), Pair.create(MIN_VALUE,MAX_VALUE));
        var outMinMax = List.of(Pair.create(MIN_VALUE, 2*MAX_VALUE));
        normalizer=Dl4JUtil.createNormalizer(inMinMax,outMinMax);
        neuralMemory = MemoryNeuralSum.newDefault(normalizer);
        trainMemory(nofEpochs);
        evalMemory();
    }


    private static void trainMemory(int nofEpochs) {
        for (int i = 0; i < nofEpochs; i++) {
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
