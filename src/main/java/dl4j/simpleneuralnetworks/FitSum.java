package dl4j.simpleneuralnetworks;


import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
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
 * Created by Anwar on 3/15/2016.
 * An example of regression neural network for performing addition
 */
@SuppressWarnings({"DuplicatedCode", "FieldCanBeLocal"})
public class FitSum {

    public static final int seed = 12345;  //Random number generator seed, for reproducability
    public static final int nEpochs = 2000;  //Number of epochs (full passes of the data)
    private static final int nSamples = 10;  //Number of data points
    public static final int batchSize = 10;  //each epoch has nSamples/batchSize parameter updates
    public static final double learningRate = 0.01; //Network learning rate
    private static int MIN_RANGE = 0;  // The range of the sample data,
    private static int MAX_RANGE = 3;
    public static final Random rng = new Random(seed);

    public static void main(String[] args){
        //Generate the training data
        DataSetIterator iterator = getTrainingData(batchSize,rng);
        //Create the network
        int numInput = 2;    int nHidden = 5;    int numOutputs = 1;
        MultiLayerNetwork net = new MultiLayerNetwork(new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(learningRate, 0.9))
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInput).nOut(nHidden)
                        .activation(Activation.RELU) //Can be RELU
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(nHidden).nOut(numOutputs).build())
                .build()
        );
        net.init();         net.setListeners(new ScoreIterationListener(100));

        //Train the network on the full data set, and evaluate in periodically
        for( int i=0; i<nEpochs; i++ ){        iterator.reset();            net.fit(iterator);        }

        //print results of 5 test points
        final int ntp=5;
        double[][] inmat=new double[][]{{0.1,0.2},{0.1,2},{2,1},{2,0.2},{5,6}};
        for (int i=0; i<ntp; i++ ) {
            final INDArray in = Nd4j.create(new double[] { inmat[i][0], inmat[i][1] }, 1, 2);
            INDArray output = net.output(in, false);
            System.out.print(in+" gives "); System.out.println(output);
        }
    }

    @SuppressWarnings("SameParameterValue")
    public static DataSetIterator getTrainingData(int batchSize, Random rand){
        double [] sum = new double[nSamples];
        double [] input1 = new double[nSamples];
        double [] input2 = new double[nSamples];
        for (int i= 0; i< nSamples; i++) {
            input1[i] = MIN_RANGE + (MAX_RANGE - MIN_RANGE) * rand.nextDouble();
            input2[i] =  MIN_RANGE + (MAX_RANGE - MIN_RANGE) * rand.nextDouble();
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

