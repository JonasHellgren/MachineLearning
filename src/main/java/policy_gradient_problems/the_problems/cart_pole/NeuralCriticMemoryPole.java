package policy_gradient_problems.the_problems.cart_pole;

import common_dl4j.*;
import org.apache.commons.math3.util.Pair;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import policy_gradient_problems.abstract_classes.StateI;

import java.util.List;
import java.util.Random;

public class NeuralCriticMemoryPole {

    static int NOF_INPUTS = StatePole.newUprightAndStill().asList().size();
    static int NOF_OUTPUTS = 1;

    NetSettings netSettings;  //todo veck
    MultiLayerNetwork net;
    final Random randGen;  //todo veck
    NormalizerMinMaxScaler normalizerIn, normalizerOut;
    Dl4JNetFitter fitter;

    public static NeuralCriticMemoryPole newDefault() {
        return new NeuralCriticMemoryPole(getDefaultNetSettings(),ParametersPole.newDefault());
    }

    public static NetSettings getDefaultNetSettings() {
        return NetSettings.builder()
                .nInput(NOF_INPUTS).nHiddenLayers(3).nHidden(10).nOutput(NOF_OUTPUTS)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.IDENTITY)
                .nofFitsPerEpoch(1).learningRate(1e-3).momentum(0.95).seed(1234)
                .lossFunction(LossFunctions.LossFunction.MSE.getILossFunction())
                .relativeNofFitsPerBatch(0.5)
                .build();
    }

    public NeuralCriticMemoryPole(NetSettings settings, ParametersPole parameters) {
        this.netSettings=settings;
        this.net=MultiLayerNetworkCreator.create(netSettings);
        net.init();
        this.randGen=new Random(settings.seed());
        this.normalizerIn = createNormalizerIn(parameters);
        this.normalizerOut = createNormalizerOut(parameters);
        this.fitter = Dl4JNetFitter.builder()
                .nofInputs(NOF_INPUTS).nofOutputs(NOF_OUTPUTS)
                .net(net).randGen(randGen)
                .normalizerIn(normalizerIn).normalizerOut(normalizerOut)
                .build();
    }


    public void fit(List<List<Double>> in, List<Double> out) {
        fitter.train(in, out);
    }

    public Double getOutValue(INDArray inData) {
        normalizerIn.transform(inData);
        var output = net.output(inData, false);
        normalizerOut.revertFeatures(output);
        return output.getDouble();
    }

    public Double getOutValue(List<Double> inData) {
        var inData1 = Dl4JUtil.convertList(inData, NOF_INPUTS);
        return getOutValue(inData1);
    }

    public Double getOutValue(StateI<VariablesPole> state) {
        var inData1 = Dl4JUtil.convertList(state.asList(), NOF_INPUTS);
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


}
