package dl4j.regressionnetworks;


import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * An example of regression neural network for performing addition
 * Increasing range gives non stable learning.
 * Note the use of the identity function on the network output layer, for regression
 */
@SuppressWarnings({"DuplicatedCode", "FieldCanBeLocal"})
public class SumRegression {

    public static final int seed = 12345;  //Random number generator seed, for reproducibility
    public static final int nEpochs = 500;  //Number of epochs (full passes of the data)
    private static final int nIterationsBetweenPrints = 100;
    private static final int nSamples = 1000;  //Number of data points
    public static final int batchSize = 100;  //each epoch has nSamples/batchSize parameter updates
    public static final double learningRate = 0.01;
    public static final double momentum = 0.9; //for avoiding local minima
    private static final int rangeMin = -5;  // The range of the sample data,
    private static final int rangeMax = 5;
    public static final Random rng = new Random(seed);

    public static final int numInput = 2;
    public static final int nHidden = 10;
    public static final int numOutputs = 1;

    public static void main(String[] args){
        //Generate the training data
        DataSetIterator iterator = getTrainingData(batchSize,rng);
        //Create the network

        MultiLayerNetwork config = new MultiLayerNetwork(new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(learningRate, momentum))
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInput).nOut(nHidden)
                        .activation(Activation.RELU).build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(nHidden).nOut(numOutputs).build())
                .build()
        );
        config.init();
        config.setListeners(new ScoreIterationListener(nIterationsBetweenPrints));

        //Train the network on the full data set, and evaluate it periodically
        for( int i=0; i<nEpochs; i++ ) {
            iterator.reset();
            config.fit(iterator);
        }

        printTestSet(config);
    }

    private static void printTestSet(MultiLayerNetwork config) {
        //print results of 5 test points

        double[][] inTestData=new double[][]{{0.1,0.2},{0.1,2},{2,1},{2,0.2},{5,6}};
        for (int i=0; i<inTestData.length; i++ ) {
            final INDArray in = Nd4j.create(new double[] { inTestData[i][0], inTestData[i][1] }, 1, 2);
            INDArray output = config.output(in, false);
            System.out.print(in+" gives "); System.out.println(output);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static DataSetIterator getTrainingData(int batchSize, Random rand){
        double [] sum = new double[nSamples];
        double [] input1 = new double[nSamples];
        double [] input2 = new double[nSamples];
        for (int i= 0; i< nSamples; i++) {
            input1[i] = rangeMin + (rangeMax - rangeMin) * rand.nextDouble();
            input2[i] =  rangeMin + (rangeMax - rangeMin) * rand.nextDouble();
            sum[i] = input1[i] + input2[i];
        }
        INDArray inputNDArray1 = Nd4j.create(input1, nSamples,1);
        INDArray inputNDArray2 = Nd4j.create(input2, nSamples,1);
        INDArray inputNDArray = Nd4j.hstack(inputNDArray1,inputNDArray2);
        INDArray outPut = Nd4j.create(sum, nSamples, 1);
        DataSet dataSet = new DataSet(inputNDArray, outPut);
        List<DataSet> listDs = dataSet.asList();
        Collections.shuffle(listDs,rng);
        return new ListDataSetIterator<>(listDs,batchSize);
    }
}

