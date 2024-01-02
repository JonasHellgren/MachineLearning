package policy_gradient_problems.the_problems.short_corridor;

import common_dl4j.*;
import org.apache.commons.math3.util.Pair;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.the_problems.cart_pole.NeuralCriticMemoryPole;
import policy_gradient_problems.the_problems.cart_pole.ParametersPole;

import java.util.List;
import java.util.Random;

import static common.ListUtils.findMax;
import static common.ListUtils.findMin;

public class NeuralCriticMemorySC {

    static int NOF_INPUTS = 1, NOF_OUTPUTS = EnvironmentSC.NOF_ACTIONS;

    MultiLayerNetwork net;
    NormalizerMinMaxScaler normalizerIn, normalizerOut;
    Dl4JNetFitter fitter;

    public static NeuralCriticMemoryPole newDefault() {
        return new NeuralCriticMemoryPole(getDefaultNetSettings(), ParametersPole.newDefault());
    }

    public NeuralCriticMemorySC(NetSettings netSettings) {
        this.net= MultiLayerNetworkCreator.create(netSettings);
        net.init();
        this.normalizerIn = createNormalizerIn();
        this.normalizerOut = createNormalizerOut();
        this.fitter = Dl4JNetFitter.builder()
                .nofInputs(NOF_INPUTS).nofOutputs(NOF_OUTPUTS)
                .net(net).randGen(new Random(netSettings.seed()))
                .normalizerIn(normalizerIn).normalizerOut(normalizerOut)
                .build();
    }


    public void fit(List<List<Double>> in, List<Double> out, int  nofFitsPerEpoch) {
        fitter.train(in, out, nofFitsPerEpoch);
    }

    public Double getOutValue(StateI<VariablesSC> state) {
        var inData1 = Dl4JUtil.convertList(state.asList(), NOF_INPUTS);
        return getOutValue(inData1);
    }

    public double getError() {
        return net.gradientAndScore().getSecond();
    }

    private static NetSettings getDefaultNetSettings() {
        return NetSettings.builder()
                .nInput(NOF_INPUTS).nHiddenLayers(1).nHidden(10).nOutput(1)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.IDENTITY)
                .nofFitsPerEpoch(1).learningRate(1e-3).momentum(0.95).seed(1234)
                .lossFunction(LossFunctions.LossFunction.MSE.getILossFunction())
                .build();
    }

    private Double getOutValue(INDArray inData) {
        normalizerIn.transform(inData);
        var output = net.output(inData, false);
        normalizerOut.revertFeatures(output);
        return output.getDouble();
    }

    private static NormalizerMinMaxScaler createNormalizerIn() {
        List<Double> os = EnvironmentSC.SET_OBSERVABLE_STATES.stream().map(n -> n.doubleValue()).toList();
        var inMinMax = List.of(Pair.create(findMin(os).orElseThrow(),findMax(os).orElseThrow()));
        return Dl4JUtil.createNormalizer(inMinMax, Pair.create(-1d,1d));  //0,1 gives worse performance
    }

    private static NormalizerMinMaxScaler createNormalizerOut() {
        var values=EnvironmentSC.STATE_REWARD_MAP.values().stream().toList();
        var outMinMax = List.of(Pair.create(findMin(values).orElseThrow(), findMax(values).orElseThrow()));
        return Dl4JUtil.createNormalizer(outMinMax);
    }

}
