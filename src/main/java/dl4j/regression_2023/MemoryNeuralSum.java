package dl4j.regression_2023;

import common.ListUtils;
import lombok.Builder;
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class MemoryNeuralSum {


    @Builder
    public record Settings(
            double learningRate,
            int nHidden) {
    }

    public static final int RAND_SEED = 12345;  //Random number generator seed, for reproducability
    static int NOF_OUTPUTS = 1, NOF_INPUTS = 2;

    MultiLayerNetwork net;
    public static final Random rng = new Random(RAND_SEED);


    public static MemoryNeuralSum newDefault() {
        return new MemoryNeuralSum(Settings.builder().learningRate(1e-3).nHidden(2).build());
    }

    public MemoryNeuralSum(Settings settings) {

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(RAND_SEED)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(settings.learningRate, 0.9))
                .list()
                .layer(0, new DenseLayer.Builder().nIn(NOF_INPUTS).nOut(settings.nHidden)
                        .activation(Activation.RELU) //Can be RELU
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(settings.nHidden).nOut(NOF_OUTPUTS).build())
                .build();
        this.net = new MultiLayerNetwork(conf);
        net.init();
    }


    public void train(List<List<Double>> in, List<Double> out) {
        int length = in.size();
        INDArray inputNDArray = getIndArray(in);
        INDArray outPut = Nd4j.create(ListUtils.toArray(out), length, NOF_OUTPUTS);
        DataSet dataSet = new DataSet(inputNDArray, outPut);
        List<DataSet> listDs = dataSet.asList();
        Collections.shuffle(listDs, rng);
        DataSetIterator iterator = new ListDataSetIterator<>(listDs, length);
        iterator.reset();
        net.fit(iterator);
    }


    public Double getOutValue(List<Double> inData) {
        List<List<Double>> inDataList=new ArrayList<>();
        inDataList.add(inData);
        INDArray output = net.output(getIndArray(inDataList), false);
        return output.getDouble(NOF_OUTPUTS -1);
    }

    private static INDArray getIndArray(List<List<Double>> in) {
        int numRows = in.size();
        int numColumns = in.get(0).size();

        if (numColumns!= NOF_INPUTS) {
            throw new IllegalArgumentException("bad numColumns, numColumns = "+numColumns);
        }

        double[] flatArray = new double[numRows * numColumns];
        for (int i = 0; i < numRows; i++) {
            List<Double> row = in.get(i);
            for (int j = 0; j < numColumns; j++) {
                flatArray[i * numColumns + j] = row.get(j);
            }
        }
        return Nd4j.create(flatArray, new int[]{numRows, numColumns});
    }


}
