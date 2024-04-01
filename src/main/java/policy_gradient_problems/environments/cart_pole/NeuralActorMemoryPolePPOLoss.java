package policy_gradient_problems.environments.cart_pole;

import common_dl4j.*;
import org.apache.commons.math3.util.Pair;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import java.util.List;
import static common.ListUtils.arrayPrimitiveDoublesToList;

/***
 * absNoFit - to large -> converges to sub optimal
 * epsPPO - to small - no/slow convergence
 * epsFinDiff  - to small - no/slow convergence
 * absNoFit - slows down computation with little convergence gain
 *
 *
 * Effective reinforcement learning involves balancing exploration (trying new actions to discover their value)
 * and exploitation (using known information to maximize reward). A small ϵ can skew this balance towards exploitation
 * too early, limiting the exploration necessary for finding optimal strategies in
 * environments with nuances like CartPole.
 */

public class NeuralActorMemoryPolePPOLoss {
    static final int N_INPUTS = StatePole.newUprightAndStill(ParametersPole.newDefault()).nofStates();
    static final int N_OUTPUTS = EnvironmentPole.NOF_ACTIONS;

    MultiLayerNetwork net;
    NormalizerMinMaxScaler normalizerIn, normalizerOut;
    Dl4JNetFitter fitter;

    public static NeuralActorMemoryPolePPOLoss newDefault(ParametersPole parametersPole) {
        return new NeuralActorMemoryPolePPOLoss(getDefaultNetSettings(), parametersPole);
    }

    public NeuralActorMemoryPolePPOLoss(NetSettings netSettings, ParametersPole parametersPole) {
        this.net = MultiLayerNetworkCreator.create(netSettings);
        this.normalizerIn = createNormalizerIn(parametersPole);
        this.normalizerOut = createNormalizerOut(parametersPole);
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
        return arrayPrimitiveDoublesToList(net.output(getInAsNormalized(in)).toDoubleVector());
    }

    public double getError() {
        return net.gradientAndScore().getSecond();
    }

    private INDArray getInAsNormalized(List<Double> in) {
        INDArray indArray = Nd4j.create(in);
        normalizerIn.transform(indArray);
        indArray = indArray.reshape(1, N_INPUTS);
        return indArray;
    }

    private static NetSettings getDefaultNetSettings() {
        return NetSettings.builder()
                .nInput(N_INPUTS).nHiddenLayers(2).nHidden(64).nOutput(N_OUTPUTS)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.SOFTMAX)
                .learningRate(1e-4).momentum(0.9).seed(1234)   //1e-3
                .lossFunction(PPOLoss.newWithEpsPPOEpsFinDiff(0.5,1e-5))  //0.9,1e-5
                .sizeBatch(64).isNofFitsAbsolute(true).absNoFit(4)  //relativeNofFitsPerBatch(0.01)
                .build();
    }

    private static NormalizerMinMaxScaler createNormalizerIn(ParametersPole p) {
        List<Pair<Double, Double>> inMinMax = p.minMaxStatePairList();
        return Dl4JUtil.createNormalizer(inMinMax, Pair.create(-1d, 1d));  //0,1 gives worse performance
    }

    private static NormalizerMinMaxScaler createNormalizerOut(ParametersPole p) {
        var outMinMax = List.of(Pair.create(0d, 1d));
        return Dl4JUtil.createNormalizer(outMinMax, Pair.create(0d, 1d));
    }
/***
 *                 .nInput(N_INPUTS).nHiddenLayers(2).nHidden(64).nOutput(N_OUTPUTS)
 *                 .activHiddenLayer(Activation.RELU).activOutLayer(Activation.SOFTMAX)
 *                 .learningRate(1e-4).momentum(0.95).seed(1234)   //1e-3
 *                 .lossFunction(PPOLoss.newWithEpsPPOEpsFinDiff(1e-1,1e-1))  //1e-3,1e-1
 *                 .sizeBatch(8).isNofFitsAbsolute(true).absNoFit(8)  //...3
 *                 .build();
 */


}
