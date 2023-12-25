package policy_gradient_problems.the_problems.twoArmedBandit;

import common_dl4j.CustomPolicyGradientLoss;
import common.ListUtils;
import common.RandUtils;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
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
import policy_gradient_problems.abstract_classes.AgentInterface;

import java.util.List;

import static common.IndexFinder.findBucket;
import static policy_gradient_problems.common.BucketLimitsHandler.getLimits;
import static policy_gradient_problems.common.BucketLimitsHandler.throwIfBadLimits;

public class AgentBanditNeural implements AgentInterface  {

    static final int seed = 12345;
    static final double momentum = 0.95;
    static final int numInput = 1;
    public static final INDArray IN = Nd4j.zeros(1, numInput);
    static final int numOutputs = 2;
    static final int nHidden = 4;

    MultiLayerNetwork network;

    public static AgentBanditNeural newDefault(double learningRate) {
        return new AgentBanditNeural(learningRate);
    }

    public AgentBanditNeural(double learningRate) {
        System.out.println("agent created");
        this.network=createNetwork(learningRate);
    }

    @Override
    public int chooseAction() {
        var limits = getLimits(getActionProbabilities());
        throwIfBadLimits(limits);
        return findBucket(ListUtils.toArray(limits), RandUtils.randomNumberBetweenZeroAndOne());
    }

    public List<Double> getActionProbabilities() {
        return ListUtils.arrayPrimitiveDoublesToList(network.output(IN).toDoubleVector());
    }

    public void fit(INDArray out) {
        network.fit(IN,out);
    }

    @NotNull
    private static MultiLayerNetwork createNetwork(double learningRate1) {

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(learningRate1, momentum))
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInput).nOut(nHidden)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new OutputLayer.Builder(new CustomPolicyGradientLoss())
                        .activation(Activation.SOFTMAX)
                        .nIn(nHidden).nOut(numOutputs).build())
                .build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(Integer.MAX_VALUE));
        return net;
    }

}
