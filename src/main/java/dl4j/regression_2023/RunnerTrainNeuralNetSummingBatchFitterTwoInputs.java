package dl4j.regression_2023;

import common.CpuTimer;
import common.ListUtils;
import common_dl4j.Dl4JUtil;
import dl4j.regression_2023.classes.SumDataGenerator;
import org.apache.commons.math3.util.Pair;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.List;


/**
 * Creates and trains a network adding two numbers
 */

public class RunnerTrainNeuralNetSummingBatchFitterTwoInputs {
    static final double MIN_VALUE = 0, MAX_VALUE = 10d;
    static final int N_SAMPLES = 50, NOF_EPOCHS = 100,  NOF_OUTPUTS = 1;

    static SumDataGenerator dataGenerator;
    static NeuralMemorySumUsingBatchFitter neuralMemory;
    static NormalizerMinMaxScaler normalizerIn,normalizerOut;

    public static void main(String[] args) {
        CpuTimer timer=new CpuTimer();
        dataGenerator = createGenerator();
        normalizerIn=createNormalizerIn();
        normalizerOut=createNormalizerOut();

        var trainingData=dataGenerator.getTrainingData();
        neuralMemory = NeuralMemorySumUsingBatchFitter.newDefault();
        var lossVersusEpisode=trainMemory(trainingData);
        plotLoss(lossVersusEpisode);
        evalMemory();
        System.out.println("time used (millis) = " + timer.absoluteProgressInMillis());
    }

    private static void plotLoss(List<Double> errors) {
        XYChart chart = QuickChart.getChart("Training error", "episode", "Error", "e(ep)", null, errors);
        new SwingWrapper<>(chart).displayChart();
    }

    private static SumDataGenerator createGenerator() {
        return SumDataGenerator.builder()
                .minValue(MIN_VALUE).maxValue(MAX_VALUE).nSamplesPerEpoch(N_SAMPLES).build();
    }

    private static NormalizerMinMaxScaler createNormalizerIn() {
        var inMinMax = List.of(Pair.create(MIN_VALUE, MAX_VALUE), Pair.create(MIN_VALUE,MAX_VALUE));
        return Dl4JUtil.createNormalizer(inMinMax);
    }

    private static NormalizerMinMaxScaler createNormalizerOut() {
        var outMinMax = List.of(Pair.create(MIN_VALUE, 2*MAX_VALUE));
        return Dl4JUtil.createNormalizer(outMinMax);
    }


    private static List<Double> trainMemory(Pair<List<List<Double>> , List<Double>>  trainData) {
        List<Double> errors=new ArrayList<>();
        INDArray inputNDArray = Dl4JUtil.convertListOfLists(trainData.getFirst());
        INDArray outPutNDArray = Nd4j.create(ListUtils.toArray(trainData.getSecond()), N_SAMPLES,  NOF_OUTPUTS);
        normalizerIn.transform(inputNDArray);
        normalizerOut.transform(outPutNDArray);

        for (int i = 0; i < NOF_EPOCHS; i++) {
            neuralMemory.train(inputNDArray,outPutNDArray);
            double error=neuralMemory.getError();
            errors.add(error);
        }
        return errors;
    }

    private static void evalMemory() {
        var trainData = dataGenerator.getTrainingData();
        for (List<Double> inData : trainData.getFirst()) {
            INDArray inputNDArray = Dl4JUtil.convertListToOneRow(inData);
            var outValue = getOutValue(inputNDArray);
            System.out.println("inData = " + inData + ", outValue = " + outValue+", error = "+Math.abs(outValue- ListUtils.sumList(inData)));
        }
    }

    public static Double getOutValue(INDArray inData) {
        normalizerIn.transform(inData);
        INDArray output = neuralMemory.getNet().output(inData, false);
        normalizerOut.revertFeatures(output);
        return output.getDouble();
    }

}
