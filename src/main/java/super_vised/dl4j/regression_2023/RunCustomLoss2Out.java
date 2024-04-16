package super_vised.dl4j.regression_2023;


import super_vised.dl4j.regression_2023.classes.CustomLoss;
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.jetbrains.annotations.NotNull;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This is an example that illustrates how to instantiate and use a custom loss function.
 * The example is identical to the one in org.deeplearning4j.examples.feedforward.regression.RegressionSum
 * except for the custom loss function
 */
public class RunCustomLoss2Out {
    static final int seed = 12345;
    static final int nEpochs = 100;  //200
    static final int nSamples = 1000;  //1000
    static final int batchSize = 100;
    static final double learningRate = 0.001;
    static final double momentum = 0.95;
    static final int PRINT_ITERATIONS = 100;
    static int MIN_RANGE = 0;
    static int MAX_RANGE = 3;
    public static final Random rng = new Random(seed);

    public static void main(String[] args) {
        DataSetIterator iterator = getTrainingData();
        MultiLayerNetwork net = createNetwork();
        trainNetwork(iterator, net);
        testNetwork(net);
    }

    private static DataSetIterator getTrainingData() {
        double[] sum = new double[nSamples];
        double[] input1 = new double[nSamples];
        double[] input2 = new double[nSamples];
        for (int i = 0; i < nSamples; i++) {
            input1[i] = MIN_RANGE + (MAX_RANGE - MIN_RANGE) * rng.nextDouble();
            input2[i] = MIN_RANGE + (MAX_RANGE - MIN_RANGE) * rng.nextDouble();
            sum[i] = input1[i] + input2[i];
        }
        INDArray inputNDArray1 = Nd4j.create(input1, new int[]{nSamples, 1});
        INDArray inputNDArray2 = Nd4j.create(input2, new int[]{nSamples, 1});
        INDArray inputNDArray = Nd4j.hstack(inputNDArray1, inputNDArray2);
        INDArray outPut1 = Nd4j.create(sum, new int[]{nSamples, 1});
        INDArray outPut2 = Nd4j.create(sum, new int[]{nSamples, 1});
        INDArray outPut = Nd4j.hstack(outPut1, outPut2.mul(2));

        DataSet dataSet = new DataSet(inputNDArray, outPut);
        System.out.println("dataSet = " + dataSet);
        List<DataSet> listDs = dataSet.asList();
        Collections.shuffle(listDs, rng);
        return new ListDataSetIterator<>(listDs, batchSize);
    }


    private static void trainNetwork(DataSetIterator iterator, MultiLayerNetwork net) {
        for (int i = 0; i < nEpochs; i++) {
            iterator.reset();
            net.fit(iterator);
        }
    }

    @NotNull
    private static MultiLayerNetwork createNetwork() {
        int numInput = 2;
        int numOutputs = 2;
        int nHidden = 5;
        MultiLayerNetwork net = new MultiLayerNetwork(new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(learningRate, momentum))
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInput).nOut(nHidden)
                        .activation(Activation.RELU)
                        .build())
              .layer(1, new OutputLayer.Builder(new CustomLoss())
                        .activation(Activation.IDENTITY)
                        .nIn(nHidden).nOut(numOutputs).build())
                .build()
        );
        net.init();
        net.setListeners(new ScoreIterationListener(PRINT_ITERATIONS));
        return net;
    }


    private static void testNetwork(MultiLayerNetwork net) {
        final INDArray input = Nd4j.create(new double[]{0.111111, 0.3333333333333}, new int[]{1, 2});
        INDArray out = net.output(input, false);
        System.out.println(out);
    }


}
