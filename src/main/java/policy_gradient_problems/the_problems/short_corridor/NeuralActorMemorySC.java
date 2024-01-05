package policy_gradient_problems.the_problems.short_corridor;

import common.ListUtils;
import common_dl4j.*;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import java.util.List;

public class NeuralActorMemorySC {

    static int NOF_INPUTS = 3, NOF_OUTPUTS = EnvironmentSC.NOF_ACTIONS;

    MultiLayerNetwork net;

    public static NeuralActorMemorySC newDefault() {
        return new NeuralActorMemorySC(getDefaultNetSettings());
    }

    public NeuralActorMemorySC(NetSettings netSettings) {
        this.net= MultiLayerNetworkCreator.create(netSettings);
        net.init();
    }

    public void fit(List<Double> in, List<Double> out) {
        INDArray indArray = transformToIndArray(in);
        net.fit(indArray, Nd4j.create(out));
    }

    public double[] getOutValue(double[] inData) {
        INDArray indArray = transformToIndArray(ListUtils.arrayPrimitiveDoublesToList(inData));
       return net.output(indArray).toDoubleVector();
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
