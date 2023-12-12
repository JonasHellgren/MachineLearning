package dl4j.regression_2023;

import common.Dl4JUtil;
import common.ListUtils;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import java.util.List;
import java.util.Random;


/**
 * https://medium.com/@AlexanderObregon/getting-started-with-machine-learning-in-java-using-deeplearning4j-3a5dc47dbbf4
 */

public class FitSum2 {
    static int NOF_INPUTS = 2,NOF_OUTPUTS = 1;

    static  NormalizerMinMaxScaler normalizer;

    public static void main(String[] args) {
        int nofEpochs = 100;
        var dataGenerator = SumDataGenerator.builder()
                .minValue(0).maxValue(10).nSamplesPerEpoch(10).build();
        normalizer = new NormalizerMinMaxScaler();

        List<List<Double>> in=List.of(List.of(0d,10d),List.of(0d,10d));
        List<Double> out=List.of(0d,20d);

        INDArray inputNDArray = Dl4JUtil.getIndArray(in,2);
        INDArray outPut = Nd4j.create(ListUtils.toArray(out), in.size(), 1);
        DataSetIterator iterator = Dl4JUtil.getDataSetIterator(inputNDArray, outPut,new Random());
        normalizer.fit(iterator);


        var neuralMemory = MemoryNeuralSum.newDefault(normalizer);
        trainMemory(nofEpochs, dataGenerator, neuralMemory);
        evalMemory(dataGenerator, neuralMemory);
    }
    private static void trainMemory(int nofEpochs, SumDataGenerator dataGenerator, MemoryNeuralSum neuralMemory) {
        for (int i = 0; i < nofEpochs; i++) {
            var trainData = dataGenerator.getTrainingData();
            neuralMemory.train(trainData.getFirst(), trainData.getSecond());
        }
    }

    private static void evalMemory(SumDataGenerator dataGenerator, MemoryNeuralSum neuralMemory) {
        var trainData = dataGenerator.getTrainingData();
        for (List<Double> inData : trainData.getFirst()) {
            INDArray inputNDArray = Dl4JUtil.getIndArray(List.of(inData),NOF_INPUTS);
            INDArray outPut = Nd4j.create(ListUtils.toArray(List.of(0d)), 1, NOF_OUTPUTS);

            DataSet dataSet=new DataSet(inputNDArray, outPut);
            normalizer.transform(dataSet);

            var outValue = neuralMemory.getOutValue(inData);
            normalizer.revertLabels(Nd4j.create(ListUtils.toArray(List.of(outValue)), 1, NOF_OUTPUTS));

            System.out.println("inData = " + inData + ", outValue = " + outValue);
        }
    }


}
