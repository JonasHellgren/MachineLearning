package udemycourse.nn3refined.XOR;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class Runner {
    public static void main(String[] args) {

        int seed = 1234;        // number used to initialize a pseudorandom number generator.
        int nofIterations=1000;
        final int iterBetweenPrintOuts=100;
        double learningRate=0.5;

        //the input has 2 dimensions: x and y (both logical values so 0 or 1
        //it is like a matrix: we have to define rowIndex and colIndex accordingly
        INDArray input = Nd4j.zeros(4, 2);
        INDArray labels = Nd4j.zeros(4, 2);

        //two first inputs/labels:  (in1,in2)=(0,0) -> (class0,class1)=(false,true)=(1,0)

        //have to define the x and y values one by one (by indexes)
        input.putScalar(new int[]{0, 0}, 0);     // at (0,0) put 0
        input.putScalar(new int[]{0, 1}, 0);
        input.putScalar(new int[]{1, 0}, 1);
        input.putScalar(new int[]{1, 1}, 0);
        input.putScalar(new int[]{2, 0}, 0);
        input.putScalar(new int[]{2, 1}, 1);
        input.putScalar(new int[]{3, 0}, 1);
        input.putScalar(new int[]{3, 1}, 1);

        //we have two classes logical 0 (1 0) and logical 1 (0 1)
        //two lines --> represent one class !!!
        labels.putScalar(new int[]{0, 0}, 1);  // at (0,0) put 1 class 0 (false)
        labels.putScalar(new int[]{0, 1}, 0);
        labels.putScalar(new int[]{1, 0}, 0);
        labels.putScalar(new int[]{1, 1}, 1);
        labels.putScalar(new int[]{2, 0}, 0);
        labels.putScalar(new int[]{2, 1}, 1);
        labels.putScalar(new int[]{3, 0}, 1);
        labels.putScalar(new int[]{3, 1}, 0);

        //the dataset is the combination of inputs and labels
        DataSet dataset = new DataSet(input, labels);

        //configuration related information (using builder pattern)
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .updater(new Sgd(learningRate))
                .seed(seed)
                .biasInit(0) // init the bias with 0 - empirical value, too
                .miniBatch(false)
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn(2)
                        .nOut(4)
                        .activation(Activation.SIGMOID)
                        .weightInit(new UniformDistribution(0, 1)) // random initialize weights with values between 0 and 1
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(2)
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
            neuralNetwork.fit(dataset);
        }

        //create output for every training sample
        INDArray output = neuralNetwork.output(dataset.getFeatures());

        //prints statistics + how often the right output had the highest value + 2 is the number of classes in the output
        Evaluation eval = new Evaluation();
        eval.eval(dataset.getLabels(), output);
        System.out.println(eval.stats());

        //we can use the neural network to make new predictions
        System.out.println("NEW PREDICTION");
        System.out.println(neuralNetwork.output(input));
        System.out.printf("Time used (s): %d\n", (System.currentTimeMillis() - startTime)/1000);

    }
}
