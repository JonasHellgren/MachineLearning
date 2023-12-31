package common_dl4j;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.layers.BaseOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.transforms.custom.SoftMax;
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
        net= MultiLayerNetworkCreator.create(NetSettings.builder()
                .nHiddenLayers(2)
                .nInput(N_INPUT).nHidden(2).nOutput(2)
                .learningRate(1e-2)
                        .lossFunction(CustomPolicyGradientLoss.newNumDefault())
                        .activOutLayer(Activation.SOFTMAX)
                .build());
    }

    @Test
    public void whenCreated_thenCorrectGrad() {

        INDArray oneHotVector = Nd4j.zeros(EnvironmentBandit.NOF_ACTIONS);
        int Gt = 1;  //10
        oneHotVector.putScalar(0, Gt);

        MultiLayerConfiguration config = net.getLayerWiseConfigurations();
        var layerConfig = config.getConf(config.getConfs().size() - 1);

        BaseOutputLayer outputLayer = (BaseOutputLayer) layerConfig.getLayer();
        ILossFunction lossFunction = outputLayer.getLossFn();

        INDArray z = Nd4j.ones(EnvironmentBandit.NOF_ACTIONS);
        IActivation af= Activation.SOFTMAX.getActivationFunction();
        var grad=lossFunction.computeGradient(oneHotVector,z,af,null);
        System.out.println("grad = " + grad);

        printProbs();

        double pa=0.5;
        Assertions.assertEquals(pa-Gt,grad.getDouble(0), DELTA);  //more of a 0
        Assertions.assertEquals(pa,grad.getDouble(1),DELTA);  //less of a 1

    }

    @Test
    public void whenCreated_thenCorrectDiffScore() {




    }


    private void printProbs() {
        System.out.println("probs = " + net.output(IN));
    }


}