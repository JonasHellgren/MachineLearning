
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.PerformanceListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.nativeblas.NativeOpsHolder;
import regressionnetworks.function.MathFunction;
import regressionnetworks.function.SinXDivXMathFunction;


import java.util.Collections;
import java.util.List;
import java.util.Random;


public class TestSinDivX {

    //Random number generator seed, for reproducability
    public static final int seed = 12345;
    //Number of epochs (full passes of the data)
    public static final int nEpochs = 300;
    //How frequently should we plot the network output?
    private static final int plotFrequency = 250;
    //Number of data points
    private static final int nSamples = 500;
    //Batch size: i.e., each iteration has nSamples/batchSize parameter updates
    public static final int batchSize = 100;
    //Network learning rate
    public static final double learningRate = 0.01;
    public static final Random rng = new Random(seed);
    public static final int numInputs = 1;
    private static final int numOutputs = 1;


    @Test
    public void Test1() {
        //Switch these two options to do different functions with different networks
        final MathFunction fn = new SinXDivXMathFunction();
        final MultiLayerConfiguration conf = getDeepDenseLayerNetworkConfiguration();

        //Generate the training data
        final INDArray x = Nd4j.linspace(-10, 10, nSamples).reshape(nSamples, 1);
        final DataSetIterator iterator = getTrainingData(x, fn, batchSize, rng);

        //Create the network
        final MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();

        Nd4j.getMemoryManager().setAutoGcWindow(10000);             //Set to 10 seconds (10000ms) between System.gc() calls
        Nd4j.getMemoryManager().togglePeriodicGc(false);            //Disable periodic GC calls
        int listenerFrequency = 100;
        net.setListeners(new ScoreIterationListener(listenerFrequency));
        //net.setListeners(new PerformanceListener(listenerFrequency));

        boolean reportScore = true;
        boolean reportGC = true;

        net.setListeners(new PerformanceListener(listenerFrequency, reportScore, reportGC));

        System.out.println("ND4J Data Type Setting: " + Nd4j.dataType());
        Nd4j.create(1);    //Initialize ND4J before calling device native ops methods
        System.out.println("Optimal requirements met: " + NativeOpsHolder.getInstance().getDeviceNativeOps().isOptimalRequirementsMet());
        System.out.println("Binary level: " + NativeOpsHolder.getInstance().getDeviceNativeOps().binaryLevel());
        System.out.println("Optimal level: " + NativeOpsHolder.getInstance().getDeviceNativeOps().optimalLevel());

        //Train the network on the full data set, and evaluate in periodically
        final INDArray[] networkPredictions = new INDArray[nEpochs / plotFrequency];
        for (int i = 0; i < nEpochs; i++) {
            iterator.reset();
            net.fit(iterator);
            if ((i + 1) % plotFrequency == 0) networkPredictions[i / plotFrequency] = net.output(x, false);
        }
    }


    private static MultiLayerConfiguration getDeepDenseLayerNetworkConfiguration() {
        final int numHiddenNodes = 50;
        return new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(learningRate, 0.9))
                .list()
                .layer(new DenseLayer.Builder().nIn(numInputs).nOut(numHiddenNodes)
                        .activation(Activation.TANH).build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.TANH).build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(numHiddenNodes).nOut(numOutputs).build())
                .build();
    }

    /**
     * Create a DataSetIterator for training
     *
     * @param x         X values
     * @param function  Function to evaluate
     * @param batchSize Batch size (number of quickstartexamples for every call of DataSetIterator.next())
     * @param rng       Random number generator (for repeatability)
     */
    @SuppressWarnings("SameParameterValue")
    private static DataSetIterator getTrainingData(final INDArray x, final MathFunction function, final int batchSize, final Random rng) {
        final INDArray y = function.getFunctionValues(x);
        final DataSet allData = new DataSet(x, y);

        final List<DataSet> list = allData.asList();
        Collections.shuffle(list, rng);
        return new ListDataSetIterator<>(list, batchSize);
    }
}