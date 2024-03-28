package policy_gradient_problems.environments.cart_pole;

import common.ListUtils;
import common_dl4j.*;
import org.apache.commons.math3.util.Pair;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import policy_gradient_problems.domain.abstract_classes.StateI;
import java.util.List;

public class NeuralCriticMemoryPole {

    static final int NOF_INPUTS = StatePole.newUprightAndStill(ParametersPole.newDefault()).nofStates();
    static final int NOF_OUTPUTS = 1;

    MultiLayerNetwork net;
    NormalizerMinMaxScaler normalizerIn, normalizerOut;
    Dl4JNetFitter fitter;

    public static NeuralCriticMemoryPole newDefault() {
        return new NeuralCriticMemoryPole(getDefaultNetSettings(),ParametersPole.newDefault());
    }

    public static NetSettings getDefaultNetSettings() {
        return NetSettings.builder()
                .nInput(NOF_INPUTS).nHiddenLayers(2).nHidden(64).nOutput(NOF_OUTPUTS)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.IDENTITY)
                .learningRate(1e-4).momentum(0.9).seed(1234)    //1e-3
                .lossFunction(LossFunctions.LossFunction.MSE.getILossFunction())
                .sizeBatch(64).isNofFitsAbsolute(true).absNoFit(16)
                .build();
    }

    public NeuralCriticMemoryPole(NetSettings netSettings, ParametersPole parameters) {
        this.net=MultiLayerNetworkCreator.create(netSettings);
        net.init();
        this.normalizerIn = createNormalizerIn(parameters);
        this.normalizerOut = createNormalizerOut(parameters);
        this.fitter=new Dl4JNetFitter(net,netSettings);
    }

    public void fit(List<List<Double>> in, List<Double> out) {
        INDArray inputNDArray = Dl4JUtil.convertListOfLists(in);
        INDArray outPutNDArray = Nd4j.create(ListUtils.toArray(out), in.size(), NOF_OUTPUTS);
        //      INDArray outPutNDArray = Dl4JUtil.convertListOfLists(List.of(out), NOF_OUTPUTS);
        normalizerIn.transform(inputNDArray);
        normalizerOut.transform(outPutNDArray);
        fitter.fit(inputNDArray,outPutNDArray);
    }

    public Double getOutValue(INDArray inData) {
        normalizerIn.transform(inData);
        var output = net.output(inData, false);
        normalizerOut.revertFeatures(output);
        return output.getDouble();
    }

    public Double getOutValue(List<Double> inData) {
        var inData1 = Dl4JUtil.convertListToOneRow(inData);
        return getOutValue(inData1);
    }

    public Double getOutValue(StateI<VariablesPole> state) {
        var inData1 = Dl4JUtil.convertListToOneRow(state.asList());
        return getOutValue(inData1);
    }

    public double getError() {
        return net.gradientAndScore().getSecond();
    }

    private static NormalizerMinMaxScaler createNormalizerIn(ParametersPole p) {
        var inMinMax = List.of(
                Pair.create(-p.angleMax(), p.angleMax()),
                Pair.create(-p.xMax(), p.xMax()),
                Pair.create(-p.angleDotMax(), p.angleDotMax()),
                Pair.create(-p.xDotMax(), p.xDotMax()));
        return Dl4JUtil.createNormalizer(inMinMax, Pair.create(-1d,1d));  //0,1 gives worse performance
    }

    private static NormalizerMinMaxScaler createNormalizerOut(ParametersPole p) {
        var outMinMax = List.of(Pair.create(0d, p.maxNofSteps()));
        return Dl4JUtil.createNormalizer(outMinMax);
    }

/**
 *               .nInput(NOF_INPUTS).nHiddenLayers(2).nHidden(10).nOutput(NOF_OUTPUTS)
 *                 .activHiddenLayer(Activation.RELU).activOutLayer(Activation.IDENTITY)
 *                 .learningRate(1e-3).momentum(0.95).seed(1234)    //1e-3
 *                 .lossFunction(LossFunctions.LossFunction.MSE.getILossFunction())
 *                 .sizeBatch(64).relativeNofFitsPerBatch(0.5)
 *                 .build();
 *     }
 */

}
