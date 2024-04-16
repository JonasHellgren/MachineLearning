package super_vised.dl4j.regression_2023;

import common.dl4j.MultiLayerNetworkCreator;
import common.dl4j.NetSettings;
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/***
 * Suppose a dataset with 100 samples and batch size of 10. The number of mini-batches per epoch would be 10.
 *
 *
 */

public class UsingDataSetIterator {

    public static final int PRINT_ITERATIONS = 100;
    public static final int SEED = 1234;

    public static void main(String[] args) {
        int nSamples = 100; // Number of data points
        int batchSize = 20; // Batch size for training
        int nEpochs = 5;

        List<DataSet> dataSetList = getDataSetList(nSamples);
        MultiLayerNetwork net = getNetwork();
        trainShuffle(dataSetList, net, nEpochs,batchSize);
        evaluation(net);
    }

    //The getRow method with the argument true ensures that the extracted row is a 2D array with shape [[]]
    // instead of collapsing it into a 1D array with shape [].
    private static List<DataSet> getDataSetList(int nSamples) {
        INDArray features = Nd4j.randn(nSamples).mul(1).reshape(nSamples, 1); // Random values between -5 and 5
        INDArray labels = features.mul(2).addi(3).mul(0.5); // y = 2x + 3

        List<DataSet> dataSetList = new ArrayList<>();
        for (int i = 0; i < nSamples; i++) {
            INDArray featureRow = features.getRow(i, true);
            INDArray labelRow = labels.getRow(i, true);
            dataSetList.add(new DataSet(featureRow, labelRow));
        }
        return dataSetList;
    }


    private static MultiLayerNetwork getNetwork() {
        NetSettings netSettings = NetSettings.builder()
                .nHiddenLayers(1).nInput(1).nHidden(2).nOutput(1)
                .weightInit(WeightInit.RELU_UNIFORM)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.IDENTITY)
                .l2Value(1e-2).learningRate(1e-3).momentum(0.9)
                .lossFunction(LossFunctions.LossFunction.MSE.getILossFunction())
                .build();
        return MultiLayerNetworkCreator.create(netSettings);
    }

    /**
     * Collections.shuffle shuffles the entire list of DataSet objects before creating the ListDataSetIterator.
     * Each epoch will now process mini-batches that are randomly shuffled.
     * Nof batches per epoch is described in top of file.
     *
     * Shuffling the entire dataset is a common and recommended practice in training neural networks,
     * shuffling within a mini-batch is less common and typically used in specific scenarios
     */

    private static void trainShuffle(List<DataSet> dataSetList, MultiLayerNetwork net, int nEpochs,int batchSize) {
        ListDataSetIterator<DataSet> iterator = new ListDataSetIterator<>(dataSetList, batchSize);
        net.setListeners(new ScoreIterationListener(PRINT_ITERATIONS));
        Collections.shuffle(dataSetList, new Random(SEED));
        for (int epoch = 0; epoch < nEpochs; epoch++) {
            int nofBatches=0;
            while (iterator.hasNext()) {
                DataSet miniBatch = iterator.next();
                //miniBatch.shuffle();  //optional
                System.out.println("miniBatch = " + miniBatch);
                net.fit(miniBatch); // Fit the model on each mini-batch
                nofBatches++;
            }
            System.out.println("epoch = " + epoch+", nofBatches = " + nofBatches);
            iterator.reset(); // Reset iterator for the next epoch
        }
    }

    private static void trainNoShuffle(int batchSize, List<DataSet> dataSetList, MultiLayerNetwork net, int nEpochs) {
        ListDataSetIterator<DataSet> iterator = new ListDataSetIterator<>(dataSetList, batchSize);
        net.setListeners(new ScoreIterationListener(100));
        for (int i = 0; i < nEpochs; i++) {
            iterator.reset();
            net.fit(iterator);
        }
    }


    private static void evaluation(MultiLayerNetwork net) {
        System.out.println("Training complete");
        int nTestSamples = 10; // Number of test data points
        INDArray testFeatures = Nd4j.randn(nTestSamples).reshape(nTestSamples, 1).mul(1);
        INDArray actualLabels = testFeatures.mul(2).addi(3).mul(0.5); // y = 2x + 3
        INDArray predictions = net.output(testFeatures);
        System.out.println("actualLabels = " + actualLabels);
        var errors = predictions.sub(actualLabels);
        System.out.println("errors = " + errors);
    }

}
