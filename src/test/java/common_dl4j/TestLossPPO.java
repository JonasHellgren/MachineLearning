package common_dl4j;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.layers.BaseOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.ILossFunction;

import java.util.List;

public class TestLossPPO {

    public static final int N_INPUT = 1;
    public static final INDArray IN = Nd4j.zeros(1, N_INPUT);
    public static final double DELTA = 1e-3;
    public static final int N_OUTPUT = 2;
    public static final List<List<Double>> NET_INPUT_GIVING_ZERO_DOT_FIVE_PROB_BOTH_ACTIONS = List.of(List.of(0d, 0d));

    MultiLayerNetwork net;

    @BeforeEach
    public void init() {
        net = getNet(LossPPO.newDefault());
    }

    @NotNull
    private static MultiLayerNetwork getNet(ILossFunction lossFunction) {
        return MultiLayerNetworkCreator.create(NetSettings.builder()
                .nHiddenLayers(2)
                .nInput(N_INPUT).nHidden(2).nOutput(N_OUTPUT)
                .learningRate(1e-2)
                .lossFunction(lossFunction)
                .activOutLayer(Activation.SOFTMAX)
                .build());
    }

    /**
     * Aim is to minimize score => smallest of scoreDecreasedProb and scoreIncreasedProb is best
     */

    @Test
    void givenPositiveAdvantage_thenLowerScoreForProbIncreases() {
        double probNet = 0.5;
        double advantage = 1d;
        double scoreDecreasedProb=calcScore(probNet+0.1, advantage);  //probNew<probOld
        double scoreIncreasedProb=calcScore(probNet-0.1, advantage);
        System.out.println("scoreDecreasedProb = " + scoreDecreasedProb);
        System.out.println("scoreIncreasedProb = " + scoreIncreasedProb);
        Assertions.assertTrue(scoreIncreasedProb<scoreDecreasedProb);
    }

    @Test
    void givenNegativeAdvantage_thenLowerScoreForProbDecreases() {
        double probNet = 0.5;
        double advantage = -1d;
        double scoreDecreasedProb=calcScore(probNet+0.1, advantage);
        double scoreIncreasedProb=calcScore(probNet-0.1, advantage);
        System.out.println("scoreDecreasedProb = " + scoreDecreasedProb);
        System.out.println("scoreIncreasedProb = " + scoreIncreasedProb);
        Assertions.assertTrue(scoreIncreasedProb>scoreDecreasedProb);
    }

    /**
     * Gradient descent means to update the model's parameters in the opposite direction of the gradient of the loss
     * function with respect to those parameters.
     * If  the gradient with respect to the outputs is  [1,−1],
     * this indicates that the network's parameters should be updated in a way that will increase
     * the probability of action 1 and decrease the probability of action 0.
     */

    @Test
    void givenPositiveAdvantage_thenCorrectGradientIfProbIncreases() {
        double probNet = 0.5;
        double advantage = 1d;
        INDArray grad=calcGradient(probNet+0.1, advantage);
        Assertions.assertTrue(grad.getDouble(0)>grad.getDouble(1));  //change net to increase prob action 1
    }

    @Test
    void givenNegativeAdvantage_thenCorrectGradientIfProbIncreases() {
        double probNet = 0.5;
        double advantage = -1d;
        INDArray grad=calcGradient(probNet+0.1, advantage);
        Assertions.assertTrue(grad.getDouble(0)<grad.getDouble(1));  //change net to increase prob action 0
    }


    private double calcScore(double newProb1, double advantage) {
        INDArray z = Dl4JUtil.convertListOfLists(NET_INPUT_GIVING_ZERO_DOT_FIVE_PROB_BOTH_ACTIONS).castTo(DataType.FLOAT);
        double action = 1d;
        var listOfListsOut = List.of(List.of(action, advantage, newProb1));
        INDArray out = Dl4JUtil.convertListOfLists(listOfListsOut).castTo(DataType.FLOAT);
        IActivation af= Activation.SOFTMAX.getActivationFunction();
        return getLossFunction(net).computeScore(out,z,af,null,true);
    }

    private INDArray calcGradient(double probOld, double advantage) {
        INDArray z = Dl4JUtil.convertListOfLists(NET_INPUT_GIVING_ZERO_DOT_FIVE_PROB_BOTH_ACTIONS).castTo(DataType.FLOAT);
        double action = 1d;
        var listOfListsOut = List.of(List.of(action, advantage, probOld));
        INDArray out = Dl4JUtil.convertListOfLists(listOfListsOut).castTo(DataType.FLOAT);
        IActivation af= Activation.SOFTMAX.getActivationFunction();
        return getLossFunction(net).computeGradient(out,z,af,null);
    }



    private static ILossFunction getLossFunction(MultiLayerNetwork net) {
        MultiLayerConfiguration config = net.getLayerWiseConfigurations();
        var layerConfig = config.getConf(config.getConfs().size() - 1);
        BaseOutputLayer outputLayer = (BaseOutputLayer) layerConfig.getLayer();
        return outputLayer.getLossFn();
    }

}
