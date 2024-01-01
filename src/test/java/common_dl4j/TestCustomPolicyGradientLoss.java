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
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.ILossFunction;
import policy_gradient_problems.the_problems.twoArmedBandit.EnvironmentBandit;

public class TestCustomPolicyGradientLoss {

    public static final int N_INPUT = 1;
    public static final INDArray IN = Nd4j.zeros(1, N_INPUT);
    public static final double DELTA = 1e-3;


    MultiLayerNetwork net;

    @BeforeEach
    public void init() {
        net = getNet(CustomPolicyGradientLoss.newDefault());
    }

    @NotNull
    private static MultiLayerNetwork getNet(CustomPolicyGradientLoss lossFunction) {
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
        INDArray out = getOutWithGtAtIndex(1d,0);
        var probsBefore= net.output(IN).dup();
        net.fit(IN,out);
        var probsAfter= net.output(IN).dup();
        Assertions.assertTrue(probsAfter.getDouble(0)>probsBefore.getDouble(0));
        Assertions.assertTrue(probsAfter.getDouble(1)<probsBefore.getDouble(1));
    }


    @Test
    public void whenOneAtIndex1_thenCorrectNewProb() {
        INDArray out = getOutWithGtAtIndex(1d,1);
        var probsBefore= net.output(IN).dup();
        net.fit(IN,out);
        var probsAfter= net.output(IN).dup();
        Assertions.assertTrue(probsAfter.getDouble(0)<probsBefore.getDouble(0));
        Assertions.assertTrue(probsAfter.getDouble(1)>probsBefore.getDouble(1));
    }

    @NotNull
    private static INDArray getOutWithGtAtIndex(double gt, int l) {
        INDArray out = Nd4j.zeros(EnvironmentBandit.NOF_ACTIONS);
        out.putScalar(l, gt);
        return out;
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
