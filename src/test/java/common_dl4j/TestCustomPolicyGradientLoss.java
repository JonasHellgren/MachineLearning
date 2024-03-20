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
import policy_gradient_problems.environments.twoArmedBandit.EnvironmentBandit;

import java.util.List;

public class TestCustomPolicyGradientLoss {

    public static final int N_INPUT = 1;
    public static final INDArray IN = Nd4j.zeros(1, N_INPUT);
    public static final double DELTA = 1e-3;
    public static final int N_OUTPUT = 2;

    MultiLayerNetwork net;

    @BeforeEach
    public void init() {
        net = getNet(CustomPolicyGradientLoss.newDefault());
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

    @Test
    public void whenCreated_thenCorrectGrad() {
        List<List<Double>> listOfListsZ = List.of(List.of(0d, 0d));
        INDArray z = Dl4JUtil.convertListOfLists(listOfListsZ).castTo(DataType.FLOAT);
        List<List<Double>> listOfListsOut = List.of(List.of(1d, 0d));
        INDArray out = Dl4JUtil.convertListOfLists(listOfListsOut).castTo(DataType.FLOAT);

        ILossFunction lossFunction = getLossFunction(net);
        IActivation af= Activation.SOFTMAX.getActivationFunction();
        INDArray grad=lossFunction.computeGradient(out,z,af,null);
        System.out.println("grad = " + grad);
        Assertions.assertNotEquals(Math.signum(grad.getDouble(0)),Math.signum(grad.getDouble(1)));
    }

    @Test
    public void whenCreatedManyPoints_thenCorrectGradAnScoreArr() {

        List<List<Double>> listOfListsZ = List.of(List.of(0d, 0d),List.of(0d, 0d));
        INDArray z = Dl4JUtil.convertListOfLists(listOfListsZ).castTo(DataType.FLOAT);
        List<List<Double>> listOfListsOut = List.of(List.of(1d, 0d),List.of(0d, 0.0d));
        INDArray out = Dl4JUtil.convertListOfLists(listOfListsOut).castTo(DataType.FLOAT);

        ILossFunction lossFunction = getLossFunction(net);
        IActivation af= Activation.SOFTMAX.getActivationFunction();

        INDArray grad=lossFunction.computeGradient(out,z,af,null);
        INDArray scoreArr=lossFunction.computeScoreArray(out,z,af,null);
        double score=lossFunction.computeScore(out,z,af,null,false);
        Assertions.assertNotEquals(grad.getRow(0),grad.getRow(1));
        Assertions.assertNotEquals(scoreArr.getRow(0),scoreArr.getRow(1));
        Assertions.assertEquals(score,scoreArr.sumNumber().doubleValue());

    }

    @Test
    public void whenOneAtIndex0_thenCorrectNewProb() {
        INDArray out = getOutWithGtAtIndexNew(1d,0);
        var pBef= net.output(IN).dup();
        net.fit(IN,out);
        var pAfter= net.output(IN).dup();
        Assertions.assertTrue(getP(pAfter, 0) > getP(pBef, 0));
        Assertions.assertTrue(getP(pAfter, 1) < getP(pBef, 1));
    }

    @Test
    public void whenOneAtIndex1_thenCorrectNewProb() {
        INDArray out = getOutWithGtAtIndexNew(1d,1);
        var pBef= net.output(IN).dup();
        net.fit(IN,out);
        var pAfter= net.output(IN).dup();

        Assertions.assertTrue(getP(pAfter, 0) < getP(pBef, 0));
        Assertions.assertTrue(getP(pAfter, 1) > getP(pBef, 1));
    }

    @Test
    public void whenMinusOneAtIndex0_thenCorrectNewProb() {
        INDArray out = getOutWithGtAtIndexNew(-1d,0);
        var pBef= net.output(IN).dup();
        net.fit(IN,out);
        var pAfter= net.output(IN).dup();
        Assertions.assertTrue(getP(pAfter, 0) < getP(pBef, 0));
        Assertions.assertTrue(getP(pAfter, 1) > getP(pBef, 1));
    }

    @NotNull
    private static INDArray getOutWithGtAtIndexNew(double gt, int l) {
        int nofActions = EnvironmentBandit.NOF_ACTIONS;
        List<Double> oneHot=Dl4JUtil.createListWithOneHotWithValue(nofActions,l,gt);
        return Dl4JUtil.convertListOfLists(List.of(oneHot)).castTo(DataType.FLOAT);
    }

    private static double getP(INDArray probsAfter, int action) {
        return probsAfter.getDouble(action);
    }


    private static ILossFunction getLossFunction(MultiLayerNetwork netNotNum1) {
        MultiLayerConfiguration config = netNotNum1.getLayerWiseConfigurations();
        var layerConfig = config.getConf(config.getConfs().size() - 1);
        BaseOutputLayer outputLayer = (BaseOutputLayer) layerConfig.getLayer();
        return outputLayer.getLossFn();
    }


}
