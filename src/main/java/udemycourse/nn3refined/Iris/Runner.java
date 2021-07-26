package udemycourse.nn3refined.Iris;

import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.Random;

/**
 * Getting certificate error (IrisDataSetIterator) when running this class
 */

public class Runner {
    public static void main(String[] args) {

        int numInputs = 4;
        int numNeuronsHiddenLayer=5;
        int numOutputs = 3;
        double learningRate=0.01;
        int numSamples = 150;
        int batchSize = 50;
        int splitTrainNum = (int) (batchSize * .8);
        int seed = 123;
        int nofIterations=1000;
        final int iterBetweenPrintOuts=100;

        DataSetIterator iter = new IrisDataSetIterator(batchSize, numSamples);
        DataSet next = iter.next();
        next.shuffle();
        next.normalize();

        SplitTestAndTrain testAndTrain = next.splitTestAndTrain(splitTrainNum, new Random(seed));
        DataSet train = testAndTrain.getTrain();
        DataSet test = testAndTrain.getTest();

        //configuration related information (using builder pattern)
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .updater(new Sgd(learningRate))
                .seed(seed)
                .biasInit(0) // init the bias with 0 - empirical value, too
                .miniBatch(false)
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn(numInputs)
                        .nOut(numNeuronsHiddenLayer)
                        .activation(Activation.SIGMOID)
                        .weightInit(new UniformDistribution(0, 1)) // random initialize weights with values between 0 and 1
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(numNeuronsHiddenLayer)
                        .nOut(numNeuronsHiddenLayer)
                        .activation(Activation.SIGMOID)
                        .weightInit(new UniformDistribution(0, 1)) // random initialize weights with values between 0 and 1
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nIn(numNeuronsHiddenLayer)
                        .nOut(numOutputs)
                        .activation(Activation.SOFTMAX)
                        .weightInit(new UniformDistribution(0, 1))
                        .build())
                .build();

        //build and initialize the network: checks if the configuration is fine
        MultiLayerNetwork neuralNetwork = new MultiLayerNetwork(conf);
        neuralNetwork.init();

        //print the error on every iterBetweenPrintOuts iterations
        neuralNetwork.setListeners(new ScoreIterationListener(iterBetweenPrintOuts));

        //the actual learning
        long startTime = System.currentTimeMillis();  //starting time, long <=> minimum value of 0
        for (int i = 0; i < nofIterations ; i++) {
            neuralNetwork.fit(train);
        }

        //we evaluate the model on the test set
        Evaluation evaluation = new Evaluation();
        evaluation.eval(test.getLabels(), neuralNetwork.output(test.getFeatures(), Layer.TrainingMode.TEST));
        System.out.println(evaluation.stats());
        System.out.printf("Time used (s): %d\n", (System.currentTimeMillis() - startTime)/1000);

    }
}
