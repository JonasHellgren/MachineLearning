package policy_gradient_problems.the_problems.cart_pole;

import common.ListUtils;
import common_dl4j.CustomPolicyGradientLoss;
import common_dl4j.Dl4JUtil;
import common_dl4j.MultiLayerNetworkCreator;
import common_dl4j.NetSettings;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import java.util.List;

import static common.ListUtils.arrayPrimitiveDoublesToList;

public class NeuralActorMemoryPole {
    static int NOF_INPUTS = 4, NOF_OUTPUTS = EnvironmentPole.NOF_ACTIONS;

    MultiLayerNetwork net;

    public static NeuralActorMemoryPole newDefault() {
        return new NeuralActorMemoryPole(getDefaultNetSettings());
    }

    public NeuralActorMemoryPole(NetSettings netSettings) {
        this.net= MultiLayerNetworkCreator.create(netSettings);
        net.init();
    }

    public void fit(List<Double> in, List<Double> out) {
        INDArray indArray = transformToIndArray(in);
        net.fit(indArray, Nd4j.create(out));
    }

    public List<Double> getOutValue(List<Double> inData) {
        INDArray indArray = transformToIndArray(inData);
        return arrayPrimitiveDoublesToList(net.output(indArray).toDoubleVector());
    }

    public double getError() {
        return net.gradientAndScore().getSecond();
    }

    private INDArray transformToIndArray(List<Double> in) {
        return Dl4JUtil.createOneHotAndReshape(NOF_INPUTS, in.get(0).intValue());
    }

    private static NetSettings getDefaultNetSettings() {
        return NetSettings.builder()
                .nInput(NOF_INPUTS).nHiddenLayers(1).nHidden(20).nOutput(NOF_OUTPUTS)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.SOFTMAX)
                .nofFitsPerEpoch(1).learningRate(1e-3).momentum(0.95).seed(1234)
                .lossFunction(CustomPolicyGradientLoss.newWithBeta(0.5))
                .build();
    }
}
