package regressionnetworks;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer.Builder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class XOR_FitByRegression {
    private static final int SEED = 234;
    private static final int NOF_ITERATIONS = 1000;
    private static final int NOF_ITERATIONS_BETWEENPRINTS = 100;
    private static final double LEARNING_RATE = 0.5;
    private static final int NOF_INPUT_NEURONS = 2;
    private static final int NOF_HIDDEN_NEURONS = 4;
    private static final int NOF_OUTPUT_NEURONS = 1;

    public static void main(String[] args) {
        INDArray input = Nd4j.zeros(4, 2);
        INDArray labels = Nd4j.zeros(4, 1);
        //new int[] { rowIdx, colIdx }, value}
        input.putScalar(new int[] { 0, 0 }, 0);     input.putScalar(new int[] { 0, 1 }, 0);
        labels.putScalar(new int[] { 0, 0 }, 0);  // in [0,0] -> out [0]
        input.putScalar(new int[] { 1, 0 }, 1);     input.putScalar(new int[] { 1, 1 }, 0);
        labels.putScalar(new int[] { 1, 0 }, 1);  // in [1,0] -> out [1]
        input.putScalar(new int[] { 2, 0 }, 0);     input.putScalar(new int[] { 2, 1 }, 1);
        labels.putScalar(new int[] { 2, 0 }, 1);    // in [0,1] -> out [1]
        input.putScalar(new int[] { 3, 0 }, 1);       input.putScalar(new int[] { 3, 1 }, 1);
        labels.putScalar(new int[] { 3, 0 }, 0);   // in [1,1] -> out [0]

        DataSet ds = new DataSet(input, labels);

        NeuralNetConfiguration.Builder config = new NeuralNetConfiguration.Builder();
        config.seed(SEED);
        config.biasInit(0);
        config.miniBatch(false);
        config.updater(new Sgd(LEARNING_RATE));
        ListBuilder listBuilder = config.list();

        // Hidden Layer
        DenseLayer.Builder hiddenLayerBuilder = new DenseLayer.Builder();
        hiddenLayerBuilder.nIn(NOF_INPUT_NEURONS);
        hiddenLayerBuilder.nOut(NOF_HIDDEN_NEURONS);
        hiddenLayerBuilder.activation(Activation.SIGMOID);
        hiddenLayerBuilder.weightInit(new UniformDistribution(0, 1));
        listBuilder.layer(0, hiddenLayerBuilder.build());

        // Output Layer
        Builder outputLayerBuilder = new OutputLayer.Builder(LossFunctions.LossFunction.SQUARED_LOSS);
        outputLayerBuilder.nIn(NOF_HIDDEN_NEURONS);
        outputLayerBuilder.nOut(NOF_OUTPUT_NEURONS);
        outputLayerBuilder.activation(Activation.SIGMOID);
        outputLayerBuilder.weightInit(new UniformDistribution(0, 1));
        listBuilder.layer(1, outputLayerBuilder.build());
        MultiLayerConfiguration conf = listBuilder.build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();

        net.setListeners(new ScoreIterationListener(NOF_ITERATIONS_BETWEENPRINTS));
        System.out.println(net.output(input));

        net.fit(ds);

        // here the actual learning takes place
        for(int i = 0; i < NOF_ITERATIONS; i++ ) {      net.fit(ds);      }

        System.out.println("Input"); System.out.println(input);
        System.out.println("labels"); System.out.println(labels);
        System.out.println("NN output"); System.out.println(net.output(ds.getFeatures()));

        // let Evaluation prints stats how often the right output had the highest value
        Evaluation eval = new Evaluation();
        INDArray output = net.output(ds.getFeatures());
        eval.eval(ds.getLabels(), output);
        System.out.println(eval.stats());
    }
}