package common_dl4j;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.layers.BaseOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.ILossFunction;
import policy_gradient_problems.the_problems.twoArmedBandit.EnvironmentBandit;

import java.util.List;

public class TestCustomPolicyGradientLoss {

    public static final int N_INPUT = 1;
    public static final INDArray IN = Nd4j.zeros(1, N_INPUT);
    public static final double DELTA = 1e-3;

    MultiLayerNetwork net;

    @BeforeEach
    public void init() {
        net = getNet(CustomPolicyGradientLossNew.newDefault());
    }

    @NotNull
    private static MultiLayerNetwork getNet(ILossFunction lossFunction) {
        return MultiLayerNetworkCreator.create(NetSettings.builder()
                .nHiddenLayers(2)
                .nInput(N_INPUT).nHidden(2).nOutput(2)
                .learningRate(1e-2)
                .lossFunction(lossFunction)
                .activOutLayer(Activation.SOFTMAX)
                .build());
    }

    @Test
    @Ignore
    public void whenCreated_thenCorrectGrad() {
        INDArray out = getOutWithGtAtIndex(1d,0);
        INDArray gradNum = getGradient(out, net);
        Assertions.assertNotEquals(Math.signum(gradNum.getDouble(0)),Math.signum(gradNum.getDouble(1)));
    }

    @Test
    public void whenOneAtIndex0_thenCorrectNewProb() {
        INDArray out = getOutWithGtAtIndexNew(1d,0);
        var pBef= net.output(IN).dup();

        System.out.println("IN.shapeInfoToString() = " + IN.shapeInfoToString());
        System.out.println("out.shapeInfoToString() = " + out.shapeInfoToString());

        net.fit(IN,out);
        var pAfter= net.output(IN).dup();

        System.out.println("pBef = " + pBef+", pAfter = " + pAfter);


        Assertions.assertTrue(getP(pAfter, 0) > getP(pBef, 0));
        Assertions.assertTrue(getP(pAfter, 1) < getP(pBef, 1));
    }

    @Test
    public void whenOneAtIndex1_thenCorrectNewProb() {
        INDArray out = getOutWithGtAtIndex(1d,1);
        var pBef= net.output(IN).dup();
        net.fit(IN,out);
        var pAfter= net.output(IN).dup();

        Assertions.assertTrue(getP(pAfter, 0) < getP(pBef, 0));
        Assertions.assertTrue(getP(pAfter, 1) > getP(pBef, 1));
    }

    @Test
    public void whenMinusOneAtIndex0_thenCorrectNewProb() {
        INDArray out = getOutWithGtAtIndex(-1d,0);
        var pBef= net.output(IN).dup();
        net.fit(IN,out);
        var pAfter= net.output(IN).dup();
        Assertions.assertTrue(getP(pAfter, 0) < getP(pBef, 0));
        Assertions.assertTrue(getP(pAfter, 1) > getP(pBef, 1));
    }

    @NotNull
    private static INDArray getOutWithGtAtIndexNew(double gt, int l) {
/*
        INDArray out = Nd4j.zeros(EnvironmentBandit.NOF_ACTIONS);  //FEL todo
        out.putScalar(l, gt);
*/
        int nofActions = EnvironmentBandit.NOF_ACTIONS;
        List<Double> oneHot=Dl4JUtil.createListWithOneHotWithValue(nofActions,l,gt);
        return Dl4JUtil.convertListOfLists(List.of(oneHot), nofActions).castTo(DataType.FLOAT);
    }

    @NotNull
    private static INDArray getOutWithGtAtIndex(double gt, int l) {
        INDArray out = Nd4j.zeros(EnvironmentBandit.NOF_ACTIONS);  //FEL todo
        out.putScalar(l, gt);
        return out;
    }



    private static double getP(INDArray probsAfter, int action) {
        return probsAfter.getDouble(action);
    }

    private INDArray getGradient(INDArray out, MultiLayerNetwork netNotNum1) {
        MultiLayerConfiguration config = netNotNum1.getLayerWiseConfigurations();
        var layerConfig = config.getConf(config.getConfs().size() - 1);
        BaseOutputLayer outputLayer = (BaseOutputLayer) layerConfig.getLayer();
        ILossFunction lossFunction = outputLayer.getLossFn();
        INDArray z = Nd4j.ones(EnvironmentBandit.NOF_ACTIONS);
        IActivation af= Activation.SOFTMAX.getActivationFunction();
        return lossFunction.computeGradient(out,z,af,null);
    }



}
