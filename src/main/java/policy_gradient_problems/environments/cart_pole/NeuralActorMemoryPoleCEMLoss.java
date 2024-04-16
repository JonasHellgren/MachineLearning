package policy_gradient_problems.environments.cart_pole;

import common.dl4j.*;
import org.apache.commons.math3.util.Pair;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import java.util.List;

import static common.list_arrays.ListUtils.arrayPrimitiveDoublesToList;
import static policy_gradient_problems.environments.cart_pole.InNetNormalizer.createNormalizerIn;
import static policy_gradient_problems.environments.cart_pole.InNetNormalizer.getInAsNormalized;

/**
 * The out value is probabilities in [0,1], hence shall not be reverted in getOutValue
 * But normalization is needed in the fit method
 */

public class NeuralActorMemoryPoleCEMLoss {
    public static final double NOMINAL_ADVANTAGE = 10d;
    static int N_INPUTS = StatePole.newUprightAndStill(ParametersPole.newDefault()).nofStates();
    static int N_OUTPUTS = EnvironmentPole.NOF_ACTIONS;

    MultiLayerNetwork net;
    NormalizerMinMaxScaler normalizerIn, normalizerOut;
    Dl4JNetFitter fitter;

    public static NeuralActorMemoryPoleCEMLoss newDefault(ParametersPole parametersPole) {
        return new NeuralActorMemoryPoleCEMLoss(getDefaultNetSettings(), parametersPole);
    }

    public NeuralActorMemoryPoleCEMLoss(NetSettings netSettings, ParametersPole parametersPole) {
        this.net = MultiLayerNetworkCreator.create(netSettings);
        this.normalizerIn = createNormalizerIn(parametersPole);
        this.normalizerOut = createNormalizerOut();
        net.init();
        this.fitter = new Dl4JNetFitter(net, netSettings);
    }

    public void fit(List<List<Double>> in, List<List<Double>> outList) {
        INDArray inputNDArray = Dl4JUtil.convertListOfLists(in);
        INDArray outPutNDArray = Dl4JUtil.convertListOfLists(outList);
        normalizerIn.transform(inputNDArray);
        normalizerOut.transform(outPutNDArray);
        fitter.fit(inputNDArray, outPutNDArray);
    }

    public List<Double> getOutValue(List<Double> in) {
        return arrayPrimitiveDoublesToList(net.output(
                getInAsNormalized(in, normalizerIn)).toDoubleVector());
    }

    public double getError() {
        return net.gradientAndScore().getSecond();
    }


    private static NetSettings getDefaultNetSettings() {
        return NetSettings.builder()
                .nInput(N_INPUTS).nHiddenLayers(3).nHidden(20).nOutput(N_OUTPUTS)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.SOFTMAX)
                .learningRate(1e-3).momentum(0.95).seed(1234)
                .lossFunction(LossCEM.newWithBeta(1e-1))
                .sizeBatch(8).isNofFitsAbsolute(true).absNoFit(3)
                .build();
    }

    private static NormalizerMinMaxScaler createNormalizerOut() {
        var outMinMax = List.of(Pair.create(0d, NOMINAL_ADVANTAGE));
        return Dl4JUtil.createNormalizer(outMinMax, Pair.create(0d, 1d));
    }

}
