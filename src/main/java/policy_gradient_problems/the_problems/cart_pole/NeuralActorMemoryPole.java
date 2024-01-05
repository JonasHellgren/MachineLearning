package policy_gradient_problems.the_problems.cart_pole;

import common.ListUtils;
import common_dl4j.CustomPolicyGradientLoss;
import common_dl4j.Dl4JUtil;
import common_dl4j.MultiLayerNetworkCreator;
import common_dl4j.NetSettings;
import org.apache.commons.math3.util.Pair;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.jetbrains.annotations.NotNull;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import java.util.List;

import static common.ListUtils.arrayPrimitiveDoublesToList;

public class NeuralActorMemoryPole {
    static int NOF_INPUTS = StatePole.newUprightAndStill().asList().size(), NOF_OUTPUTS = EnvironmentPole.NOF_ACTIONS;

    MultiLayerNetwork net;
    NormalizerMinMaxScaler normalizerIn;

    public static NeuralActorMemoryPole newDefault(ParametersPole parametersPole) {
        return new NeuralActorMemoryPole(getDefaultNetSettings(),parametersPole);
    }

    public NeuralActorMemoryPole(NetSettings netSettings, ParametersPole parametersPole) {
        this.net= MultiLayerNetworkCreator.create(netSettings);
        this.normalizerIn = createNormalizerIn(parametersPole);
        net.init();
    }

    public void fit(List<Double> in, List<Double> out) {
        net.fit(getAsNormalizedIndArray(in), Nd4j.create(out));
    }
    public List<Double> getOutValue(List<Double> in) {
        return arrayPrimitiveDoublesToList(net.output(getAsNormalizedIndArray(in)).toDoubleVector());
    }

    public double getError() {
        return net.gradientAndScore().getSecond();
    }

    private INDArray getAsNormalizedIndArray(List<Double> in) {
        INDArray indArray = Nd4j.create(in);
        normalizerIn.transform(indArray);
        indArray=indArray.reshape(1,NOF_INPUTS);
        return indArray;
    }

    private static NetSettings getDefaultNetSettings() {
        return NetSettings.builder()
                .nInput(NOF_INPUTS).nHiddenLayers(1).nHidden(20).nOutput(NOF_OUTPUTS)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.SOFTMAX)
                .nofFitsPerEpoch(1).learningRate(1e-3).momentum(0.95).seed(1234)
                .lossFunction(CustomPolicyGradientLoss.newWithBeta(0.5))
                .build();
    }

    private static NormalizerMinMaxScaler createNormalizerIn(ParametersPole p) {
        List<Pair<Double, Double>> inMinMax = p.minMaxStatePairList();
        return Dl4JUtil.createNormalizer(inMinMax, Pair.create(-1d,1d));  //0,1 gives worse performance
    }

}
