package dl4j.regression_2023;

import common_dl4j.Dl4JUtil;
import dl4j.regression_2023.classes.SumDataGenerator;
import org.apache.commons.math3.util.Pair;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;

import java.util.ArrayList;
import java.util.List;


/**
 * Creates and trains a network adding two numbers
 */

public class RunnerTrainNeuralNetSummingTwoInputs {
    static final double MIN_VALUE = 0, MAX_VALUE = 10d;
    static final int N_SAMPLES_PER_EPOCH = 10, NOF_EPOCHS = 100, NOF_INPUTS = 2;

    static SumDataGenerator dataGenerator;
    static NeuralMemorySum neuralMemory;

    public static void main(String[] args) {
        dataGenerator = createGenerator();
        neuralMemory = NeuralMemorySum.newDefault(createNormalizerIn(),createNormalizerOut());
        var lossVersusEpisode=trainMemory();
        plotLoss(lossVersusEpisode);
        evalMemory();
    }

    private static void plotLoss(List<Double> errors) {
        XYChart chart = QuickChart.getChart("Training error", "episode", "Error", "e(ep)", null, errors);
        new SwingWrapper<>(chart).displayChart();
    }

    private static SumDataGenerator createGenerator() {
        return SumDataGenerator.builder()
                .minValue(MIN_VALUE).maxValue(MAX_VALUE).nSamplesPerEpoch(N_SAMPLES_PER_EPOCH).build();
    }

    private static NormalizerMinMaxScaler createNormalizerIn() {
        var inMinMax = List.of(Pair.create(MIN_VALUE, MAX_VALUE), Pair.create(MIN_VALUE,MAX_VALUE));
        return Dl4JUtil.createNormalizer(inMinMax);
    }

    private static NormalizerMinMaxScaler createNormalizerOut() {
        var outMinMax = List.of(Pair.create(MIN_VALUE, 2*MAX_VALUE));
        return Dl4JUtil.createNormalizer(outMinMax);
    }


    private static List<Double> trainMemory() {
        List<Double> errors=new ArrayList<>();
        for (int i = 0; i < NOF_EPOCHS; i++) {
            var trainData = dataGenerator.getTrainingData();
            neuralMemory.train(trainData.getFirst(), trainData.getSecond());
            double error=neuralMemory.getError();
            errors.add(error);
        }
        return errors;
    }

    private static void evalMemory() {
        var trainData = dataGenerator.getTrainingData();
        for (List<Double> inData : trainData.getFirst()) {
            INDArray inputNDArray = Dl4JUtil.convertList(inData,NOF_INPUTS);
            var outValue = neuralMemory.getOutValue(inputNDArray);
            System.out.println("inData = " + inData + ", outValue = " + outValue);
        }
    }

}
