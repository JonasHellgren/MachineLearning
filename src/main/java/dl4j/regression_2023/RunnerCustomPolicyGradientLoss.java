package dl4j.regression_2023;

import common_dl4j.CustomPolicyGradientLoss;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.jetbrains.annotations.NotNull;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;

/**
 * In the simple example below prob for retaking action 1 is favorized (given highest advantage)
 */

public class RunnerCustomPolicyGradientLoss {

    static final int seed = 12345;
    static final int nEpochs = 50;
    static final double learningRate = 0.001;
    static final double momentum = 0.95;

    static final int numInput = 4;
    static final int numOutputs = 2;
    static final int nHidden = 4;
    public static final int PRINT_ITERATIONS = 10;


    public static void main(String[] args) {
        float advAction0 = 0,advAction1 = 10;
        var net = createNetwork();

        INDArray inEx = Nd4j.rand(1, numInput);
        net.output(inEx);
        System.out.println("inEx = " + inEx);
        printNetOut(net, inEx);

        for (int i = 0; i < nEpochs; i++) {
            INDArray in = Nd4j.rand(1, numInput);
            INDArray out = Nd4j.create(new float[]{advAction0, advAction1});
            net.fit(in, out);
        }

        printNetOut(net, inEx); //shall give high prob for second output

    }

    private static void printNetOut(MultiLayerNetwork net, INDArray inEx) {
        System.out.println("net.output(inEx) = " + net.output(inEx));
    }

    @NotNull
    private static MultiLayerNetwork createNetwork() {

        MultiLayerNetwork net = new MultiLayerNetwork(new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(learningRate, momentum))
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInput).nOut(nHidden)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new OutputLayer.Builder(CustomPolicyGradientLoss.newDefault())
                        .activation(Activation.SOFTMAX)
                        .nIn(nHidden).nOut(numOutputs).build())
                .build()
        );
        net.init();
        net.setListeners(new ScoreIterationListener(PRINT_ITERATIONS));
        return net;
    }

}
